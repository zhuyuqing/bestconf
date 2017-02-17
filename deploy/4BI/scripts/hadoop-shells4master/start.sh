#!/bin/bash
path=/opt/hadoopshells/startresults.txt
rm -f /opt/hadoopshells/startresults.txt;
touch /opt/hadoopshells/startresults.txt;
cd /opt/hadoop-2.6.5/hadoop-2.6.5
sbin/start-all.sh  >& $path
echo ok >> $path
