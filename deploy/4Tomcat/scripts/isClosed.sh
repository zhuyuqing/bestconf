#!/bin/bash
export CATALINA_HOME=/root/apache-tomcat-8.5.9
ps -ef|grep catalina |awk '{print $2}' > ./stopresult.txt

i=0
while read line
do
	((i=i+1))
done < ./stopresult.txt

if [ $i -eq 1 ]; then
	echo "ok"
else
	echo "not ok!"
fi
