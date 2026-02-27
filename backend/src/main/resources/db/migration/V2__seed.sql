-- Seed data (H2 compatible with MERGE syntax)

MERGE INTO shops (shop_domain, currency, timezone)
KEY (shop_domain)
VALUES ('demo.myshopify.com', 'USD', 'America/New_York');

MERGE INTO dim_channel KEY (channel_id) VALUES
  (1, 'meta', 'Meta Ads', 'paid', 'active'),
  (2, 'google', 'Google Ads', 'paid', 'active'),
  (3, 'tiktok', 'TikTok Ads', 'paid', 'disabled'),
  (4, 'linkedin', 'LinkedIn Ads', 'paid', 'disabled'),
  (5, 'reddit', 'Reddit Ads', 'paid', 'disabled'),
  (6, 'pinterest', 'Pinterest Ads', 'paid', 'disabled');

MERGE INTO users (email, password_hash, primary_shop_id, role)
KEY (email)
VALUES ('demo@shop.com', '{noop}Demo1234!', 1, 'ADMIN');

MERGE INTO user_shops (user_id, shop_id) VALUES (1, 1);

MERGE INTO agg_shop_day KEY (shop_id, date) VALUES
  (1, '2026-01-01', 10, 2500.00, 2100.00, 200.00, 200.00, 6, 4, 1500.00, 1000.00),
  (1, '2026-01-02', 12, 3200.00, 2700.00, 250.00, 250.00, 7, 5, 1700.00, 1500.00),
  (1, '2026-01-03', 8, 1900.00, 1600.00, 150.00, 150.00, 3, 5, 700.00, 1200.00);

MERGE INTO agg_shop_day_with_spend KEY (shop_id, date) VALUES
  (1, '2026-01-01', 2500.00, 2100.00, 10, 800.00, 3.125000),
  (1, '2026-01-02', 3200.00, 2700.00, 12, 900.00, 3.555556),
  (1, '2026-01-03', 1900.00, 1600.00, 8, 500.00, 3.800000);

MERGE INTO agg_customer_ltv KEY (shop_id, customer_gid) VALUES
  (1, 'gid://shopify/Customer/1', '2025-11-05 10:00:00', '2026-01-03 11:30:00', 5, 1200.00, 980.00),
  (1, 'gid://shopify/Customer/2', '2025-12-01 12:10:00', '2026-01-02 08:20:00', 3, 900.00, 720.00);

MERGE INTO agg_ltv_cohort_month KEY (shop_id, cohort_month, months_since_first_order) VALUES
  (1, '2025-11-01', 0, 40, 8000.00, 8000.00),
  (1, '2025-11-01', 1, 40, 3000.00, 11000.00),
  (1, '2025-11-01', 2, 40, 2000.00, 13000.00),
  (1, '2025-11-01', 3, 40, 1500.00, 14500.00),
  (1, '2025-12-01', 0, 55, 9000.00, 9000.00),
  (1, '2025-12-01', 1, 55, 3200.00, 12200.00),
  (1, '2025-12-01', 2, 55, 2100.00, 14300.00),
  (1, '2025-12-01', 3, 55, 1700.00, 16000.00);

MERGE INTO fact_channel_day KEY (shop_id, channel_id, date) VALUES
  (1, 1, '2026-01-01', 500.00, 120000, 3200, 18, 1400.00),
  (1, 2, '2026-01-01', 300.00, 90000, 2100, 12, 1100.00),
  (1, 1, '2026-01-02', 550.00, 130000, 3500, 20, 1500.00),
  (1, 2, '2026-01-02', 350.00, 95000, 2300, 14, 1200.00),
  (1, 1, '2026-01-03', 300.00, 80000, 1800, 10, 800.00),
  (1, 2, '2026-01-03', 200.00, 70000, 1600, 8, 700.00);

