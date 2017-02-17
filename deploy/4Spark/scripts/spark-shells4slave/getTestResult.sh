
resultFile=/opt/HiBench-master/report/hibench.report
minduration=0
i=0
result=0
if [  -f "$resultFile" ]; then
cat $resultFile > /opt/spark/testresult.txt

while read line
do
        if [ -n "$line" ]; then
                duration=`echo $line|awk -F ' ' '{print $5}'|tr -d ' '`
                if [ $duration == "Duration(s)" ];
                   then
                        ((i++))
                else 
                        ((i++))
                        result=`echo "$result + $duration"|bc` 
                        if [ $i == 3 ]; then
                                break
                           
                        fi 
                fi
        fi
done < /opt/spark/testresult.txt
if [ $i == 3 ]; then
        num2=10000
        num3=`echo "sclae=10;$num2/$result"|bc`
        echo $num3
else
        echo "error"
fi
else
   echo "not exist"
fi
