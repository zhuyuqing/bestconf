#!/bin/bash
export CATALINA_HOME=/root/apache-tomcat-8.5.9
kill -9 $(ps -ef|grep catalina |awk '{print $2}')
echo ok
