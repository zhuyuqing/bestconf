#!/bin/bash
resultFile=/opt/cassandra/stopresults.txt
rm -f /opt/cassandra/stopresult.txt
tail -n 1 $resultFile > /opt/cassandra/stopresult.txt

while read line
do

      if [ "$line" == "ok" ];
        then
          echo "ok"
        else
         echo "not ok!"
      fi
done < /opt/cassandra/stopresult.txt

