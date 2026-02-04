-- PostgreSQL schema (with JSONB payloads)

CREATE TABLE IF NOT EXISTS shops (
  shop_id BIGSERIAL PRIMARY KEY,
  shop_domain TEXT NOT NULL UNIQUE,
  currency VARCHAR(3) NOT NULL,
  timezone TEXT NOT NULL,
  last_backfill_through TIMESTAMP,
  last_incremental_sync_at TIMESTAMP,
  shopify_access_token_encrypted TEXT,
  shopify_access_token_iv TEXT,
  shopify_scopes TEXT,
  shopify_installed_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS dim_customers (
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  customer_gid TEXT NOT NULL,
  email TEXT,
  email_hash TEXT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  first_order_processed_at TIMESTAMP,
  last_order_processed_at TIMESTAMP,
  PRIMARY KEY (shop_id, customer_gid)
);

CREATE INDEX IF NOT EXISTS idx_dim_customers_shop_email ON dim_customers (shop_id, email);
CREATE INDEX IF NOT EXISTS idx_dim_customers_shop_first_order ON dim_customers (shop_id, first_order_processed_at);

CREATE TABLE IF NOT EXISTS fact_orders (
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  order_gid TEXT NOT NULL,
  order_name TEXT,
  created_at TIMESTAMP,
  processed_at TIMESTAMP,
  updated_at TIMESTAMP,
  cancelled_at TIMESTAMP,
  financial_status TEXT,
  customer_gid TEXT,
  gross_total NUMERIC(14,2),
  gross_tax NUMERIC(14,2),
  gross_shipping NUMERIC(14,2),
  net_sales NUMERIC(14,2),
  is_new_customer BOOLEAN,
  PRIMARY KEY (shop_id, order_gid)
);

CREATE INDEX IF NOT EXISTS idx_fact_orders_shop_processed ON fact_orders (shop_id, processed_at);
CREATE INDEX IF NOT EXISTS idx_fact_orders_shop_updated ON fact_orders (shop_id, updated_at);
CREATE INDEX IF NOT EXISTS idx_fact_orders_shop_customer ON fact_orders (shop_id, customer_gid);

CREATE TABLE IF NOT EXISTS agg_shop_day (
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  date DATE NOT NULL,
  orders_count INT DEFAULT 0 NOT NULL,
  revenue_gross NUMERIC(14,2) DEFAULT 0 NOT NULL,
  net_sales NUMERIC(14,2) DEFAULT 0 NOT NULL,
  tax_total NUMERIC(14,2) DEFAULT 0 NOT NULL,
  shipping_total NUMERIC(14,2) DEFAULT 0 NOT NULL,
  new_orders_count INT DEFAULT 0 NOT NULL,
  returning_orders_count INT DEFAULT 0 NOT NULL,
  new_revenue_gross NUMERIC(14,2) DEFAULT 0 NOT NULL,
  returning_revenue_gross NUMERIC(14,2) DEFAULT 0 NOT NULL,
  PRIMARY KEY (shop_id, date)
);

CREATE INDEX IF NOT EXISTS idx_agg_shop_day_date ON agg_shop_day (date);

CREATE TABLE IF NOT EXISTS agg_customer_ltv (
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  customer_gid TEXT NOT NULL,
  first_order_processed_at TIMESTAMP,
  last_order_processed_at TIMESTAMP,
  lifetime_orders_count INT DEFAULT 0 NOT NULL,
  lifetime_revenue_gross NUMERIC(14,2) DEFAULT 0 NOT NULL,
  lifetime_net_sales NUMERIC(14,2) DEFAULT 0 NOT NULL,
  PRIMARY KEY (shop_id, customer_gid)
);

CREATE INDEX IF NOT EXISTS idx_agg_customer_ltv_shop_first ON agg_customer_ltv (shop_id, first_order_processed_at);

CREATE TABLE IF NOT EXISTS agg_ltv_cohort_month (
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  cohort_month DATE NOT NULL,
  months_since_first_order INT NOT NULL,
  customers_in_cohort INT DEFAULT 0 NOT NULL,
  net_sales NUMERIC(14,2) DEFAULT 0 NOT NULL,
  cumulative_net_sales NUMERIC(14,2) DEFAULT 0 NOT NULL,
  PRIMARY KEY (shop_id, cohort_month, months_since_first_order)
);

CREATE INDEX IF NOT EXISTS idx_agg_ltv_cohort_month_shop ON agg_ltv_cohort_month (shop_id, cohort_month);

CREATE TABLE IF NOT EXISTS dim_channel (
  channel_id BIGSERIAL PRIMARY KEY,
  channel_key TEXT NOT NULL UNIQUE,
  channel_name TEXT NOT NULL,
  channel_type TEXT NOT NULL CHECK (channel_type IN ('paid', 'organic', 'marketplace')),
  status TEXT NOT NULL DEFAULT 'active' CHECK (status IN ('active', 'disabled'))
);

CREATE TABLE IF NOT EXISTS fact_channel_day (
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  channel_id BIGINT NOT NULL REFERENCES dim_channel(channel_id),
  date DATE NOT NULL,
  spend NUMERIC(14,2) DEFAULT 0 NOT NULL,
  impressions BIGINT DEFAULT 0 NOT NULL,
  clicks BIGINT DEFAULT 0 NOT NULL,
  attributed_revenue NUMERIC(14,2) DEFAULT 0 NOT NULL,
  attributed_orders INT DEFAULT 0 NOT NULL,
  PRIMARY KEY (shop_id, channel_id, date)
);

CREATE INDEX IF NOT EXISTS idx_fact_channel_day_shop_date ON fact_channel_day (shop_id, date);

CREATE TABLE IF NOT EXISTS agg_shop_day_with_spend (
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  date DATE NOT NULL,
  revenue_gross NUMERIC(14,2) DEFAULT 0 NOT NULL,
  net_sales NUMERIC(14,2) DEFAULT 0 NOT NULL,
  orders_count INT DEFAULT 0 NOT NULL,
  ad_spend_total NUMERIC(14,2) DEFAULT 0 NOT NULL,
  mer NUMERIC(14,6) DEFAULT 0 NOT NULL,
  PRIMARY KEY (shop_id, date)
);

CREATE INDEX IF NOT EXISTS idx_agg_shop_day_with_spend_date ON agg_shop_day_with_spend (date);

CREATE TABLE IF NOT EXISTS raw_meta_ads (
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  date DATE NOT NULL,
  payload JSONB NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (shop_id, date, created_at)
);

CREATE INDEX IF NOT EXISTS idx_raw_meta_ads_shop_date ON raw_meta_ads (shop_id, date);

CREATE TABLE IF NOT EXISTS raw_google_ads (
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  date DATE NOT NULL,
  payload JSONB NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (shop_id, date, created_at)
);

CREATE INDEX IF NOT EXISTS idx_raw_google_ads_shop_date ON raw_google_ads (shop_id, date);

CREATE TABLE IF NOT EXISTS raw_tiktok_ads (
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  date DATE NOT NULL,
  payload JSONB NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (shop_id, date, created_at)
);

CREATE INDEX IF NOT EXISTS idx_raw_tiktok_ads_shop_date ON raw_tiktok_ads (shop_id, date);

CREATE TABLE IF NOT EXISTS raw_linkedin_ads (
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  date DATE NOT NULL,
  payload JSONB NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (shop_id, date, created_at)
);

CREATE INDEX IF NOT EXISTS idx_raw_linkedin_ads_shop_date ON raw_linkedin_ads (shop_id, date);

CREATE TABLE IF NOT EXISTS raw_reddit_ads (
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  date DATE NOT NULL,
  payload JSONB NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (shop_id, date, created_at)
);

CREATE INDEX IF NOT EXISTS idx_raw_reddit_ads_shop_date ON raw_reddit_ads (shop_id, date);

CREATE TABLE IF NOT EXISTS raw_pinterest_ads (
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  date DATE NOT NULL,
  payload JSONB NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (shop_id, date, created_at)
);

CREATE INDEX IF NOT EXISTS idx_raw_pinterest_ads_shop_date ON raw_pinterest_ads (shop_id, date);

CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  email TEXT NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  primary_shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  role TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS user_shops (
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  shop_id BIGINT NOT NULL REFERENCES shops(shop_id) ON DELETE CASCADE,
  PRIMARY KEY (user_id, shop_id)
);

CREATE TABLE IF NOT EXISTS shop_oauth_state (
  shop_id BIGINT NOT NULL PRIMARY KEY REFERENCES shops(shop_id) ON DELETE CASCADE,
  state TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL
);
