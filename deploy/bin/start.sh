#!/usr/bin/env bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
BESTCONF_HOME="$bin/../"

echo $JAVA_HOME
JAVA=$JAVA_HOME/bin/java

CLASSPATH=${CLASSPATH}:$JAVA_HOME/lib/tools.jar
# add libs to CLASSPATH
for f in $BESTCONF_HOME/lib/*.jar; do
	CLASSPATH=${CLASSPATH}:$f;
done
CLASSPATH="${CLASSPATH}:${BESTCONF_HOME}/bestconf.jar"

CLASS="cn.ict.zyq.bestConf.bestConf.BestConf"

cd ${BESTCONF_HOME}
exec "$JAVA" $JVM_OPTS -classpath "$CLASSPATH" $CLASS

