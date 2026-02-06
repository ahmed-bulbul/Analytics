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
      USING convert_from(action, 'UTF8');
  END IF;

  IF EXISTS (
    SELECT 1
    FROM pg_attribute a
    JOIN pg_type t ON a.atttypid = t.oid
    WHERE a.attrelid = 'audit_events'::regclass
      AND a.attname = 'metadata'
      AND t.typname = 'jsonb'
  ) THEN
    ALTER TABLE audit_events
      ALTER COLUMN metadata TYPE TEXT
      USING metadata::text;
  END IF;
END $$;
