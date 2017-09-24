#!/bin/bash
JMETER_HOME=/root/apache-jmeter-3.1
resultFile=$JMETER_HOME/testresults.txt
minval=0
if [  -f "$resultFile" ]; then
	tail -5 $resultFile > ./tempresult
	lastline=""
	while read line
	do
			result=$(echo $line | grep "Tidying up")
			if [ "$result" != "" ];then
				#we can get the result
				result=$(echo $lastline | grep "summary = ")
				if [ "$result" != "" ];then
					#we actually have the result
					throughput=`echo $lastline|awk -F ' ' '{print $7}'|tr -d ' '|tr -d '/s'`
					break
				fi
			fi
			lastline=$line
	done < ./tempresult
	if [ `expr $throughput \> $minval` -eq 1 ]; then
		echo $throughput
	else
		echo "error"
	fi
else
	echo "not exist"
fi
