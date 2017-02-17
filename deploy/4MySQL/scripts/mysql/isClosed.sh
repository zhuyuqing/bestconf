resultFile=/opt/mysql/stopresults.txt
rm -f ./stopresult.txt
tail -n 1 $resultFile > ./stopresult.txt

while read line
do
      result=`echo $line|awk -F ' ' '{print $4}'|tr -d ' '`

      if [ "$result" == "OK" ];
        then
          echo "ok"
        else
         echo "not ok!"
      fi
done < ./stopresult.txt
echo "ok"

