#!/bin/bash
resultFile=/opt/spark/stopresults.txt
rm -f ./stopresult.txt
tail -n 2 $resultFile > ./stopresult.txt

while read line
do
      if [ "$line" == "ok" ];
        then
          echo "ok"
        else
         echo "not ok!"
      fi
done < ./stopresult.txt

