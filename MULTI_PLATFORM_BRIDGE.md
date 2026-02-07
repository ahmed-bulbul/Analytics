# Multi-Platform E-Commerce Analytics Bridge

## Overview

This document describes the data bridge between various e-commerce platform APIs (Shopify, WooCommerce, Magento) and the analytics database dimensional model designed for OLAP systems like Apache Doris.

## Architecture

### Multi-Tenant SaaS Structure

```
User (Multi-Tenant)
  └─ Multiple Shops (Dimension)
       ├─ Shopify Stores
       ├─ WooCommerce Stores
       └─ Magento Stores
```

Each shop is now a **dimension table** (`shops`/`dim_shop`) with platform-specific attributes, supporting multi-platform analytics in a unified star schema.

## Dimensional Model

### 1. Dimension Tables

#### **DIM_SHOP** (shops table)
The shop dimension supports multiple e-commerce platforms as a unified dimension.

| Column | Type | Description | Source |
|--------|------|-------------|--------|
| shop_id | BIGINT | Primary key | Auto-generated |
| shop_domain | TEXT | Unique shop identifier | Shopify: myshopify.com domain<br/>WooCommerce: Site URL<br/>Magento: Store domain |
| platform_type | VARCHAR(20) | Platform identifier | 'shopify', 'woocommerce', 'magento' |
| platform_shop_id | TEXT | Platform-specific ID | Platform-specific identifier |
| shop_name | TEXT | Display name | Shop name from platform |
| shop_owner_name | TEXT | Owner name | Shop owner information |
| shop_owner_email | TEXT | Owner email | Contact email |
| currency | VARCHAR(3) | Shop currency | ISO 4217 currency code |
| timezone | TEXT | Shop timezone | IANA timezone |
| country_code | VARCHAR(2) | Country | ISO 3166-1 alpha-2 |
| shop_plan_name | TEXT | Subscription plan | Platform plan/tier |
| is_active | BOOLEAN | Active status | Soft delete flag |
| created_at | TIMESTAMP | Creation time | Record creation |
| updated_at | TIMESTAMP | Last update | Record update |

**Platform Mapping:**
- **Shopify**: Use `shop.myshopify_domain` as shop_domain
- **WooCommerce**: Use site URL as shop_domain
- **Magento**: Use store domain as shop_domain

#### **DIM_CUSTOMER** (dim_customers table)
Customer dimension with platform context.

| Column | Type | Description | Platform Source |
|--------|------|-------------|-----------------|
| shop_id | BIGINT | FK to shops | Platform shop reference |
| customer_gid | TEXT | Platform customer ID | Shopify: `gid://shopify/Customer/{id}`<br/>WooCommerce: Customer ID<br/>Magento: Customer entity_id |
| email | TEXT | Customer email | Customer email address |
| email_hash | TEXT | Hashed email | SHA-256 hash for privacy |
| created_at | TIMESTAMP | Customer created | Platform customer creation |
| updated_at | TIMESTAMP | Customer updated | Last update timestamp |
| first_order_processed_at | TIMESTAMP | First order date | Calculated from orders |
| last_order_processed_at | TIMESTAMP | Last order date | Calculated from orders |

**Platform Mapping:**
- **Shopify GraphQL**: Use `customer.id` (GID format)
- **WooCommerce REST API**: Use `customer.id`
- **Magento REST API**: Use `customer.id`

#### **DIM_CHANNEL** (dim_channel table)
Marketing channel dimension (platform-agnostic).

| Column | Type | Description |
|--------|------|-------------|
| channel_id | BIGINT | Primary key |
| channel_key | TEXT | Unique key (e.g., 'meta', 'google') |
| channel_name | TEXT | Display name |
| channel_type | TEXT | Type: 'paid', 'organic', 'marketplace' |
| status | TEXT | Status: 'active', 'disabled' |

### 2. Fact Tables

#### **FACT_ORDERS** (fact_orders table)
Order transactions fact table.

