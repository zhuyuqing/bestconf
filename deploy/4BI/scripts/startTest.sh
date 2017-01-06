#!/bin/bash
path=/opt/hadoop-2.6.5/testresults.txt
rm -f /opt/HiBench-master/report/hibench.report
rm -f /opt/hadoop-2.6.5/testresults.txt
touch /opt/hadoop-2.6.5/testresults.txt
cd /opt/HiBench-master
bin/run-all.sh >& $path

