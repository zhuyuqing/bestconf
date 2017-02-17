resultPath=/opt/mysql/testresults.txt
rm -f /opt/mysql/testresults.txt
touch /opt/mysql/testresults.txt
cd /tmp
#sysbench --test=oltp --mysql-socket=/tmp/mysql.sock --mysql-user=root --mysql-password=root --db-driver=mysql prepare
sysbench --test=memory --memory-block-size=64K --memory-total-size=16G --num-threads=16 --max-requests=4140000 --mysql-socket=/tmp/mysql.sock --mysql-user=root --mysql-password=root --oltp-dist-type=uniform --report-interval=10 --db-driver=mysql --oltp-test-mode=simple run >& $resultPath
echo ok >> $resultPath
