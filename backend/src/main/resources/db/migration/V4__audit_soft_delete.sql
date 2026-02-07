ALTER TABLE users ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS deleted_by BIGINT;

ALTER TABLE shops ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE shops ADD COLUMN IF NOT EXISTS deleted_by BIGINT;

CREATE TABLE IF NOT EXISTS audit_events (
  id BIGSERIAL PRIMARY KEY,
  action TEXT NOT NULL,
  actor_user_id BIGINT,
  actor_email TEXT,
  target_user_id BIGINT,
  target_shop_id BIGINT,
  metadata JSONB,
  ip_address TEXT,
  user_agent TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_audit_events_action ON audit_events (action);
CREATE INDEX IF NOT EXISTS idx_audit_events_actor ON audit_events (actor_user_id);
CREATE INDEX IF NOT EXISTS idx_audit_events_target_shop ON audit_events (target_shop_id);
