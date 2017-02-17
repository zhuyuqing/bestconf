#!/bin/bash
resultFile=/opt/spark/startresults.txt  

rm -f /opt/spark/startresult.txt
tail -n 2 $resultFile > /opt/spark/startresult.txt

while read line
do
      if [ "$line" == "ok" ]; 
        then 
          echo "ok"
        else
         echo "not ok!"
      fi
done < /opt/spark/startresult.txt
/opt/hadoop-2.6.5/hadoop-2.6.5/bin/hadoop dfsadmin -safemode leave

