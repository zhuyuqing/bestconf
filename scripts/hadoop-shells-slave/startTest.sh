#!/bin/bash
path=/opt/hadoopshells/testresults.txt
rm -f /opt/HiBench-master/report/hibench.report
rm -f /opt/hadoopshells/testresults.txt
touch /opt/hadoopshells/testresults.txt
cd /opt/HiBench-master
bin/run-all.sh >& $path

