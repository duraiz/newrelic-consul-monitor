#!/bin/sh

/bin/consul-template -consul ${CONSUL_CLUSTER} -template ${TEMPLATE}:/opt/newrelic_consul_plugin-1.0.0/config/newrelic.json:"${COMMAND}"