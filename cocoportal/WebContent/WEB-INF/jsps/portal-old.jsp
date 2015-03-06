<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<%@page import="net.geant.coco.agent.portal.beans.CoCoCoordinates"%>
<%@page import="net.geant.coco.agent.portal.beans.Topology"%>
<%@page import="net.geant.coco.agent.portal.beans.CoCoNode"%>
<%@page import="net.geant.coco.agent.portal.beans.CoCoLink"%>
<%@page import="net.geant.coco.agent.portal.beans.NodeType"%>
<%@page import="net.geant.coco.agent.portal.beans.CoCoVPN"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collections"%>





<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>CoCo Web Portal</title>
</head>
<link href="${pageContext.request.contextPath}/static/css/main.css"
	rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/static/css/coco.css"
	rel="stylesheet" type="text/css" />
<body>
	<p>
		<a href="${pageContext.request.contextPath}/offers">Show current
			offers.</a>
	</p>
	<p>
		<a href="${pageContext.request.contextPath}/createoffer">Add a new
			offer.</a>
	</p>

	<div id="logos">
		<div class="logo">
			<img
				src="${pageContext.request.contextPath}/static/images/SURFnet.pdf"
				height="63" style="margin-bottom: 10px" />
		</div>
		<div class="logo"></div>
		<div class="logo">
			<img src="${pageContext.request.contextPath}/static/images/tno.png"
				height="63" style="margin-bottom: 10px" />
		</div>
		<div class="logo">
			<img src="${pageContext.request.contextPath}/static/images/geant.png"
				height="63" style="margin-bottom: 10px" />
		</div>
		<div class="logo">
			<img src="${pageContext.request.contextPath}/static/images/UvA.pdf"
				height="63" style="margin-bottom: 10px" />
		</div>
		<div>
			<img src="${pageContext.request.contextPath}/static/images/pica.png"
				height="63" style="margin-bottom: 10px" />
		</div>
	</div>

	<h1>User controlled provisioning of VPNs on OpenFlow switches
		using OpenDaylight.</h1>

	<c:forEach var="switchNode" items="${switchNodes}">
		<tr>
			<td><c:out value="${switchNode.name}"></c:out></td>
			<td><c:out value="${switchNode.x}"></c:out></td>
			<td><c:out value="${switchNode.y}"></c:out></td>
		</tr>
	</c:forEach>

	<canvas id="canvas" width="870px" height="924px"></canvas>

	<script>
		var ctx = document.getElementById('canvas').getContext("2d");
		var img = new Image();
		img.src = "${pageContext.request.contextPath}/static/images/nederland.png";
		img.onload = function() {
			ctx.drawImage(img, 0, 0);
			ctx.font = "12px Helvetica";
			ctx.textAlign = 'center';
		}
	</script>

</body>
</html>