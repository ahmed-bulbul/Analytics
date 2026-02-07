# Shopify Analytics Tables Setup Guide

This guide explains how to use the `setup_shopify_analytics.sh` script to create dimension tables, fact tables, and rollup operations for Shopify analytics.

## Overview

The script provides a comprehensive database schema for Shopify e-commerce analytics, including:

### 📊 Dimension Tables
- **dim_customers**: Customer information for analytics
- **dim_channel**: Marketing channel information (Meta, Google, TikTok, etc.)

### 📈 Fact Tables
- **fact_orders**: Individual order transactions with detailed metrics
- **fact_channel_day**: Daily marketing channel performance metrics

### 🎯 Aggregate/Rollup Tables
- **agg_shop_day**: Daily shop-level metrics rollup
- **agg_customer_ltv**: Customer lifetime value calculations
- **agg_ltv_cohort_month**: Monthly cohort analysis for LTV tracking
- **agg_shop_day_with_spend**: Daily metrics combined with ad spend and MER

### 💾 Raw Payload Tables
- **raw_meta_ads**: Raw Meta/Facebook Ads data (JSONB/CLOB)
- **raw_google_ads**: Raw Google Ads data (JSONB/CLOB)

## Prerequisites

### For H2 (Development)
- Java 17+
- Spring Boot application (already configured in this project)

### For PostgreSQL (Production)
- PostgreSQL 12+ installed
- `psql` command-line tool
- Access credentials to your PostgreSQL database

Install PostgreSQL on macOS:
```bash
brew install postgresql
```

## Usage

### Basic Usage (H2)

For local development with H2 in-memory database:

```bash
./setup_shopify_analytics.sh
```

or explicitly:

```bash
./setup_shopify_analytics.sh h2
```

This generates SQL files that will be used by the Spring Boot application automatically.

### PostgreSQL Usage

For production PostgreSQL database:

```bash
./setup_shopify_analytics.sh postgres \
  "jdbc:postgresql://localhost:5432/analytics" \
  "postgres" \
  "your_password"
```

Replace the connection details with your actual PostgreSQL configuration:
- **Host**: Default is `localhost`, change if remote
- **Port**: Default is `5432`
- **Database**: Name of your database
- **Username**: Your PostgreSQL username
- **Password**: Your PostgreSQL password

## Script Output

The script generates two important SQL files in `/tmp/`:

1. **Schema SQL** (`/tmp/shopify_analytics_<timestamp>.sql`)
   - Contains all CREATE TABLE statements
   - Includes indexes for performance optimization
   - Supports both H2 and PostgreSQL syntax

2. **Rollup Queries SQL** (`/tmp/shopify_analytics_rollup_queries.sql`)
   - Contains INSERT queries to populate aggregate tables
   - Includes analysis query examples
   - Ready to be scheduled for regular execution

## Rollup Operations

The script includes four main rollup operations:

### 1. Daily Shop Aggregates
Rolls up individual orders into daily summaries:
- Total orders count
- Revenue and net sales
- New vs. returning customer metrics
- Tax and shipping totals

### 2. Customer Lifetime Value (LTV)
Aggregates all orders per customer:
- First and last order dates
- Total number of orders
- Lifetime revenue and net sales

### 3. Cohort Analysis
Groups customers by first order month and tracks spending over time:
- Monthly cohorts
- Revenue by months since first order
- Cumulative sales tracking

### 4. Marketing Efficiency
Combines order data with ad spend:
- Daily revenue and orders
- Total ad spend across all channels
- Marketing Efficiency Ratio (MER = Revenue / Ad Spend)

## Example Workflows

### Initial Setup

1. Run the script to create tables:
```bash
./setup_shopify_analytics.sh postgres \
  "jdbc:postgresql://localhost:5432/analytics" \
  "postgres" \
  "password"
```

2. Populate dimension tables:
```sql
-- Insert marketing channels
INSERT INTO dim_channel (channel_key, channel_name, channel_type, status) VALUES
  ('meta', 'Meta Ads', 'paid', 'active'),
  ('google', 'Google Ads', 'paid', 'active'),
  ('tiktok', 'TikTok Ads', 'paid', 'active');
```

3. Import order data into `fact_orders`

4. Run rollup queries to populate aggregates:
```bash
psql -h localhost -U postgres -d analytics -f /tmp/shopify_analytics_rollup_queries.sql
```

### Daily Refresh (Scheduled Job)

Create a cron job to refresh aggregate tables daily:

```bash
# Edit crontab
crontab -e

# Add this line to run at 2 AM daily
0 2 * * * /path/to/shopify_analytics_rollup_queries.sql | psql -h localhost -U postgres -d analytics
```

Or use a more robust approach with error logging:

```bash
0 2 * * * PGPASSWORD=your_password psql -h localhost -U postgres -d analytics \
  -f /path/to/shopify_analytics_rollup_queries.sql \
  >> /var/log/shopify_rollup.log 2>&1
```

## Analysis Queries

The rollup queries file includes ready-to-use analysis queries:

