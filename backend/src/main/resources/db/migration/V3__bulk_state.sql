CREATE TABLE IF NOT EXISTS shop_bulk_state (
  shop_id BIGINT PRIMARY KEY REFERENCES shops(shop_id) ON DELETE CASCADE,
  current_type TEXT,
  operation_id TEXT,
  status TEXT,
  url TEXT,
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
