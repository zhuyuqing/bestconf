path=/opt/mysql/startresults.txt
rm -f /opt/mysql/startresults.txt
touch /opt/mysql/startresults.txt
cd /usr/sbin
./service mysqld start >& $path      
