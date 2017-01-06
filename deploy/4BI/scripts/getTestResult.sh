
resultFile=/opt/HiBench-master/report/hibench.report
minduration=0
i=1
if [  -f "$resultFile" ]; then
cat $resultFile > /opt/hadoop-2.6.5/hibenchresults.txt
#for line in `cat $resultFile`
while read line
do
        if [ -n "$line" ]; then
                duration=`echo $line|awk -F ' ' '{print $5}'|tr -d ' '`
                if [ $duration == "Duration(s)" ];
                   then
                        ((i++))
                elif [ "$duration" -gt "$minduratio" ];
                   then
                        ((i++))
                fi
        fi
done < /opt/hadoop-2.6.5/hibenchresults.txt
if [ $i=3 ]; then
        echo $duration
else
        echo "error"
fi
else
   echo "not exist"
fi
