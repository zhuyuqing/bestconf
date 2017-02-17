resultFile=/opt/mysql/testresults.txt
rm -f ./testresult.txt
tail -n 2 $resultFile > ./testresult.txt

while read line
do
      if [ "$line" == "ok" ];
        then
          echo "ok"
        else
         echo "not ok!"
      fi
done < ./testresult.txt
