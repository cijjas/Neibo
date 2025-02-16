# Use Eclipse Temurin Java 8 image
FROM eclipse-temurin:8-jre

# Set environment variables for Tomcat
ENV TOMCAT_VERSION=9.0.98
ENV CATALINA_HOME=/opt/tomcat

# Create Tomcat directory
RUN mkdir -p $CATALINA_HOME

# Download and extract Apache Tomcat
RUN curl -fSL https://archive.apache.org/dist/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz \
    | tar xz --strip-components=1 -C $CATALINA_HOME \

# Expose the Tomcat default port
EXPOSE 8080

# Copy the WAR file and rename it
COPY /webapp/target/webapp.war $CATALINA_HOME/webapps/paw-2023b-02.war

# Set the working directory and start Tomcat
WORKDIR $CATALINA_HOME
CMD ["bin/catalina.sh", "run"]
