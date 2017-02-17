#!/bin/bash
path=/opt/cassandra/startresults.txt
rm -f /opt/cassandra/startresults.txt;
touch /opt/cassandra/startresults.txt;
rm -rf /opt/huawei/sds/apache-cassandra-2.1.12/data && mv /opt/huawei/sds/apache-cassandra-2.1.12/databackup /opt/huawei/sds/apache-cassandra-2.1.12/data
cd /opt/huawei/sds/apache-cassandra-2.1.12
bin/cassandra >> $path
cp -r /opt/cassandra/databackup  /opt/huawei/sds/apache-cassandra-2.1.12 && echo ok
