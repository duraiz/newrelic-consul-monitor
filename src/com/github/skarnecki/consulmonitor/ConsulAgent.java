package com.github.skarnecki.consulmonitor;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.configuration.ConfigurationException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConsulAgent extends Agent {

    private static final String GUID = "com.github.skarnecki.consulmonitor";
    private static final String VERSION = "1.0.0";

    private static final String HTTP = "http";
    private static final String PEERS_URL = "/v1/status/peers";

    private String name;
    private URL peersUrl;

    public ConsulAgent(String name, String host, Integer port) throws ConfigurationException {
        super(GUID, VERSION);
        try {
            this.name = name;
            this.peersUrl = new URL(HTTP, host, port, PEERS_URL);
        } catch (MalformedURLException e) {
            throw new ConfigurationException("Wikipedia metric URL could not be parsed", e);
        }
    }

    @Override
    public String getComponentHumanLabel() {
        return name;
    }

    @Override
    public void pollCycle() {
        Integer members = getNumberOfMembers();
        if (members != null) {
             reportMetric("Articles/Count", "members", members);
        } else {
            //TODO: log numArticles when null
        }
    }

    private Integer getNumberOfMembers() {
        JSONObject jsonObj = getJSONResponse(this.peersUrl);
        if (jsonObj != null) {
            return jsonObj.size();
        } else {
            return null;
        }
    }

    private JSONObject getJSONResponse(URL url) {
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
        return (JSONObject) response;
    }

}
