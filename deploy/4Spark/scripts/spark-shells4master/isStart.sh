#!/bin/bash
resultFile=/opt/spark/startresults.txt  
#resultFile=./hibench.report
#while :
#do
#for line in `cat  $resultFile`
rm -f /opt/spark/startresult.txt
tail -n 2 $resultFile > /opt/spark/startresult.txt

while read line
do
      #echo $line     #这里可根据实际用途变化
      #target = `echo $line|awk -F ' ' '{print $5}'|tr -d ' '`
      #duration=`echo $line|awk -F ' ' '{print $6}'|tr -d ' '`      
      #echo $duration
     
      if [ "$line" == "ok" ]; 
        then 
          echo "ok"
        else
         echo "not ok!"
      fi
done < /opt/spark/startresult.txt
/opt/hadoop-2.6.5/hadoop-2.6.5/bin/hadoop dfsadmin -safemode leave

