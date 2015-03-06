<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Manage VPNs</title>
</head>
<body>

	<table>
		<c:forEach var="vpn" items="${vpns}">
			<tr>
				<td><c:out value="${vpn.name}"></c:out></td>
				<td><c:out value="${vpn.mplsLabel}"></c:out></td>
			</tr>
		</c:forEach>
	</table>

</body>
</html>