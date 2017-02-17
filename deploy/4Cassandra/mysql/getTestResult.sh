resultFile=/opt/mysql/testresults.txt
#resultFile=./hibench.report
minduration=0
i=1
if [  -f "$resultFile" ]; then
cat $resultFile > /opt/mysql/testresult.txt
#for line in `cat $resultFile`
while read line
do
        if [ -n "$line" ]; then
                #echo $line
                throughput=`echo $line|awk -F ' ' '{print $3}'|tr -d ' '`
                transactions=`echo $line|awk -F ' ' '{print $1}'|tr -d ' '`
                #echo $transactions
                if [ $transactions == "transactions:" ];
                   then
                        #echo "hehe"
                        ((i++))
                        #echo $i
                        break
                fi
        fi
done < /opt/mysql/testresult.txt
if [ $i=2 ]; then
        #echo $i
        #echo $throughput
        echo ${throughput:1}
else
        echo "error"
fi
else
   echo "not exist"
fi

