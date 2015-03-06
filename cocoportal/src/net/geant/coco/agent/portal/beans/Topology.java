/**
 * 
 */
package net.geant.coco.agent.portal.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author rvdp
 *
 */
public class Topology {

	private DirectedGraph<CoCoNode, CoCoLink> graph = new DefaultDirectedGraph<CoCoNode, CoCoLink>(
			CoCoLink.class);
	private HashMap<String, CoCoNode> nodeMap = new HashMap<String, CoCoNode>();
	private List<CoCoLink> edges = new ArrayList<CoCoLink>();
	private List<CoCoVPN> vpns = new ArrayList<CoCoVPN>();
	private String id;
	private static int flowId = 1;
	private int activeVpn = 1;

	public Topology() {

		// make REST call to OpenDaylight to get topology info in JSON format
		String jsonTopo = ClientTest.getJsonTopo();

		// Parse the JSON info in 'jsonTopo'
		try {
			// Store nodes in HashMap 'nodeMap' with 'node-id' as key
			// Element are CoCoNode objects that also contain the node's
			// termination points
			JSONParser jsonParser = new JSONParser();
			JSONObject json = (JSONObject) jsonParser.parse(jsonTopo);
			JSONObject networkTopology = (JSONObject) json
					.get("network-topology");
			JSONArray topologyList = (JSONArray) networkTopology
					.get("topology");
			for (int i = 0; i < topologyList.size(); i++) {
				JSONObject topo = (JSONObject) topologyList.get(i);
				JSONArray nodeList = (JSONArray) topo.get("node");
				for (int j = 0; j < nodeList.size(); j++) {
					JSONObject node = (JSONObject) nodeList.get(j);
					String nodeId = (String) node.get("node-id");
					CoCoNode n = new CoCoNode(nodeId);
					JSONArray tpList = (JSONArray) node
							.get("termination-point");
					System.out.printf("Topology node %s: ", nodeId);
					// add all termination points to the CoCoNode object n
					for (int k = 0; k < tpList.size(); k++) {
						JSONObject tp = (JSONObject) tpList.get(k);
						String tpId = (String) tp.get("tp-id");
						System.out.printf("%s ", tpId);
						n.addTp(tpId);
					}
					System.out.println();
					nodeMap.put(nodeId, n);
					// Store the node in the graph too
					graph.addVertex(n);
				}

				// Parse JSON info and store links
				JSONArray linkList = (JSONArray) topo.get("link");
				for (int m = 0; m < linkList.size(); m++) {
					JSONObject link = (JSONObject) linkList.get(m);
					String linkId = (String) link.get("link-id");

					JSONObject source = (JSONObject) link.get("source");
					String sourceNode = (String) source.get("source-node");
					String sourceTp = (String) source.get("source-tp");

					JSONObject destination = (JSONObject) link
							.get("destination");
					String dstNode = (String) destination.get("dest-node");
					String dstTp = (String) destination.get("dest-tp");

					// create an edge between sourceNode and dstNode and add it
					// to the graph
					CoCoLink e = new CoCoLink(linkId, sourceNode, sourceTp,
							dstNode, dstTp);

					String sourceTpNr = getPortName(sourceNode, sourceTp);
					String dstTpNr = getPortName(dstNode, dstTp);
					e.setSrcTpNr(sourceTpNr);
					e.setDstTpNr(dstTpNr);
					System.out.printf(
							"link %s: from %s port %s to %s port %s\n", linkId,
							sourceNode, sourceTpNr, dstNode, dstTpNr);

					CoCoNode src = nodeMap.get(sourceNode);
					CoCoNode dst = nodeMap.get(dstNode);
					graph.addEdge(src, dst, e);
					edges.add(e);

					// update interface types of termination points in src and
					// dst node
					CoCoTerminationPoint cctpSrc = src.getTp(sourceTp);
					cctpSrc.setType(InterfaceType.NNI);
					CoCoTerminationPoint cctpDst = dst.getTp(dstTp);
					cctpDst.setType(InterfaceType.NNI);
				}

			}

			String siteName;
			CoCoNode site;
			CoCoLink srcdst;
			CoCoLink dstsrc;
			CoCoNode src;
			CoCoNode dst;

			// add site1
			siteName = "site1";
			site = new CoCoNode(siteName);
			// Node type is Customer Edge
			site.setType(NodeType.CE);
			site.setVlan("101");
			site.setIpv4Prefix("10.101.0.0/24");
			site.setMac("fa:16:3e:bd:03:4a");
			site.setPeMplsLabel("5101");
			nodeMap.put("site1", site);
			nodeMap.get("openflow:1").setType(NodeType.PE);
			srcdst = new CoCoLink(siteName, siteName, siteName + ":1",
					"openflow:1", "openflow:1:51");
			srcdst.setSrcTpNr("1");
			srcdst.setDstTpNr("51");
			dstsrc = new CoCoLink(siteName, "openflow:1", "openflow:1:51",
					siteName, siteName + ":1");
			dstsrc.setSrcTpNr("51");
			dstsrc.setDstTpNr("1");
			src = nodeMap.get(siteName);
			dst = nodeMap.get("openflow:1");
			graph.addVertex(src);
			graph.addEdge(src, dst, srcdst);
			edges.add(srcdst);
			graph.addEdge(dst, src, dstsrc);
			edges.add(dstsrc);
			System.out.println("addsite: " + src);
			System.out.println("addsite: " + dst);

			// add site2
			siteName = "site2";
			site = new CoCoNode(siteName);
			// Node type is Customer Edge
			site.setType(NodeType.CE);
			site.setVlan("102");
			site.setIpv4Prefix("10.102.0.0/24");
			site.setMac("fa:16:3e:27:81:97");
			site.setPeMplsLabel("5102");
			nodeMap.put("site2", site);
			nodeMap.get("openflow:1").setType(NodeType.PE);
			srcdst = new CoCoLink(siteName, siteName, siteName + ":1",
					"openflow:1", "openflow:1:51");
			srcdst.setSrcTpNr("1");
			srcdst.setDstTpNr("51");
			dstsrc = new CoCoLink(siteName, "openflow:1", "openflow:1:51",
					siteName, siteName + ":1");
			dstsrc.setSrcTpNr("51");
			dstsrc.setDstTpNr("1");
			src = nodeMap.get(siteName);
			dst = nodeMap.get("openflow:1");
			graph.addVertex(src);
			graph.addEdge(src, dst, srcdst);
			edges.add(srcdst);
			graph.addEdge(dst, src, dstsrc);
			edges.add(dstsrc);
			System.out.println("addsite: " + src);
			System.out.println("addsite: " + dst);

			// add site3
			siteName = "site3";
			site = new CoCoNode(siteName);
			// Node type is Customer Edge
			site.setType(NodeType.CE);
			site.setVlan("103");
			site.setIpv4Prefix("10.103.0.0/24");
			site.setMac("fa:16:3e:9e:55:db");
			site.setPeMplsLabel("5103");
			nodeMap.put("site3", site);
			nodeMap.get("openflow:4").setType(NodeType.PE);
			srcdst = new CoCoLink(siteName, siteName, siteName + ":1",
					"openflow:4", "openflow:4:50");
			srcdst.setSrcTpNr("1");
			srcdst.setDstTpNr("50");
			dstsrc = new CoCoLink(siteName, "openflow:4", "openflow:4:50",
					siteName, siteName + ":1");
			dstsrc.setSrcTpNr("50");
			dstsrc.setDstTpNr("1");
			src = nodeMap.get(siteName);
			dst = nodeMap.get("openflow:4");
			graph.addVertex(src);
			graph.addEdge(src, dst, srcdst);
			edges.add(srcdst);
			graph.addEdge(dst, src, dstsrc);
			edges.add(dstsrc);
			System.out.println("addsite: " + src);
			System.out.println("addsite: " + dst);

			// add site4
			siteName = "site4";
			site = new CoCoNode(siteName);
			// Node type is Customer Edge
			site.setType(NodeType.CE);
			site.setVlan("104");
			site.setIpv4Prefix("10.104.0.0/24");
			site.setMac("fa:16:3e:d1:5a:02");
			site.setPeMplsLabel("5104");
			nodeMap.put("site4", site);
			nodeMap.get("openflow:4").setType(NodeType.PE);
			srcdst = new CoCoLink(siteName, siteName, siteName + ":1",
					"openflow:4", "openflow:4:50");
			srcdst.setSrcTpNr("1");
			srcdst.setDstTpNr("50");
			dstsrc = new CoCoLink(siteName, "openflow:4", "openflow:4:50",
					siteName, siteName + ":1");
			dstsrc.setSrcTpNr("50");
			dstsrc.setDstTpNr("1");
			src = nodeMap.get(siteName);
			dst = nodeMap.get("openflow:4");
			graph.addVertex(src);
			graph.addEdge(src, dst, srcdst);
			edges.add(srcdst);
			graph.addEdge(dst, src, dstsrc);
			edges.add(dstsrc);
			System.out.println("addsite: " + src);
			System.out.println("addsite: " + dst);

			// add site7
			siteName = "site7";
			site = new CoCoNode(siteName);
			// Node type is Customer Edge
			site.setType(NodeType.CE);
			site.setVlan("107");
			site.setIpv4Prefix("10.107.0.0/24");
			site.setMac("fa:16:3e:c6:dc:82");
			site.setPeMplsLabel("5107");
			nodeMap.put("site7", site);
			nodeMap.get("openflow:3").setType(NodeType.PE);
			srcdst = new CoCoLink(siteName, siteName, siteName + ":1",
					"openflow:3", "openflow:3:37");
			srcdst.setSrcTpNr("1");
			srcdst.setDstTpNr("37");
			dstsrc = new CoCoLink(siteName, "openflow:3", "openflow:3:37",
					siteName, siteName + ":1");
			dstsrc.setSrcTpNr("37");
			dstsrc.setDstTpNr("1");
			src = nodeMap.get(siteName);
			dst = nodeMap.get("openflow:3");
			graph.addVertex(src);
			graph.addEdge(src, dst, srcdst);
			edges.add(srcdst);
			graph.addEdge(dst, src, dstsrc);
			edges.add(dstsrc);
			System.out.println("addsite: " + src);
			System.out.println("addsite: " + dst);

			// add site8
			siteName = "site8";
			site = new CoCoNode(siteName);
			// Node type is Customer Edge
			site.setType(NodeType.CE);
			site.setVlan("108");
			site.setIpv4Prefix("10.108.0.0/24");
			site.setMac("fa:16:3e:0e:cb:7d");
			site.setPeMplsLabel("5108");
			nodeMap.put("site8", site);
			nodeMap.get("openflow:3").setType(NodeType.PE);
			srcdst = new CoCoLink(siteName, siteName, siteName + ":1",
					"openflow:3", "openflow:3:37");
			srcdst.setSrcTpNr("1");
			srcdst.setDstTpNr("37");
			dstsrc = new CoCoLink(siteName, "openflow:3", "openflow:3:37",
					siteName, siteName + ":1");
			dstsrc.setSrcTpNr("37");
			dstsrc.setDstTpNr("1");
			src = nodeMap.get(siteName);
			dst = nodeMap.get("openflow:3");
			graph.addVertex(src);
			graph.addEdge(src, dst, srcdst);
			edges.add(srcdst);
			graph.addEdge(dst, src, dstsrc);
			edges.add(dstsrc);
			System.out.println("addsite: " + src);
			System.out.println("addsite: " + dst);

			// add uva1
			siteName = "uva1";
			site = new CoCoNode(siteName);
			// Node type is Customer Edge
			site.setType(NodeType.CE);
			site.setVlan("109");
			site.setIpv4Prefix("10.109.0.0/24");
			site.setMac("fa:00:00:00:00:00");
			site.setPeMplsLabel("5109");
			nodeMap.put("uva1", site);
			nodeMap.get("openflow:3").setType(NodeType.PE);
			srcdst = new CoCoLink(siteName, siteName, siteName + ":1",
					"openflow:3", "openflow:3:52");
			srcdst.setSrcTpNr("1");
			srcdst.setDstTpNr("52");
			dstsrc = new CoCoLink(siteName, "openflow:3", "openflow:3:52",
					siteName, siteName + ":1");
			dstsrc.setSrcTpNr("52");
			dstsrc.setDstTpNr("1");
			src = nodeMap.get(siteName);
			dst = nodeMap.get("openflow:3");
			graph.addVertex(src);
			graph.addEdge(src, dst, srcdst);
			edges.add(srcdst);
			graph.addEdge(dst, src, dstsrc);
			edges.add(dstsrc);
			System.out.println("addsite: " + src);
			System.out.println("addsite: " + dst);

			// add uva2
			siteName = "uva2";
			site = new CoCoNode(siteName);
			// Node type is Customer Edge
			site.setType(NodeType.CE);
			site.setVlan("110");
			site.setIpv4Prefix("10.110.0.0/24");
			site.setMac("fa:00:00:00:00:00");
			site.setPeMplsLabel("5110");
			nodeMap.put("uva2", site);
			nodeMap.get("openflow:3").setType(NodeType.PE);
			srcdst = new CoCoLink(siteName, siteName, siteName + ":1",
					"openflow:3", "openflow:3:52");
			srcdst.setSrcTpNr("1");
			srcdst.setDstTpNr("52");
			dstsrc = new CoCoLink(siteName, "openflow:3", "openflow:3:52",
					siteName, siteName + ":1");
			dstsrc.setSrcTpNr("52");
			dstsrc.setDstTpNr("1");
			src = nodeMap.get(siteName);
			dst = nodeMap.get("openflow:3");
			graph.addVertex(src);
			graph.addEdge(src, dst, srcdst);
			edges.add(srcdst);
			graph.addEdge(dst, src, dstsrc);
			edges.add(dstsrc);
			System.out.println("addsite: " + src);
			System.out.println("addsite: " + dst);

			System.out.println("graph = " + graph.toString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private String getPortName(String nodeId, String portId) {
		try {
			String r = ClientTest.getJSONPortInfo(nodeId, portId);

			JSONParser jsonParser = new JSONParser();
			JSONObject json = (JSONObject) jsonParser.parse(r);
			JSONArray nodeConnectorList = (JSONArray) json
					.get("node-connector");
			for (int i = 0; i < nodeConnectorList.size(); i++) {
				JSONObject nodeConnector = (JSONObject) nodeConnectorList
						.get(i);
				String name = (String) nodeConnector.get("id");
				if (name.equals(portId)) {
					String portName = (String) nodeConnector
							.get("flow-node-inventory:port-number");
					return portName;
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public void printGraph() {
		Set<CoCoNode> vertices = graph.vertexSet();
		Iterator<CoCoNode> iterator = vertices.iterator();
		while (iterator.hasNext()) {
			CoCoNode n = iterator.next();
			System.out.printf("Node %s: ", n.getId());
			Collection<CoCoTerminationPoint> termPoints = n.getTermPoints();
			Iterator<CoCoTerminationPoint> tpIter = termPoints.iterator();
			while (tpIter.hasNext()) {
				CoCoTerminationPoint tp = tpIter.next();
				System.out.printf("%s (%s) ", tp.getId(), tp.getType());
			}
			System.out.println();
		}
	}

	public Set<String> getUniPorts() {
		// Collection<CoCoTerminationPoint> uniPorts;
		Set<String> uniPorts = new HashSet<String>();
		Collection<CoCoNode> nodes = nodeMap.values();
		Iterator<CoCoNode> iter = nodes.iterator();
		while (iter.hasNext()) {
			CoCoNode n = iter.next();
			Collection<CoCoTerminationPoint> termPoints = n.getTermPoints();
			Iterator<CoCoTerminationPoint> i = termPoints.iterator();
			while (i.hasNext()) {
				CoCoTerminationPoint tp = i.next();

			}
		}
		return uniPorts;
	}

	public void addVpn(String sites[]) {
		try {
			int sequence = 1;
			// String sites[] = vpn.getSites();
			CoCoVPN vpn = new CoCoVPN();
			vpn.setSites(sites);
			for (int i = 0; i < sites.length; i++) {
				for (int j = 0; j < sites.length; j++) {
					if (i != j) {
						System.out.printf("find path %s to %s\n", sites[i],
								sites[j]);
						CoCoNode src = nodeMap.get(sites[i]);
						src.setInUse(true);
						CoCoNode dst = nodeMap.get(sites[j]);
						// System.out.println("getpath site1 " + src);
						// System.out.println("getpath site3 " + dst);
						// DijkstraShortestPath<CoCoNode, CoCoLink> path = new
						// DijkstraShortestPath<CoCoNode, CoCoLink>(
						// graph, src, dst);
						// List<CoCoLink> edges = path.getPathEdgeList();
						List<CoCoLink> newEdges = new ArrayList<CoCoLink>();
						List<CoCoLink> edges = DijkstraShortestPath
								.findPathBetween(graph, src, dst);
						System.out.println("edges = " + edges);
						newEdges.addAll(edges);
						newEdges.addAll(vpn.getEdges());
						vpn.setEdges(newEdges);
						String inPort = null;
						Flow flow;
						String f;
						Iterator<CoCoLink> iter = edges.iterator();
						while (iter.hasNext()) {
							CoCoLink edge = iter.next();

							System.out.printf(
									"path link: %s port %s to %s port %s\n",
									edge.getSrcNode(), edge.getSrcTpNr(),
									edge.getDstNode(), edge.getDstTpNr());

							if (inPort != null) {
								CoCoNode s = nodeMap.get(edge.getSrcNode());
								CoCoNode d = nodeMap.get(edge.getDstNode());

								if (s.getType() == NodeType.PE) {
									// Provision Provider Edge switches
									if (s.getTp(edge.getSrcTp()).getType() == InterfaceType.NNI) {
										// Provision ingress flow
										System.out
												.printf("flow rule on %s match inport %s, vlan %s, dst prefix %s, action set_vlan %s, add_mpls %s, output %s\n",
														s.getId(), inPort,
														src.getVlan(),
														dst.getIpv4Prefix(),
														vpn.getVpnVlanId(),
														dst.getPeMplsLabel(),
														edge.getSrcTpNr());
										flow = new Flow(s.getId(), flowId);
										flow.inPort(inPort);
										flow.matchVlan(src.getVlan());
										flow.matchEthertype(0x0800);
										flow.matchDstIpv4Prefix(dst
												.getIpv4Prefix());
										flow.modVlan(String.valueOf(vpn
												.getVpnVlanId()));
										// only push MPLS label if next switch
										// is a P switch
										if (d.getType() == NodeType.P) {
											flow.pushMplsLabel(dst
													.getPeMplsLabel());
										}
										flow.setDstMAC(dst.getMac());
										flow.outPort(edge.getSrcTpNr());
										f = flow.buildFlow();
										ClientTest.sendtoSwitch(s.getId(), f,
												String.valueOf(flowId));
									} else {
										// Provision egress flow
										System.out
												.printf("flow rule on %s match inport %s, mpls %s, action set_vlan %s, pop_mpls, set_mac %s, output %s\n",
														s.getId(), inPort,
														dst.getPeMplsLabel(),
														dst.getVlan(),
														dst.getMac(),
														edge.getSrcTpNr());
										flow = new Flow(s.getId(), flowId);
										flow.inPort(inPort);
										flow.matchEthertype(0x0800);
										flow.matchDstIpv4Prefix(dst
												.getIpv4Prefix());
										// flow.matchMplsLabel(dst
										// .getPeMplsLabel());
										// flow.popMplsLabel();
										flow.modVlan(dst.getVlan());
										// flow.setDstMAC(dst.getMac());
										flow.outPort(edge.getSrcTpNr());
										f = flow.buildFlow();
										ClientTest.sendtoSwitch(s.getId(), f,
												String.valueOf(flowId));
									}
								} else {
									// Provision Provider (P) switches
									System.out
											.printf("flow rule on %s match %s, action to %s\n",
													s.getId(),
													dst.getPeMplsLabel(),
													edge.getSrcTpNr());
									flow = new Flow(s.getId(), flowId);
									flow.inPort(inPort);
									flow.matchEthertype(0x8847);
									flow.matchMplsLabel(dst.getPeMplsLabel());
									// rvdp test begin
									//flow.matchVlan(src.getVlan());
									//flow.matchDlDst(dst.getMac());
									// rvdp test end
									if (d.getType() == NodeType.PE) {
										// remove MPLS label when forwarding to
										// P switch
										flow.popMplsLabel();
									}
									flow.outPort(edge.getSrcTpNr());
									f = flow.buildFlow();
									ClientTest.sendtoSwitch(s.getId(), f,
											String.valueOf(flowId));
								}
							}
							inPort = edge.getDstTpNr();
							sequence++;
							flowId++;
						}
					}
				}
			}
			activeVpn = vpn.getId();
			vpns.add(vpn);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void removeAllVpns() {
		// set all nodes in the VPNs to free
		for (CoCoVPN vpn : vpns) {
			for (String site : vpn.getSites()) {
				vpn.resetCounter();
				nodeMap.get(site).setInUse(false);
			}
		}
		vpns.clear();
		ClientTest.clearAll();
		flowId = 1;
	}

	public List<CoCoVPN> getVpnList() {
		return vpns;
	}

	public List<CoCoLink> getActiveVpnEdges() {
		List<CoCoLink> list = null;
		for (CoCoVPN vpn : vpns) {
			if (vpn.getId() == activeVpn) {
				list = vpn.getEdges();
			}
		}
		return list;
	}

	public List<CoCoNode> getNodes() {
		List<CoCoNode> list;
		list = new ArrayList<CoCoNode>(nodeMap.values());
		return list;
	}

	public List<CoCoLink> getEdges() {
		return edges;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCanvasDrawEdgesText(List<CoCoLink> edgeList, int offset,
			String colour) {
		String id;
		CoCoCoordinates coord = new CoCoCoordinates();
		CoCoNode node;
		CoCoLink edge;
		int x;
		int y;
		String result = "";

		// draw links
		Iterator<CoCoLink> edgeIter = edgeList.iterator();
		while (edgeIter.hasNext()) {
			edge = edgeIter.next();

			// start point of link
			id = edge.getSrcNode();
			coord.setId(id);
			x = coord.getXpos() + offset;
			y = coord.getYpos() + offset;

			result += "ctx.beginPath();\n";
			result += String.format("ctx.moveTo(%d, %d);\n", x, y);

			// end point of link
			id = edge.getDstNode();
			coord.setId(id);
			x = coord.getXpos() + offset;
			y = coord.getYpos() + offset;
			result += String.format("ctx.lineTo(%d, %d);\n", x, y);
			result += "ctx.lineWidth = 3;";
			result += String.format("ctx.strokeStyle = '%s';", colour);
			result += "ctx.stroke();\n";
		}
		return result;

		/*
		 * int nodeWidth = 70; int nodeHeight = 35; List<CoCoNode> nodeList =
		 * (List<CoCoNode>) t.getNodes(); Iterator<CoCoNode> nodeIter =
		 * nodeList.iterator(); while (nodeIter.hasNext()) { node =
		 * nodeIter.next(); if (node.getType() == NodeType.CE) {
		 * out.write("ctx.fillStyle = 'lightblue';\n"); } else {
		 * out.write("ctx.fillStyle = 'red';\n"); } id = node.getId();
		 * out.write("ctx.fillRect("); coord.setId(id); x = coord.getXpos(); y =
		 * coord.getYpos(); out.print(x - nodeWidth / 2); out.write(", ");
		 * out.print(y - nodeHeight / 2); out.write(", "); out.print(nodeWidth);
		 * out.write(", "); out.print(nodeHeight); out.write(");\n");
		 * out.write("ctx.fillStyle = 'black';\n");
		 * out.write("ctx.fillText(\""); out.print(id); out.write("\", ");
		 * out.print(x); out.write(", "); out.print(y + 6); // add half of text
		 * height out.write(");\n");
		 */
	}

	public int getActiveVpn() {
		return activeVpn;
	}

	public void setActiveVpn(int activeVpn) {
		this.activeVpn = activeVpn;
	}

}
