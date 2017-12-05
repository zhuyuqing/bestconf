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

# min and max heap sizes should be set to the same value to avoid
# stop-the-world GC pauses during resize, and so that we can lock the
# heap in memory on startup to prevent any of it from being swapped
# out.
JVM_OPTS="-Xmx10g -Xms10g -Xmn1g -XX:+HeapDumpOnOutOfMemoryError"

cd ${BESTCONF_HOME}
exec "$JAVA" $JVM_OPTS -classpath "$CLASSPATH" $CLASS