| Column | Type | Description | Platform Source |
|--------|------|-------------|-----------------|
| shop_id | BIGINT | FK to shops | Shop dimension |
| order_gid | TEXT | Platform order ID | Platform-specific order ID |
| order_name | TEXT | Order display name | Order number/name |
| created_at | TIMESTAMP | Order creation | Order created timestamp |
| processed_at | TIMESTAMP | Order processed | Payment processed time |
| updated_at | TIMESTAMP | Last update | Order last updated |
| cancelled_at | TIMESTAMP | Cancellation time | If order cancelled |
| financial_status | TEXT | Payment status | Payment/financial status |
| customer_gid | TEXT | FK to dim_customers | Customer reference |
| gross_total | NUMERIC | Gross order total | Total including all fees |
| gross_tax | NUMERIC | Tax amount | Total tax |
| gross_shipping | NUMERIC | Shipping cost | Shipping fees |
| net_sales | NUMERIC | Net sales | Revenue after returns |
| is_new_customer | BOOLEAN | New customer flag | First-time customer |

**Shopify API Mapping:**
```graphql
# Shopify GraphQL Query
query {
  orders {
    id -> order_gid
    name -> order_name
    createdAt -> created_at
    processedAt -> processed_at
    updatedAt -> updated_at
    cancelledAt -> cancelled_at
    displayFinancialStatus -> financial_status
    customer { id } -> customer_gid
    totalPriceSet { shopMoney { amount } } -> gross_total
    totalTaxSet { shopMoney { amount } } -> gross_tax
    totalShippingPriceSet { shopMoney { amount } } -> gross_shipping
    netPaymentSet { shopMoney { amount } } -> net_sales
  }
}
```

**WooCommerce REST API Mapping:**
```json
// GET /wp-json/wc/v3/orders
{
  "id": -> order_gid (convert to string)
  "number": -> order_name
  "date_created": -> created_at
  "date_paid": -> processed_at (if exists, else created_at)
  "date_modified": -> updated_at
  "status": -> map to financial_status
  "customer_id": -> customer_gid
  "total": -> gross_total
  "total_tax": -> gross_tax
  "shipping_total": -> gross_shipping
  "total": -> net_sales (WooCommerce includes refunds)
}
```

**Magento REST API Mapping:**
```json
// GET /rest/V1/orders
{
  "entity_id": -> order_gid
  "increment_id": -> order_name
  "created_at": -> created_at
  "updated_at": -> updated_at
  "status": -> financial_status
  "customer_id": -> customer_gid
  "grand_total": -> gross_total
  "tax_amount": -> gross_tax
  "shipping_amount": -> gross_shipping
  "subtotal": -> net_sales
}
```

#### **FACT_CHANNEL_DAY** (fact_channel_day table)
Daily marketing channel performance.

| Column | Type | Description |
|--------|------|-------------|
| shop_id | BIGINT | FK to shops |
| channel_id | BIGINT | FK to dim_channel |
| date | DATE | Performance date |
| spend | NUMERIC | Ad spend |
| impressions | BIGINT | Impressions count |
| clicks | BIGINT | Clicks count |
| attributed_revenue | NUMERIC | Attributed revenue |
| attributed_orders | INT | Attributed orders |

### 3. Aggregation Tables

#### **AGG_SHOP_DAY** (agg_shop_day table)
Pre-aggregated daily shop metrics for fast queries.

| Column | Type | Description |
|--------|------|-------------|
| shop_id | BIGINT | FK to shops |
| date | DATE | Aggregation date |
| orders_count | INT | Total orders |
| revenue_gross | NUMERIC | Gross revenue |
| net_sales | NUMERIC | Net sales |
| tax_total | NUMERIC | Total tax |
| shipping_total | NUMERIC | Total shipping |
| new_orders_count | INT | New customer orders |
| returning_orders_count | INT | Returning customer orders |
| new_revenue_gross | NUMERIC | Revenue from new customers |
| returning_revenue_gross | NUMERIC | Revenue from returning |

## Platform Integration Layer

### Service Architecture

```
┌─────────────────────────────────────────────┐
│         Multi-Platform Service Layer        │
├─────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐        │
│  │   Shopify    │  │ WooCommerce  │        │
│  │   Service    │  │   Service    │        │
│  └──────┬───────┘  └──────┬───────┘        │
│         │                 │                 │
│         └─────────┬───────┘                 │
│                   │                         │
│         ┌─────────▼─────────┐              │
│         │  Platform Bridge  │              │
│         │     Mapper        │              │
│         └─────────┬─────────┘              │
│                   │                         │
│         ┌─────────▼─────────┐              │
│         │  Analytics Store  │              │
│         │  (PostgreSQL/     │              │
│         │   Doris Bridge)   │              │
│         └───────────────────┘              │
└─────────────────────────────────────────────┘
```

### Data Flow

1. **Extract**: Platform-specific API clients fetch data
2. **Transform**: Platform bridge mappers convert to common schema
3. **Load**: Data written to PostgreSQL operational store
4. **Bridge**: Views expose data for OLAP systems (Doris)

