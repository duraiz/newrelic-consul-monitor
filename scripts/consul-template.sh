#!/bin/sh

/bin/consul-template \
-consul ${CONSUL_CLUSTER} \
-template ${TEMPLATE_PL}:/opt/newrelic_consul_plugin-1.0.0/config/plugin.json \
-template ${TEMPLATE_NR}:/opt/newrelic_consul_plugin-1.0.0/config/newrelic.json:"${COMMAND}" \
-retry 10s