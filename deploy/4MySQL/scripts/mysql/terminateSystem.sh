#!/bin/bash
path=/opt/mysql/stopresults.txt
rm -f /opt/mysql/stopresults.txt;
touch /opt/mysql/stopresults.txt;
service mysqld stop >> $path
