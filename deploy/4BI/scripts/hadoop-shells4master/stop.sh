#!/bin/bash
path=/opt/hadoopshells/stopresults.txt
cd /opt/hadoop-2.6.5/hadoop-2.6.5
sbin/stop-all.sh >& $path &&  rm -rf /opt/hadoop-2.6.5/hadoop-2.6.5/logs/* && echo ok >> $path

