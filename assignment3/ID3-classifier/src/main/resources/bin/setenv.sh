#!/bin/sh

# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.

# Get standard environment variables
# if JAVA_HOME is not set we're not happy
if [ -z "$JAVA_HOME" ]; then
  echo "You must set the JAVA_HOME variable before running chirpers-server scripts."
  exit 1
fi

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
os400=false
case "`uname`" in
CYGWIN*) cygwin=true;;
OS400*) os400=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`

# Only set CHIRPERS_HOME if not already set
[ -z "$CHIRPERS_HOME" ] && CHIRPERS_HOME=`cd "$PRGDIR/.." ; pwd`

CHIRPERS_CLASSPATH=""



for f in "$CHIRPERS_HOME"/lib/*.jar
do
  CHIRPERS_CLASSPATH="$CHIRPERS_CLASSPATH":$f
done
CHIRPERS_CLASSPATH="$CHIRPERS_CLASSPATH":"$CHIRPERS_HOME"/conf/log4j.properties

export CHIRPERS_HOME
export CHIRPERS_CLASSPATH