#/bin/sh
export JAVA_HOME=/usr/local/java/jdk1.6.0_18/
export PATH=$PATH:/home/wheel/apache-maven-2.2.1/bin/
export BITROCK_HOME=/home/wheel/installbuilder-6.2.7/
export BIRT_WAR="/usr/local/apache-tomcat-6.0.18/webapps/"
export DHIS2_DOCS="/home/wheel/workspace/dhis2-docbook-docs/"
export DHIS2_SRC="/home/wheel/workspace/dhis2/"
export MAVEN_OPTS="-Xms256m -Xmx512m"


echo "Building DHIS 2 Core..."
cd $DHIS2_SRC/dhis-2
mvn clean install -Dtest=skip -DfailIfNoTests=false
echo "Building DHIS 2 Web..."
cd $DHIS2_SRC/dhis-2/dhis-web
mvn clean install -Dtest=skip -DfailIfNoTests=false
echo "Builidng DHIS2 Live Package"
cd $DHIS2_SRC/dhis-live/
mvn clean package -Dtest=skip -DfailIfNoTests=false
echo "Building documentation"
cd $DHIS2_DOCS
mvn package
echo "Building installer"
cd $DHIS2_SRC/dhis2-live-installer
mvn package
