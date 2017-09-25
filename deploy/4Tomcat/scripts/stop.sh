#!/bin/bash
export CATALINA_HOME=/root/apache-tomcat-8.5.9
path=$CATALINA_HOME/stopresults.txt
cd $CATALINA_HOME
bin/shutdown.sh > $path
kill -9 $(ps -ef|grep catalina |awk '{print $2}')
echo ok >> $path
