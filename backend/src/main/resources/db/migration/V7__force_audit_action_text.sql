-- Fix audit_events action column forcing to TEXT for H2 compatibility
-- H2 doesn't support PostgreSQL DO blocks, so we skip the conditional type changes
-- These columns should be TEXT by default in H2

