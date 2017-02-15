#!/bin/bash
path=/opt/hadoopshells/stopresults.txt
rm -f /opt/hadoopshells/stopresults.txt
rm -rf /opt/hadoop-2.6.5/dfs/data/*  &&  rm -rf /opt/hadoop-2.6.5/hadoop-2.6.5/logs/* >& $path
echo ok >> $path

