# Start with a Java base image
FROM tomcat:9.0.85-jdk21

# Update and install dependencies for wgrib2
RUN apt-get update
RUN apt-get install -y build-essential wget gfortran libpng-dev libjpeg-dev unzip
RUN rm -rf /var/lib/apt/lists/*

# Download and install wgrib2
RUN wget https://ftp.cpc.ncep.noaa.gov/wd51we/wgrib2/wgrib2.tgz -O wgrib2.tgz
RUN tar -xzvf wgrib2.tgz
RUN sed -i 's/USE_AEC=1/USE_AEC=0/' grib2/makefile   # Disable AEC support
RUN sed -i 's/USE_OPENJPEG=1/USE_OPENJPEG=0/' grib2/makefile   # Disable OpenJPEG support
RUN cd grib2 && \
    export CC=gcc && \
    export FC=gfortran && \
    make 

RUN mv grib2/wgrib2 /usr/local/bin && \
    chmod +x /usr/local/bin/wgrib2

	

# Set the working directory to Tomcat's directory
WORKDIR /usr/local/tomcat
COPY terrabyte.war /usr/local/tomcat/webapps/terrabyte.war
COPY titans.goes.jar /usr/local/titans.goes.jar
COPY titans.mrms.jar /usr/local/titans.mrms.jar
COPY titans.nam.jar /usr/local/titans.nam.jar
COPY titans.hrrr.jar /usr/local/titans.hrrr.jar
COPY titans.hrrr.archive.jar /usr/local/titans.hrrr.archive.jar

# Create directories and set permissions
RUN mkdir -p /usr/local/data/netcdf/ && \
    chmod -R 775 /usr/local/data/netcdf/ && \
    mkdir -p /usr/local/data/grib/ && \
    chmod -R 775 /usr/local/data/grib/


# Unpack the WAR file
RUN rm -rf /usr/local/tomcat/webapps/terrabyte && \
    mkdir /usr/local/tomcat/webapps/terrabyte && \
    unzip /usr/local/tomcat/webapps/terrabyte.war -d /usr/local/tomcat/webapps/terrabyte

# Replace the app.properties file
COPY app.properties /usr/local/tomcat/webapps/terrabyte/WEB-INF/app.properties


# Expose the port Tomcat will run on
EXPOSE 8080

# Set JVM options
ENV CATALINA_OPTS="--add-opens java.base/java.net=ALL-UNNAMED"

COPY hrrr.t00z.wrfsfcf00.grib2 /usr/local/data/grib


# Add the entrypoint script
#COPY entrypoint.sh /usr/local/bin/
#RUN chmod +x /usr/local/bin/entrypoint.sh

# Start Tomcat
# ENTRYPOINT ["entrypoint.sh"]
CMD ["./bin/catalina.sh", "run"]
