CREATE TABLE IF NOT EXISTS shop_rate_limit (
  shop_id BIGINT PRIMARY KEY REFERENCES shops(shop_id) ON DELETE CASCADE,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  capacity INT NOT NULL,
  refill_per_minute INT NOT NULL
);
