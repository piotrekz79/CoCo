<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<link href="${pageContext.request.contextPath}/static/css/main.css"
	rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/static/css/coco.css"
	rel="stylesheet" type="text/css" />
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Insert title here</title>
</head>
<body>
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

	<div id="legend">

		<div id="vpns">
			<div id="vpns-inner">
				<h3>Provisioned VPNs:</h3>
				<form action="${pageContext.request.contextPath}/" method="post">
					<c:forEach var="vpn" items="${vpns}">
						<input type='radio' name='vpn'
							value='<c:out value="${vpn.name}"></c:out>'
							onclick='this.form.submit()'>VPN <c:out
							value="${vpn.name}"></c:out>
							<br>
					</c:forEach>

				</form>
			</div>
		</div>
	</div>

	<canvas id="canvas" width="870px" height="924px"></canvas>

	<script>
		var ctx = document.getElementById('canvas').getContext("2d");
		var img = new Image();
		img.src = "${pageContext.request.contextPath}/static/images/nederland.png";
		img.onload = function() {
			ctx.drawImage(img, 0, 0);

			// draw links between switches
			var lineWidth = 3;
			ctx.lineWidth = lineWidth;
			ctx.strokeStyle = 'black';
			ctx.beginPath();
			<c:forEach var="networkLink" items="${links}">
			ctx.moveTo(<c:out value="${networkLink.fromX}"></c:out>,
					<c:out value="${networkLink.fromY}"></c:out>);
			ctx.lineTo(<c:out value="${networkLink.toX}"></c:out>,
					<c:out value="${networkLink.toY}"></c:out>);
			ctx.stroke();
			</c:forEach>

			// draw all switches
			ctx.font = "12px Helvetica";
			ctx.textAlign = 'center';
			var fontHeight = 12;
			var switchWidth = 75;
			var switchHeight = switchWidth / 2;
			<c:forEach var="networkSwitch" items="${switches}">
			ctx.fillStyle = 'red';
			ctx.fillRect(<c:out value="${networkSwitch.x}"></c:out>
					- switchWidth / 2,
					<c:out value="${networkSwitch.y}"></c:out> - switchHeight
							/ 2, switchWidth, switchHeight);
			ctx.fillStyle = 'black';
			ctx
					.fillText('<c:out value="${networkSwitch.name}"></c:out>',
							<c:out value="${networkSwitch.x}"></c:out>,
							<c:out value="${networkSwitch.y}"></c:out>
									+ fontHeight / 6);
			</c:forEach>

			// draw all sites
			ctx.font = "12px Helvetica";
			ctx.textAlign = 'center';
			var fontHeight = 12;
			var siteWidth = 75;
			var siteHeight = siteWidth / 2;
			<c:forEach var="networkSite" items="${sites}">
			ctx.fillStyle = 'lightblue';
			ctx.fillRect(<c:out value="${networkSite.x}"></c:out> - siteWidth
					/ 2, <c:out value="${networkSite.y}"></c:out> - siteHeight
					/ 2, siteWidth, siteHeight);
			ctx.fillStyle = 'black';
			ctx.fillText('<c:out value="${networkSite.name}"></c:out>',
					<c:out value="${networkSite.x}"></c:out>,
					<c:out value="${networkSite.y}"></c:out> + fontHeight / 6);
			</c:forEach>
		}
	</script>
	<p>
		<a href="${pageContext.request.contextPath}/vpns">Manage VPNs.</a>
	</p>
</body>
</html>