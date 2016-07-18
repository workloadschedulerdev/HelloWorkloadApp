<!DOCTYPE html>
<%@page import="com.ibm.twa.bluemix.samples.HelloWorkload"%>

<%!//Session key to register in the session
private static final String HELLO_WORKLOAD_SESSION_KEY = "HELLO_WORKLOAD";%>
<%
HelloWorkload hw ;
if (session.getAttribute(HELLO_WORKLOAD_SESSION_KEY)!=null){
	hw = (HelloWorkload) session.getAttribute(HELLO_WORKLOAD_SESSION_KEY);
}else{
	hw = new HelloWorkload();
	session.setAttribute(HELLO_WORKLOAD_SESSION_KEY,hw);
}
%>

<html>
<head>
	<title>Workload Scheduler Quick Start example</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" href="style.css" />
</head>
<body>
	<h1>Welcome to the Workload Scheduler Hello Workload sample application!</h1>

<%
 String action = request.getParameter("action");
 if ("create".equals(action)) {
	 hw.helloWorkloadCreate(out);
 }else if ("connect".equals(action)){
	 hw.helloWorkloadConnect(out);
 }else if ("track".equals(action)){
	 hw.helloWorkloadTrack(out);
 }
%>

<% if (!hw.isConnected()) { %>
You are not connected to the service. Click <a href="index.jsp?action=connect">Connect </a> to begin.<br>
<% } %>
<% if (hw.isConnected()) { %>
You are connected to the Workload Instance service. <br>
<a href="index.jsp?action=create">Create a process </a><br><br>
<% } %>
<% if (hw.getMyProcessId()>0) { %>
You have created and submitted to run a process with id = <%= hw.getMyProcessId() %><br>
<a href="index.jsp?action=track">Track the process </a><br>
<% } %>
	
</body>
</html>
