#!/bin/bash
export CATALINA_HOME=/root/apache-tomcat-8.5.9

path=$CATALINA_HOME/startresults.txt
rm -f $path
cd $CATALINA_HOME
bin/startup.sh > $path
