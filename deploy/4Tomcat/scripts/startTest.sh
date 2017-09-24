#!/bin/bash
JMETER_HOME=/root/apache-jmeter-3.1
path=$JMETER_HOME/testresults.txt
cd $JMETER_HOME/bin
nohup ./jmeter -n -t test4bestconf.jmx > $path &
