# Use a newer version of the official PostgreSQL image as a base
FROM postgres:14

# Set the maintainer label
LABEL maintainer="ricardo.marquez@terrabyteanalytics.com"

# Update the package list and install PostGIS
RUN apt-get update && apt-get install -y postgis postgresql-14-postgis-3

# Clean up the apt cache to reduce image size
RUN apt-get clean && rm -rf /var/lib/apt/lists/*
