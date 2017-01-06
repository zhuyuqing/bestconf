pidprepare=`pgrep prepare`
pidrun=`pgrep run`
pidrunall=`pgrep run-all`
echo $pidprepare
echo $pidrun
echo $pidrunall
if [ -n "$pidprepare" ];
then

   echo "kill prepare"
   kill -9 $pidprepare && kill -9 $pidprepare && echo yes
fi 
if [ -n "$pidrunall" ];
then
   echo "kill runall"
   kill -9 $pidrunall && kill -9 $pidrunall && echo yes
fi

if [ -n "$pidrun" ];
then
   echo "kill run"
   kill -9 $pidrun && kill -9 $pidrun && echo yes
fi

echo ok
