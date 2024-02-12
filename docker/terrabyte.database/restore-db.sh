#!/bin/bash
# restore-db.sh

set -e


echo "Creating PostGIS extension."
psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "CREATE EXTENSION IF NOT EXISTS postgis;"
psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "CREATE EXTENSION IF NOT EXISTS postgis_topology;"


echo "Restoring PostgreSQL data from dump file."

psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" < /docker-entrypoint-initdb.d/restore/dumpfile.sql
psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" < /docker-entrypoint-initdb.d/restore/dumpfile.data.sql

echo "Restore completed."
