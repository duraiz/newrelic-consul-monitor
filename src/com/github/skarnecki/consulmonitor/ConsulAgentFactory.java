package com.github.skarnecki.consulmonitor;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.AgentFactory;
import com.newrelic.metrics.publish.configuration.ConfigurationException;

import java.util.Map;

public class ConsulAgentFactory extends AgentFactory {

    @Override
    public Agent createConfiguredAgent(Map<String, Object> properties) throws ConfigurationException {
        String name = (String) properties.get("name");
        String host = (String) properties.get("host");
        Long port = (Long)properties.get("port");
        
        if (name == null || host == null || port == 0) {
            throw new ConfigurationException("'name', 'host' and 'port' cannot be null. Do you have a 'config/plugin.json' file?");
        }
        
        return new ConsulAgent(name, host, port.intValue());
    }
}
