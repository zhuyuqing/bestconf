#!/bin/bash
resultFile=/opt/cassandra/startresults.txt  
rm -f /opt/cassandra/startresult.txt
tail -n 1 $resultFile > /opt/cassandra/startresult.txt

while read line
do
      target=`echo ${line:14}`
      #duration=`echo $line|awk -F ' ' '{print $6}'|tr -d ' '`
      if [ "$target" == "No gossip backlog; proceeding" ]; 
        then 
          echo "ok"
        else
         echo "not ok!"
      fi
done < /opt/cassandra/startresult.txt
