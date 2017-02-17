#!/bin/bash
path=/opt/cassandra/stopresults.txt
rm -f /opt/cassandra/stopresults.txt
touch /opt/cassandra/stopresults.txt
kill -9 $(ps -ef|grep java |awk '{print $2}') >& $path
kill -9 $(ps -ef|grep java |awk '{print $2}') >> $path
kill -9 $(ps -ef|grep java |awk '{print $2}') >> $path
echo ok >> $path
