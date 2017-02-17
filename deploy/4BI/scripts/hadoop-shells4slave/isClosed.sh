#!/bin/bash
resultFile=/opt/hadoopshells/stopresults.txt
#resultFile=./hibench.report
#while :
#do
#for line in `cat  $resultFile`
rm -f ./stopresult.txt
tail -n 2 $resultFile > ./stopresult.txt

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
done < ./stopresult.txt

