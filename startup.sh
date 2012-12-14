#! /bin/bash

# Establish the distribution directory
DIST=`pwd`/build

# Build the service. This puts jar in bin directory
ant build.server

# Define the classpath.  We shouldn't need the first 2 files since they're in the jar.
cp=hibernate.cfg.xml:sggc.properties:bin/tgfb.jar:.


for i in `ls lib/*/*.jar lib/*/*/*.jar`; 
do 
        cp=`pwd`/$i:$cp; 
done; 

#echo $cp

if [ -f logs ]; then
   mkdir logs
fi 


# Start the server
java -classpath $cp -Djava.awt.headless=true -Djdbc.drivers=org.postgresql.Driver -Dlog4j.debug=true -Dlog4j.configuration=tgfm-log4j.properties org.teragrid.portal.filebrowser.server.servlet.TGFileTransfer &

# Record the process id
echo $! > cap.PID
