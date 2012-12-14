#!/bin/bash
# signs the applet jar. No idea why ant won't do this anymore

ant build.client
jarsigner -keystore security/keystore -storepass changeit bin/tgfb.jar utsigningcert
