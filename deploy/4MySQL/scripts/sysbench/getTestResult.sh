resultFile=/opt/mysql/testresults.txt
minduration=0
i=1
if [  -f "$resultFile" ]; then
cat $resultFile > /opt/mysql/testresult.txt
while read line
do
        if [ -n "$line" ]; then
                throughput=`echo $line|awk -F ' ' '{print $3}'|tr -d ' '`
                transactions=`echo $line|awk -F ' ' '{print $1}'|tr -d ' '`
                if [ $transactions == "transactions:" ];
                   then
                        ((i++))
                        break
                fi
        fi
done < /opt/mysql/testresult.txt
if [ $i=2 ]; then
        
        echo ${throughput:1}
else
        echo "error"
fi
else
   echo "not exist"
fi