### Authentication Storage

Platform-specific credentials are stored separately in `shop_platform_credentials`:

```sql
CREATE TABLE shop_platform_credentials (
  credential_id BIGSERIAL PRIMARY KEY,
  shop_id BIGINT REFERENCES shops(shop_id),
  platform_type VARCHAR(20),
  access_token_encrypted TEXT,
  access_token_iv TEXT,
  refresh_token_encrypted TEXT,
  scopes TEXT,
  installed_at TIMESTAMP,
  UNIQUE(shop_id, platform_type)
);
```

## Views for OLAP Integration (Doris Bridge)

### v_shop_dimension
Clean shop dimension view for analytics systems.

```sql
CREATE OR REPLACE VIEW v_shop_dimension AS
SELECT 
  shop_id,
  shop_domain,
  platform_type,
  platform_shop_id,
  shop_name,
  currency,
  timezone,
  country_code,
  is_active,
  created_at,
  updated_at
FROM shops
WHERE deleted_at IS NULL;
```

### v_order_bridge
Platform-agnostic order view.

```sql
CREATE OR REPLACE VIEW v_order_bridge AS
SELECT 
  fo.shop_id,
  s.platform_type,
  fo.order_gid as source_order_id,
  fo.order_name as order_display_name,
  fo.processed_at,
  fo.net_sales,
  fo.customer_gid as source_customer_id
FROM fact_orders fo
JOIN shops s ON fo.shop_id = s.shop_id
WHERE s.deleted_at IS NULL;
```

### v_customer_dimension
Platform-agnostic customer view.

```sql
CREATE OR REPLACE VIEW v_customer_dimension AS
SELECT 
  dc.shop_id,
  s.platform_type,
  dc.customer_gid as source_customer_id,
  dc.email,
  dc.first_order_processed_at,
  dc.last_order_processed_at
FROM dim_customers dc
JOIN shops s ON dc.shop_id = s.shop_id
WHERE s.deleted_at IS NULL;
```

## Doris Integration Strategy

### Option 1: Batch ETL
Use Flyway or scheduled jobs to sync data from PostgreSQL views to Doris:

```sql
-- Doris table creation
CREATE TABLE dim_shop (
  shop_id BIGINT,
  platform_type VARCHAR(20),
  shop_name STRING,
  currency VARCHAR(3),
  timezone STRING,
  is_active BOOLEAN
)
DUPLICATE KEY(shop_id)
DISTRIBUTED BY HASH(shop_id) BUCKETS 10;

-- Load from PostgreSQL view
INSERT INTO dim_shop
SELECT * FROM postgresql_catalog.v_shop_dimension;
```

### Option 2: Real-time CDC
Use Debezium or Flink CDC to stream changes from PostgreSQL to Doris in real-time.

### Option 3: Catalog Federation
Use Doris's catalog federation to query PostgreSQL views directly:

```sql
CREATE CATALOG postgres_catalog PROPERTIES (
  "type" = "jdbc",
  "jdbc_url" = "jdbc:postgresql://localhost:5432/analytics",
  "driver_class" = "org.postgresql.Driver"
);

-- Query directly
SELECT * FROM postgres_catalog.analytics.v_shop_dimension;
```

## API Endpoints for Analytics

### Get Shop Dimension
```
GET /api/analytics/shops?platformType=shopify&isActive=true
Response: ShopDimensionDto[]
```

### Get Orders with Platform Context
```
GET /api/analytics/orders?shopId=1&from=2026-01-01&to=2026-01-31
Response: OrderBridgeDto[]
```

### Get Customers with Platform Context
```
GET /api/analytics/customers?shopId=1
Response: CustomerDimensionDto[]
```

## Best Practices

1. **Data Consistency**: Use database transactions when writing multi-table updates
2. **Platform Abstraction**: Always use the bridge DTOs for cross-platform queries
3. **Credential Security**: Keep credentials encrypted in `shop_platform_credentials`
4. **Dimension Changes**: Track dimension changes with SCD Type 2 if needed
5. **Performance**: Use materialized views for complex aggregations in Doris

## Migration Path

1. Existing Shopify data migrates automatically via V8 migration
2. New platforms add rows with appropriate `platform_type`
3. Credentials migrate to `shop_platform_credentials` table
4. Views expose unified interface for all platforms
5. OLAP system (Doris) queries views or replicates data
