<%@page import="nl.surfnet.coco.agent.portal.CoCoCoordinates"%>
<%@page import="nl.surfnet.coco.agent.portal.Topology"%>
<%@page import="nl.surfnet.coco.agent.portal.CoCoNode"%>
<%@page import="nl.surfnet.coco.agent.portal.CoCoLink"%>
<%@page import="nl.surfnet.coco.agent.portal.NodeType"%>
<%@page import="nl.surfnet.coco.agent.portal.CoCoVPN"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collections"%>


<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html>

<html>

<head>

<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<link rel="stylesheet" href="coco.css" type="text/css" media="screen" />



<title>CoCo Web Portal</title>

</head>

<body>

	<div id="logos">
	<div class="logo"><img src="images/SURFnet.pdf" height="63" style="margin-bottom: 10px" /></div>
	<div class="logo"></div>
	<div class="logo"><img src="images/TNO.pdf" height="63" style="margin-bottom: 10px" /></div>
	<div class="logo"><img src="images/geant.png" height="63" style="margin-bottom: 10px" /></div>
	<div class="logo"><img src="images/UvA.pdf" height="63" style="margin-bottom: 10px" /></div>
	<div><img src="images/pica.png" height="63" style="margin-bottom: 10px" /></div>
	</div>
	
	<h1>User controlled provisioning of VPNs on OpenFlow switches using OpenDaylight.</h1>
	
	<div id="legend">
	<div id="newvpn">
		<form action="/agent/CoCoPortal" method="post">
			<h3>Set up new VPN:</h3>
			<%
				nl.surfnet.coco.agent.portal.Topology t = (nl.surfnet.coco.agent.portal.Topology) application.getAttribute("topo");
					List<CoCoNode> nodes = t.getNodes();
					Collections.sort(nodes);
					CoCoNode node;
					Iterator<CoCoNode> nodeIter = nodes.iterator();
					while (nodeIter.hasNext()) {
						node = nodeIter.next();	
						if ((node.getType() == NodeType.CE) && (!node.isInUse())) {
							out.write("<input type=\"checkbox\" name=\"site\" value=\"");
							out.print(node.getId());
							out.write("\">");
							out.print(node.getId());
							out.write("<br>");
						}
					}
					//out.write("<input type=\"checkbox\" name=\"site\" value=\"site1\">site 1<br>");
					//out.write("<input type=\"checkbox\" name=\"site\" value=\"site2\">site 2<br>");
					//out.write("<input type=\"checkbox\" name=\"site\" value=\"site3\">site 3<br>");
					//out.write("<input type=\"checkbox\" name=\"site\" value=\"site7\">site 7<br>");
			%>
			<input type="submit" name="go" value="submit">
		</form>
		</div>

		<div id="vpns">
		<div id="vpns-inner">
		<h3>Provisioned VPNs:</h3>
		<form action="/agent/CoCoPortal" method="post">
			<%
				List<CoCoVPN> vpns = t.getVpnList();
				Iterator<CoCoVPN> iter = vpns.iterator();
				
				CoCoVPN vpn;
				while (iter.hasNext()) {
					vpn = iter.next();
					out.write("<input type=\"radio\" name=\"vpn\" value=\"");
					out.print(vpn.getId());
					out.write("\" onclick=\"this.form.submit();\" ");
					if (vpn.getId() == t.getActiveVpn()) {
						out.write("checked");
					}
					out.write(">VPN ");
					out.print(vpn.getId());
					out.write(":<br />\n");
					for (String site: vpn.getSites()) {
						out.print(site);
						out.write("<br>\n");
					}
				}
			%>
		</form>
		</div>
		</div>
		
		<div id="reset">
		<div id="reset-inner">
		Remove all VPNs
		<form action="/agent/CoCoPortal" method="post">
			<input type="submit" name="reset" value="reset">
		</form>
		</div>
		</div>
	</div>


	<canvas id="canvas" width="870px" height="924px"></canvas>

	<script>
		var ctx = document.getElementById('canvas').getContext("2d");
		var img = new Image();
		img.src = "images/nederland.png";
		img.onload = function() {
			ctx.drawImage(img, 0, 0);
			ctx.font = "12px Helvetica";
			ctx.textAlign = 'center';
	<%String id;
			CoCoCoordinates coord = new CoCoCoordinates();
			CoCoLink edge;
			int x;
			int y;
			
			/*
			// draw links
			List<CoCoLink> edgeList = (List<CoCoLink>) t.getEdges();
			Iterator<CoCoLink> edgeIter = edgeList.iterator();
			while (edgeIter.hasNext()) {
				edge = edgeIter.next();
				
				// start point of link
				id = edge.getSrcNode();
				coord.setId(id);
				x = coord.getXpos();
				y = coord.getYpos();
				out.write("ctx.beginPath();\n");
				out.write("ctx.moveTo(");
				out.print(x);
				out.write(", ");
				out.print(y);
				out.write(");\n");
				
				// end point of link
				id = edge.getDstNode();
				coord.setId(id);
				x = coord.getXpos();
				y = coord.getYpos();
				out.write("ctx.lineTo(");
				out.print(x);
				out.write(", ");
				out.print(y);
				out.write(");\n");
				out.write("ctx.lineWidth = 1;");
				out.write("ctx.strokeStyle = 'black';");
				out.write("ctx.stroke();\n");
			}*/
			List<CoCoLink> edgeList = (List<CoCoLink>) t.getEdges();
			out.print(t.getCanvasDrawEdgesText(edgeList, 0, "black"));
			
			//List<CoCoVPN> vpnList = t.getVpnList();
			//Iterator<CoCoVPN> vpnIter = vpnList.iterator();
			//while (vpnIter.hasNext()) {
			//	out.print(t.getCanvasDrawEdgesText(vpnIter.next().getEdges(), 10, "red"));
			//}
			edgeList = t.getActiveVpnEdges();
			if (edgeList != null) {
				out.print(t.getCanvasDrawEdgesText(edgeList, 0, "red"));
			}

			int nodeWidth = 70;
			int nodeHeight = 35;
			List<CoCoNode> nodeList = t.getNodes();
			nodeIter = nodeList.iterator();
			while (nodeIter.hasNext()) {
				node = nodeIter.next();
				if (node.getType() == NodeType.CE) {
					out.write("ctx.fillStyle = 'lightblue';\n");
				} else {
					out.write("ctx.fillStyle = 'red';\n");
				}
				id = node.getId();
				out.write("ctx.fillRect(");
				coord.setId(id);
				x = coord.getXpos();
				y = coord.getYpos();
				out.print(x - nodeWidth / 2);
				out.write(", ");
				out.print(y - nodeHeight / 2);
				out.write(", ");
				out.print(nodeWidth);
				out.write(", ");
				out.print(nodeHeight);
				out.write(");\n");
				out.write("ctx.fillStyle = 'black';\n");
				out.write("ctx.fillText(\"");
				out.print(id);
				out.write("\", ");
				out.print(x);
				out.write(", ");
				out.print(y + 6); // add half of text height
				out.write(");\n");
			}
			out.write("ctx.font = \"26px Helvetica\";\n");
			out.write("ctx.fillStyle = \"blue\";\n");
			out.write("ctx.fillText('Live Monitoring', 435, 30);\n");
			%>
		}
	</script>

<h2>http://www.geant.net/opencall/SDN/Pages/CoCo.aspx</h2>
Live demonstration: Tuesday 18 November 2pm G&Eacute;ANT booth 2525
</body>

</html>