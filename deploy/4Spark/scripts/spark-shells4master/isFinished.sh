#!/bin/bash
resultFile=/opt/spark/testresults.txt
rm -f ./testresult.txt
tail -n 2 $resultFile > ./testresult.txt

while read line
do
      if [ "$line" == "Run all done!" ];
        then
          echo "ok"
        else
         echo "not ok!"
      fi
done < ./testresult.txt
