resultFile=/opt/mysql/startresults.txt
#resultFile=./hibench.report
#while :
#do
#for line in `cat  $resultFile`
rm -f ./result.txt
tail -n 1 $resultFile > ./result.txt

while read line
do
      #echo $line     #这里可根据实际用途变化
      #target = `echo $line|awk -F ' ' '{print $5}'|tr -d ' '`
      #duration=`echo $line|awk -F ' ' '{print $6}'|tr -d ' '`      
      #echo $duration
      result=`echo $line|awk -F ' ' '{print $3}'|tr -d ' '`

      if [ "$result" == "OK" ];
        then
          echo "ok"
        else
         echo "not ok!"
      fi
done < ./result.txt

