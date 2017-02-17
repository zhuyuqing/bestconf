
resultFile=/opt/cassandra/testresults.txt

minduration=0
i=0
result=0
runtime=0
if [  -f "$resultFile" ]; then
cat $resultFile > /opt/cassandra/testresultofycsb.txt

while read line
do
        if [ -n "$line" ]; then
                label=`echo $line|awk -F ',' '{print $1}'|tr -d ' '`
                status=`echo $line|awk -F ',' '{print $2}'|tr -d ' '`
                if [ "$label" == "[UPDATE]" -a "$status" == "Return=OK" ];
                   then
                        ((i++))
                        update=`echo $line|awk -F ',' '{print $3}'|tr -d ' '`
                        result=`echo "$result + $update"|bc` 
                        if [ $i == 3 ]; then
                                break
                           
                        fi 
                fi
                if [ "$label" == "[READ]" -a "$status" == "Return=OK" ];
                   then 
                        ((i++))
                        readvalue=`echo $line|awk -F ',' '{print $3}'|tr -d ' '`
                        result=`echo "$result + $readvalue"|bc`    
                        if [ $i == 3 ]; then
                                break
                           
                        fi 
                fi
              
                if [ "$label" == "[OVERALL]" -a "$status" == "RunTime(ms)" ];
                   then
                        runtime=`echo $line|awk -F ',' '{print $3}'|tr -d ' '`
                        
                fi
 

        fi
done < /opt/cassandra/testresultofycsb.txt
if [ $i == 2 -a $runtime -gt 0 ]; then
        result=$[result*1000]
        throughput=`echo "sclae=10;$result/$runtime"|bc`
        echo $throughput
else
        echo "error"
fi
else
   echo "not exist"
fi
