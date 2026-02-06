DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM pg_attribute a
    JOIN pg_type t ON a.atttypid = t.oid
    WHERE a.attrelid = 'audit_events'::regclass
      AND a.attname = 'action'
      AND t.typname = 'bytea'
  ) THEN
    ALTER TABLE audit_events
      ALTER COLUMN action TYPE TEXT
      USING encode(action, 'escape');
  END IF;

  IF EXISTS (
    SELECT 1
    FROM pg_attribute a
    JOIN pg_type t ON a.atttypid = t.oid
    WHERE a.attrelid = 'audit_events'::regclass
      AND a.attname = 'metadata'
      AND t.typname = 'bytea'
  ) THEN
    ALTER TABLE audit_events
      ALTER COLUMN metadata TYPE TEXT
      USING encode(metadata, 'escape');
  END IF;
END $$;
