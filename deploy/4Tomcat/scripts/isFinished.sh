#!/bin/bash
JMETER_HOME=/root/apache-jmeter-3.1
path=$JMETER_HOME/testresults.txt
tail -n 1 $path > ./testresult.txt

while read line
do
     result=$(echo $line | grep "... end of run")
      if [ "$result" != "" ];
        then
          echo "ok"
        else
         echo "not ok!"
      fi
done < ./testresult.txt

