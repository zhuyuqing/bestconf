#!/bin/bash
path=/opt/hadoop-2.6.5/startresults.txt
pathtest="$path""/""startresults.txt"
echo $pathtest 
rm -f /opt/hadoop-2.6.5/startresults.txt;
touch /opt/hadoop-2.6.5/startresults.txt;
cd /opt/hadoop-2.6.5/hadoop-2.6.5;
#sbin/start-all.sh >& $path
sbin/start-all.sh >& $path; cd /opt/hadoop-2.6.5/hadoop-2.6.5; bin/hadoop dfsadmin -safemode leave >> $path; echo ok >> $path 
