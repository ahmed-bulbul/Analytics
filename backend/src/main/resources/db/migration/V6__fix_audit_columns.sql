-- Fix audit_events columns for H2 compatibility
-- H2 doesn't support PostgreSQL DO blocks, so we skip the conditional type changes
-- These columns should be TEXT by default in H2

