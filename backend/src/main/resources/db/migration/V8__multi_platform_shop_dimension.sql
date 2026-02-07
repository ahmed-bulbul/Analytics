-- Migration to transform shops table into a proper dimension table
-- with support for multiple e-commerce platforms (Shopify, WooCommerce, Magento)

-- Step 1: Add platform_type and other dimension fields to shops table
ALTER TABLE shops
  ADD COLUMN IF NOT EXISTS platform_type VARCHAR(20) NOT NULL DEFAULT 'shopify',
  ADD COLUMN IF NOT EXISTS platform_shop_id TEXT,
  ADD COLUMN IF NOT EXISTS shop_name TEXT,
  ADD COLUMN IF NOT EXISTS shop_owner_name TEXT,
  ADD COLUMN IF NOT EXISTS shop_owner_email TEXT,
  ADD COLUMN IF NOT EXISTS country_code VARCHAR(2),
  ADD COLUMN IF NOT EXISTS shop_plan_name TEXT,
  ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE;

-- Add constraint for platform_type
ALTER TABLE shops
  DROP CONSTRAINT IF EXISTS shops_platform_type_check;
  
ALTER TABLE shops
  ADD CONSTRAINT shops_platform_type_check 
  CHECK (platform_type IN ('shopify', 'woocommerce', 'magento'));

-- Step 2: Create indexes for better performance on dimension queries
CREATE INDEX IF NOT EXISTS idx_shops_platform_type ON shops (platform_type);
CREATE INDEX IF NOT EXISTS idx_shops_is_active ON shops (is_active);
CREATE INDEX IF NOT EXISTS idx_shops_platform_shop_id ON shops (platform_shop_id);

-- Step 3: Update existing data to include platform information
UPDATE shops 
SET 
  platform_type = 'shopify',
  platform_shop_id = shop_domain,
  shop_name = COALESCE(shop_domain, 'Unknown Shop'),
  is_active = TRUE
WHERE platform_type IS NULL OR platform_type = 'shopify';

-- Step 4: Create a bridge table for platform-specific credentials
-- This separates authentication concerns from the dimension table
CREATE TABLE IF NOT EXISTS shop_platform_credentials (
  credential_id BIGSERIAL PRIMARY KEY,
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  platform_type VARCHAR(20) NOT NULL,
  access_token_encrypted TEXT,
  access_token_iv TEXT,
  refresh_token_encrypted TEXT,
  refresh_token_iv TEXT,
  api_key_encrypted TEXT,
  api_secret_encrypted TEXT,
  scopes TEXT,
  credentials_metadata JSONB,
  installed_at TIMESTAMP,
  expires_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
  UNIQUE(shop_id, platform_type)
);

CREATE INDEX IF NOT EXISTS idx_shop_platform_creds_shop ON shop_platform_credentials (shop_id);
CREATE INDEX IF NOT EXISTS idx_shop_platform_creds_platform ON shop_platform_credentials (platform_type);

-- Step 5: Migrate existing Shopify credentials to new table
INSERT INTO shop_platform_credentials 
  (shop_id, platform_type, access_token_encrypted, access_token_iv, scopes, installed_at)
SELECT 
  shop_id,
  'shopify' as platform_type,
  shopify_access_token_encrypted,
  shopify_access_token_iv,
  shopify_scopes,
  shopify_installed_at
FROM shops
WHERE shopify_access_token_encrypted IS NOT NULL
ON CONFLICT (shop_id, platform_type) DO NOTHING;

-- Step 6: Add comments for documentation
COMMENT ON TABLE shops IS 'Dimension table for e-commerce shops across multiple platforms (Shopify, WooCommerce, Magento)';
COMMENT ON COLUMN shops.platform_type IS 'E-commerce platform: shopify, woocommerce, or magento';
COMMENT ON COLUMN shops.platform_shop_id IS 'Platform-specific shop identifier (e.g., Shopify domain, WooCommerce site URL)';
COMMENT ON TABLE shop_platform_credentials IS 'Platform-specific API credentials and authentication tokens (encrypted at rest)';

-- Step 7: Create view for Doris/analytics integration
-- This view provides a clean bridge between operational data and analytics
CREATE OR REPLACE VIEW v_shop_dimension AS
SELECT 
  s.shop_id,
  s.shop_domain,
  s.platform_type,
  s.platform_shop_id,
  s.shop_name,
  s.shop_owner_name,
  s.shop_owner_email,
  s.currency,
  s.timezone,
  s.country_code,
  s.shop_plan_name,
  s.is_active,
  s.created_at,
  s.updated_at,
  s.deleted_at,
  CASE WHEN s.deleted_at IS NULL THEN TRUE ELSE FALSE END as is_current
FROM shops s;

COMMENT ON VIEW v_shop_dimension IS 'Clean view of shop dimension for analytics/OLAP systems (Doris, etc.)';

-- Step 8: Create platform-agnostic order source view
-- This helps map different platform order structures to a common format
CREATE OR REPLACE VIEW v_order_bridge AS
SELECT 
  fo.shop_id,
  s.platform_type,
  fo.order_gid as source_order_id,
  fo.order_name as order_display_name,
  fo.created_at,
  fo.processed_at,
  fo.updated_at,
  fo.cancelled_at,
  fo.financial_status,
  fo.customer_gid as source_customer_id,
  fo.gross_total,
  fo.gross_tax,
  fo.gross_shipping,
  fo.net_sales,
  fo.is_new_customer
FROM fact_orders fo
JOIN shops s ON fo.shop_id = s.shop_id;

COMMENT ON VIEW v_order_bridge IS 'Bridge view mapping platform-specific order data to common analytics format';

-- Step 9: Create customer dimension view for analytics
CREATE OR REPLACE VIEW v_customer_dimension AS
SELECT 
  dc.shop_id,
  s.platform_type,
  dc.customer_gid as source_customer_id,
  dc.email,
  dc.email_hash,
  dc.created_at,
  dc.updated_at,
  dc.first_order_processed_at,
  dc.last_order_processed_at,
  CASE WHEN dc.email IS NOT NULL THEN TRUE ELSE FALSE END as has_email
FROM dim_customers dc
JOIN shops s ON dc.shop_id = s.shop_id;

COMMENT ON VIEW v_customer_dimension IS 'Customer dimension with platform context for analytics';
