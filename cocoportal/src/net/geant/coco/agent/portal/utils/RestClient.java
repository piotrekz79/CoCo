package net.geant.coco.agent.portal.utils;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class RestClient {
    private static int TIMEOUT = 3000;

    public static void clearAll() {
        // Delete all flows on switches
        sendtoSwitch("openflow:1", null, null);
        sendtoSwitch("openflow:2", null, null);
        sendtoSwitch("openflow:3", null, null);
        sendtoSwitch("openflow:4", null, null);
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://192.168.56.125:8181/restconf")
        //return UriBuilder.fromUri("http://192.168.255.59:8181/restconf")
                .build();
    }

    public static String getJsonTopo() {
        try {
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            client.setConnectTimeout(TIMEOUT);
            client.setReadTimeout(TIMEOUT);

            ClientResponse r;
            client.addFilter(new HTTPBasicAuthFilter("admin", "admin"));
            WebResource service = client.resource(getBaseURI());
            r = service.path("operational/network-topology:network-topology")
                    .type(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);
            int status = r.getStatus();
            String out = r.getEntity(String.class);

            if (status == 200) {
                return out;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String getJSONPortInfo(String nodeId, String portId) {
        try {
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            ClientResponse r;
            client.addFilter(new HTTPBasicAuthFilter("admin", "admin"));
            WebResource service = client.resource(getBaseURI());
            String path = String.format("%s/node-connector/%s", nodeId, portId);
            r = service.path("operational/opendaylight-inventory:nodes/node")
                    .path(path)
                    .type(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);
            int status = r.getStatus();
            String out = r.getEntity(String.class);
            if (status == 200) {
                return out;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void sendtoSwitch(String switchID, String flow, String flowNr) {
        try {
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            ClientResponse r;
            client.addFilter(new HTTPBasicAuthFilter("admin", "admin"));
            WebResource service = client.resource(getBaseURI());

            if (flow == null) {
                // Get inventory first. Needed to avoid error on DELETE???
                System.out.println("inventory on switch " + switchID);
                r = service.path("operational/opendaylight-inventory:nodes")
                        .type(javax.ws.rs.core.MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .get(ClientResponse.class);

                // Delete all flows on switch
                System.out.println("clear on switch " + switchID);
                String path = String.format(
                        "opendaylight-inventory:nodes/node/%s", switchID);
                r = service.path("config").path(path)
                        .type(javax.ws.rs.core.MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .delete(ClientResponse.class);
            } else {
                System.out.println("config on switch " + switchID);

                String path = String
                        .format("config/opendaylight-inventory:nodes/node/%s/table/0/flow/",
                                switchID);
                System.out.println(flow);
                System.out.flush();
                r = service.path(path).path(flowNr)
                        .type(javax.ws.rs.core.MediaType.APPLICATION_XML)
                        .accept(MediaType.APPLICATION_XML)
                        .put(ClientResponse.class, flow);
                System.out.println(r.getStatus());
            }
            String error = r.getEntity(String.class);
            System.out.println(error);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
