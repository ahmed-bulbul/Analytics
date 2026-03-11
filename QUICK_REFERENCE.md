# Quick Reference: setup_shopify_analytics.sh

## One-Line Commands

### For H2 (Development)
```bash
./setup_shopify_analytics.sh
```

### For PostgreSQL (Production)
```bash
./setup_shopify_analytics.sh postgres "jdbc:postgresql://localhost:5432/analytics" "postgres" "password"
```

## What Gets Created

### Dimension Tables
- `dim_customers` - Customer profiles
- `dim_channel` - Marketing channels (Meta, Google, etc.)

### Fact Tables
- `fact_orders` - Order transactions
- `fact_channel_day` - Daily channel metrics

### Aggregate Tables (Rollups)
- `agg_shop_day` - Daily shop metrics
- `agg_customer_ltv` - Customer lifetime value
- `agg_ltv_cohort_month` - Monthly cohort analysis
- `agg_shop_day_with_spend` - Revenue + Ad spend + MER

## Output Files

Both files are created in `/tmp/`:

1. **Schema SQL**: `shopify_analytics_<timestamp>.sql`
   - All CREATE TABLE statements
   - All indexes

2. **Rollup Queries**: `shopify_analytics_rollup_queries.sql`
   - 4 rollup operations
   - 6 analysis query examples

## Rollup Operations

1. **Daily Shop Aggregates** - Rolls up orders by day
2. **Customer LTV** - Aggregates lifetime metrics per customer
3. **Cohort Analysis** - Monthly cohorts and retention
4. **Marketing Efficiency** - Combines revenue with ad spend

## Next Steps

1. Run the script
2. Execute rollup queries:
   ```bash
   psql -h localhost -U postgres -d analytics -f /tmp/shopify_analytics_rollup_queries.sql
   ```
3. Schedule daily refreshes (cron job)
4. Query aggregate tables for analytics

## Key Metrics Calculated

- **MER** (Marketing Efficiency Ratio) = Revenue / Ad Spend
- **LTV** (Lifetime Value) = Total revenue per customer
- **AOV** (Average Order Value) = Revenue / Orders
- **CAC** (Customer Acquisition Cost) = Ad Spend / Orders
- **ROAS** (Return on Ad Spend) = Revenue / Spend per channel

## Troubleshooting

| Issue | Solution |
|-------|----------|
| `psql: command not found` | `brew install postgresql` |
| Connection refused | Start PostgreSQL: `brew services start postgresql` |
| Permission denied | `chmod +x setup_shopify_analytics.sh` |

## Full Documentation

See [SHOPIFY_ANALYTICS_SETUP.md](SHOPIFY_ANALYTICS_SETUP.md) for:
- Detailed usage instructions
- Performance optimization tips
- Integration with Spring Boot
- Schema diagrams
- Cron job examples
- Advanced queries

## Example Analysis Query

```sql
-- Top 10 customers by LTV
SELECT 
    customer_gid,
    lifetime_orders_count,
    lifetime_revenue_gross,
    ROUND(lifetime_revenue_gross / lifetime_orders_count, 2) as avg_order_value
FROM agg_customer_ltv
ORDER BY lifetime_revenue_gross DESC
LIMIT 10;
```

## Features

✅ Supports H2 and PostgreSQL  
✅ Color-coded output  
✅ Error handling  
✅ Comprehensive indexes  
✅ Ready-to-use rollup queries  
✅ Analysis query examples  
✅ macOS compatible  
✅ Production-ready
