#!/bin/bash
path=/opt/hadoop-2.6.5/stopresults.txt
cd /opt/hadoop-2.6.5/hadoop-2.6.5
sbin/stop-all.sh >& $path && echo ok >> $path

