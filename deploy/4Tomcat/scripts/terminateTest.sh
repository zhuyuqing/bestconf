#!/bin/bash
export JMETER_HOME=/root/apache-jmeter-3.1
kill -9 $(ps -ef|grep test4bestconf.jmx |awk '{print $2}')
echo ok
