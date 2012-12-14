TGFB_HOME=~/workspace/TGFMRedux
DEPLOY_DIR=~/Sites/TGFMRedux

# set up variable declarations
rm -r $DEPLOY_DIR
mkdir $DEPLOY_DIR

# copy the necessary jars to the deployment directory
cp -r $TGFB_HOME/lib/client/*.jar $TGFB_HOME/lib/common/*.jar $TGFB_HOME/lib/xmlrpc/*.jar $TGFB_HOME/lib/help/*.jar  $DEPLOY_DIR/ 
cp $TGFB_HOME/www/* $DEPLOY_DIR/

# remove the previous jar
cd $TGFB_HOME/bin
rm tgfb.jar

# copy the config files to be bundled with the jar
cp -r $TGFB_HOME/tgfb.config $TGFB_HOME/bin/org/teragrid/portal/filebrowser/applet/

# copy the trusted ca files to be bundled with the jar
mkdir $TGFB_HOME/bin/etc/
cp -r $TGFB_HOME/etc/certs.jar $TGFB_HOME/bin/etc/

# copy the help files to be bundled with the jar
#cp -r $TGFB_HOME/doc $TGFB_HOME/bin/

# create the new jar
jar -cf tgfb.jar org etc
#jar -cf tgfb.jar org etc doc
echo "Created jar, copying to public directory."

# sign it to run on the remote system with acces previleges
jarsigner -storepass changeit tgfb.jar testapplet

# deploy the jar to the proper place
cp tgfb.jar $DEPLOY_DIR/
echo "Deployment complete."


