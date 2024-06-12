#!/bin/bash

JBOSS_HOME=/opt/jboss/wildfly
JBOSS_CLI=$JBOSS_HOME/bin/jboss-cli.sh
JBOSS_CONFIG=standalone-full.xml

function wait_for_server() {
  until `$JBOSS_CLI -c "ls /deployment" &> /dev/null`; do
    sleep 1
  done
}

echo "=> Starting WildFly server"
/opt/jboss/wildfly/bin/standalone.sh -c standalone-full.xml > /dev/null &

echo "=> Waiting for the server to boot"
wait_for_server

echo "=> Executing the commands"
/opt/jboss/wildfly/bin/jboss-cli.sh -c --file="/opt/jboss/wildfly-scripts.cli"

echo "=> Shutting down WildFly"
$JBOSS_CLI -c ":shutdown"