### Weekly Revenue
```sql
SELECT 
    shop_id,
    DATE_TRUNC('week', date) as week,
    SUM(orders_count) as weekly_orders,
    SUM(revenue_gross) as weekly_revenue
FROM agg_shop_day
GROUP BY shop_id, DATE_TRUNC('week', date)
ORDER BY shop_id, week DESC;
```

### Top Customers by LTV
```sql
SELECT 
    customer_gid,
    lifetime_orders_count,
    lifetime_revenue_gross,
    ROUND(lifetime_revenue_gross / lifetime_orders_count, 2) as avg_order_value
FROM agg_customer_ltv
ORDER BY lifetime_revenue_gross DESC
LIMIT 10;
```

### Marketing Channel Performance
```sql
SELECT 
    c.channel_name,
    SUM(fcd.spend) as total_spend,
    SUM(fcd.attributed_revenue) as total_revenue,
    ROUND(SUM(fcd.attributed_revenue) / NULLIF(SUM(fcd.spend), 0), 2) as roas
FROM fact_channel_day fcd
INNER JOIN dim_channel c ON fcd.channel_id = c.channel_id
WHERE fcd.date >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY c.channel_name
ORDER BY total_revenue DESC;
```

## Performance Optimization

### Indexes
The script automatically creates indexes on:
- Foreign keys for join performance
- Date columns for time-based queries
- Customer identifiers for LTV queries
- Email fields for customer lookups

### Partitioning (PostgreSQL)
For large datasets, consider partitioning fact tables by date:

```sql
-- Example: Partition fact_orders by month
CREATE TABLE fact_orders_y2024m01 PARTITION OF fact_orders
FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');
```

### Materialized Views (PostgreSQL)
For complex aggregations, use materialized views:

```sql
CREATE MATERIALIZED VIEW mv_monthly_metrics AS
SELECT 
    shop_id,
    DATE_TRUNC('month', date) as month,
    SUM(orders_count) as monthly_orders,
    SUM(revenue_gross) as monthly_revenue
FROM agg_shop_day
GROUP BY shop_id, DATE_TRUNC('month', date);

-- Refresh daily
REFRESH MATERIALIZED VIEW mv_monthly_metrics;
```

## Troubleshooting

### Issue: psql command not found
**Solution**: Install PostgreSQL client tools
```bash
brew install postgresql
```

### Issue: Connection refused
**Solution**: Ensure PostgreSQL is running
```bash
brew services start postgresql
# or
pg_ctl -D /usr/local/var/postgres start
```

### Issue: Permission denied
**Solution**: Add execute permissions to the script
```bash
chmod +x setup_shopify_analytics.sh
```

### Issue: Duplicate key errors in rollups
**Solution**: The queries use `ON CONFLICT DO UPDATE` which handles duplicates. Ensure your PostgreSQL version is 9.5+.

## Integration with Spring Boot

The Spring Boot application in this repository is already configured to use these tables:

1. **H2 Development**: Schema is loaded automatically from `backend/src/main/resources/schema.sql`
2. **PostgreSQL Production**: Use Flyway migrations in `backend/src/main/resources/db/migration/`

To run the application:

```bash
cd backend
mvn spring-boot:run
```

Access H2 Console at: http://localhost:8080/h2
- JDBC URL: `jdbc:h2:mem:analytics`
- Username: `sa`
- Password: (leave empty)

## Schema Diagram

```
┌─────────────────┐
│     shops       │
└────────┬────────┘
         │
         ├──────────┬──────────┬──────────┬──────────┐
         │          │          │          │          │
    ┌────▼────┐ ┌──▼──────┐ ┌─▼────┐ ┌──▼─────┐ ┌──▼─────────┐
    │dim_     │ │fact_    │ │fact_ │ │agg_    │ │agg_        │
    │customers│ │orders   │ │channel│ │shop_day│ │customer_ltv│
    └─────────┘ └─────────┘ │_day  │ └────────┘ └────────────┘
                             └──┬───┘
                                │
                         ┌──────▼──────┐
                         │dim_channel  │
                         └─────────────┘
```

## Data Flow

1. **Extract**: Raw data from Shopify API → `fact_orders`, `dim_customers`
2. **Transform**: Marketing data from ad platforms → `fact_channel_day`
3. **Load**: Raw payloads → `raw_meta_ads`, `raw_google_ads`
4. **Aggregate**: Rollup operations → `agg_*` tables
5. **Analyze**: Query aggregate tables for insights

## Best Practices

1. **Regular Rollups**: Schedule rollup queries to run daily or hourly
2. **Incremental Updates**: Use date filters to only process new data
3. **Monitor Performance**: Add indexes based on query patterns
4. **Archive Old Data**: Move old raw data to cold storage
5. **Test First**: Always test rollup queries on a subset before full run
6. **Backup**: Regular backups of aggregate tables

## Support

For issues or questions:
1. Check the generated SQL files in `/tmp/`
2. Review PostgreSQL logs: `tail -f /usr/local/var/log/postgres.log`
3. Test queries individually before running full rollups
4. Ensure table permissions are correct

## License

This script is part of the Analytics project. See main project LICENSE for details.
