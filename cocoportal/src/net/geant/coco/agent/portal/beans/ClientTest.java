package net.geant.coco.agent.portal.beans;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class ClientTest {
	private static int seq = 50;

	/*
	 * public static String test() { ClientConfig config = new
	 * DefaultClientConfig(); Client client = Client.create(config);
	 * client.addFilter(new HTTPBasicAuthFilter("admin", "admin")); WebResource
	 * service = client.resource(getBaseURI());
	 * 
	 * // Get plain text String output =
	 * service.path("operational").path("opendaylight-inventory:nodes"
	 * ).accept(MediaType.APPLICATION_JSON).get(String.class); output =
	 * service.path
	 * ("operational").path("opendaylight-inventory:nodes/node/openflow:1/table/0"
	 * ).accept(MediaType.APPLICATION_XML).get(String.class); return(output); }
	 */

	public static void clearAll() {
		// Delete all flows on switches
		sendtoSwitch("openflow:1", null, null);
		sendtoSwitch("openflow:2", null, null);
		sendtoSwitch("openflow:3", null, null);
		sendtoSwitch("openflow:4", null, null);
	}

	public static String puttest() {
		String f;
		Flow flow;
		String switchID;
/*
		// Configure flow on sw1 37 -> 15
		switchID = "openflow:1";
		flow = new Flow(switchID, seq);
		flow.inPort(37);
		flow.matchVlan(101);
		flow.stripVlan();
		flow.outPort(15);
		f = flow.buildFlow();
		sendtoSwitch(switchID, f);

		// Configure flow on sw1 15 -> 37
		switchID = "openflow:1";
		flow = new Flow(switchID, seq);
		flow.inPort(15);
		flow.pushVlan(101);
		flow.setDstMAC("fa:16:3e:bd:03:4a");
		flow.outPort(37);
		f = flow.buildFlow();
		sendtoSwitch(switchID, f);

		// Configure flow on sw2 16 -> 45
		switchID = "openflow:2";
		flow = new Flow(switchID, seq);
		flow.inPort(16);
		flow.outPort(45);
		f = flow.buildFlow();
		sendtoSwitch(switchID, f);

		// Configure flow on sw2 45 -> 16
		switchID = "openflow:2";
		flow = new Flow(switchID, seq);
		flow.inPort(45);
		flow.outPort(16);
		f = flow.buildFlow();
		sendtoSwitch(switchID, f);

		// Configure flow on sw4 50 -> 46
		switchID = "openflow:4";
		flow = new Flow(switchID, seq);
		flow.inPort(50);
		flow.matchVlan(103);
		flow.stripVlan();
		flow.outPort(46);
		f = flow.buildFlow();
		sendtoSwitch(switchID, f);

		// Configure flow on sw4 46 -> 50
		switchID = "openflow:4";
		flow = new Flow(switchID, seq);
		flow.inPort(46);
		flow.pushVlan(103);
		flow.setDstMAC("fa:16:3e:9e:55:db");
		flow.outPort(50);
		f = flow.buildFlow();
		sendtoSwitch(switchID, f);
*/		
		return ("done");
	}

	private static URI getBaseURI() {
		//return UriBuilder.fromUri("http://192.168.56.125:8181/restconf")
		return UriBuilder.fromUri("http://192.168.255.59:8181/restconf")
				.build();
	}

	public static String getJsonTopo() {
		try {
			ClientConfig config = new DefaultClientConfig();
			Client client = Client.create(config);
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
				String sequence_nr = String.valueOf(seq);
				seq++;

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
