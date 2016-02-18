package com.github.skarnecki.consulmonitor;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.configuration.ConfigurationException;
import com.newrelic.metrics.publish.util.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConsulAgent extends Agent {

    private static final Logger logger = Logger.getLogger(ConsulAgent.class);

    private static final String GUID = "com.github.skarnecki.consulmonitor";
    private static final String VERSION = "1.0.0";

    private static final String HTTP = "http";
    private static final String PEERS_URL = "/v1/status/peers";
    private static final String SERVICES_URL = "/v1/catalog/services";
    private static final String NODES_URL = "/v1/catalog/nodes";

    private String name;
    private URL peersUrl;
    private URL servicesUrl;
    private URL nodesUrl;

    public ConsulAgent(String name, String host, Integer port) throws ConfigurationException {
        super(GUID, VERSION);
        try {
            this.name = name;
            this.peersUrl = new URL(HTTP, host, port, PEERS_URL);
            this.servicesUrl = new URL(HTTP, host, port, SERVICES_URL);
            this.nodesUrl = new URL(HTTP, host, port, NODES_URL);
        } catch (MalformedURLException e) {
            throw new ConfigurationException("URL could not be parsed", e);
        }
    }

    @Override
    public String getComponentHumanLabel() {
        return name;
    }

    @Override
    public void pollCycle() {
        Integer members = getNumberOfMembers();
        Integer services = getNumberOfServices();
        Integer nodes = getNumberOfNodes();
        if (members != null) {
            reportMetric("Articles/Count", "members", members);
        }
        if (services != null) {
            reportMetric("Articles/Count", "services", services);
        }
        if (nodes != null) {
            reportMetric("Articles/Count", "nodes", nodes);
        }
    }

    private Integer getNumberOfMembers() {
        JSONArray jsonObj = (JSONArray)getJSONResponse(this.peersUrl);
        logger.debug(new Object[]{jsonObj});
        if (jsonObj != null) {
            return jsonObj.size();
        } else {
            return null;
        }
    }

    private Integer getNumberOfServices() {
        JSONObject jsonObj = (JSONObject)getJSONResponse(this.servicesUrl);
        logger.debug(new Object[]{getJSONResponse(this.servicesUrl)});
        if (jsonObj != null) {
            return jsonObj.size();
        } else {
            return null;
        }
    }

    private Integer getNumberOfNodes() {
        JSONArray jsonObj = (JSONArray)getJSONResponse(this.nodesUrl);
        logger.debug(new Object[]{jsonObj});
        if (jsonObj != null) {
            return jsonObj.size();
        } else {
            return null;
        }
    }

    private Object getJSONResponse(URL url) {
        Object response = null;
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Accept", "application/json");
            inputStream = connection.getInputStream();
            response = JSONValue.parse(new InputStreamReader(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {}
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

}
