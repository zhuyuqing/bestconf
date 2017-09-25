#!/bin/bash
export CATALINA_HOME=/root/apache-tomcat-8.5.9

resultFile=$CATALINA_HOME/startresults.txt
tail -n 1 $resultFile > ./result.txt

while read line
do    
      if [ "$line" == "Tomcat started." ]; 
        then 
          echo "ok"
        else
         echo "not ok!"
      fi
done < ./result.txt

