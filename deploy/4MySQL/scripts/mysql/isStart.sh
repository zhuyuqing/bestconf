resultFile=/opt/mysql/startresults.txt
rm -f ./result.txt
tail -n 1 $resultFile > ./result.txt

while read line
do
      result=`echo $line|awk -F ' ' '{print $3}'|tr -d ' '`

      if [ "$result" == "OK" ];
        then
          echo "ok"
        else
         echo "not ok!"
      fi
done < ./result.txt

