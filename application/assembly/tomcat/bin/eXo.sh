#!/bin/sh

PRG="$0"

PRGDIR=`dirname "$PRG"`
LOG_OPTS="-Dorg.exoplatform.services.log.Log=org.apache.commons.logging.impl.SimpleLog"
#SECURITY_OPTS="-Djava.security.auth.login.config=$PRGDIR/../conf/jaas.conf"
EXO_OPTS="-Dexo.product.developing=true"

#DYLD_LIBRARY_PATH="/Users/tuannguyen/Desktop/YourKit.app/bin/mac/"
#export  DYLD_LIBRARY_PATH
#YOURKIT_PROFILE_OPTION="-agentlib:yjpagent"

JAVA_OPTS="$YOURKIT_PROFILE_OPTION $JAVA_OPTS $LOG_OPTS $SECURITY_OPTS $EXO_OPTS"
export JAVA_OPTS
exec "$PRGDIR"/catalina.sh "$@"
