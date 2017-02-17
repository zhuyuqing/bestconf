#!/bin/bash
path=/opt/cassandra/testresults.txt
rm -f /opt/cassandra/testresults.txt
touch /opt/cassandra/testresults.txt
cd /opt/YCSB
bin/ycsb run  cassandra2-cql -P  workloads/workloada -p hosts=172.16.48.211,172.16.48.41,172.16.48.209 -p cassandra.username=cassandra -p cassandra.password=cassandra  -p cassandra.maxconnections=20 -p cassandra.coreconnections=6 -threads 16 -s >& $path
echo ok >> $path
