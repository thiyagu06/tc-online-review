<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page language="java" session="true" isErrorPage="true" %>
<%@ page import="java.util.Date"%>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html xhtml="true">
<head>
	<title>TopCoder - Error</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

	<link type="text/css" rel="stylesheet" href="<html:rewrite href='/css/home.css' />" />
	<link type="image/x-icon" rel="shortcut icon" href="<html:rewrite href='/i/favicon.ico' />" />
	<link type="text/css" rel="stylesheet" href="<html:rewrite href='/css/or/new_styles.css' />" />

<style TYPE="text/css">
<!--
body {
	text-align: center;
}

.centerer {
	width: 400px;
	background-image: url(/i/interface/errorBox.gif);
	background-repeat: no-repeat;
	background-position: top center;
	text-align: left;
	margin-top: 20px;
	margin-left: auto;
	margin-right: auto;
	margin-bottom: 20px;
	padding: 0px;
	}
-->
</style>
</head>

<body>
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="homeTopBar" align="left"><!-- @ --></td>
			<td class="homeTopBar" align="right"><a href="/tc?module=Static&#x26;d1=about&#x26;d2=index" class="loginLinks">About TopCoder</a></td>
		</tr>
	</table>

	<table width="100%" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td width="50%" class="homeLogo" align="left"><a href="/"><html:img src="/i/home/TC_homeLogo.gif" alt="TopCoder" /></a></td>
			<td width="50%" class="homeLogo" align="right">&#160;</td>
		</tr>
	</table>
	<br /><br />

<%
	// Try to print stack trace into a String object

    String stackTrace;
    if (exception == null) {
        stackTrace = "exception is null";
    } else {
        StringWriter sw = new StringWriter();

        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        pw.close();

        stackTrace = sw.toString();
        stackTrace = stackTrace.replaceAll("\n", "<br />");
        stackTrace = stackTrace.replaceAll("\t", "&#160;&#160;&#160;&#160; ");
    }
%>

<%--
	<div align="left" class="homeText">
		Exception Info (for debugging): <%= stackTrace %>
	</div>
--%>
	<div class="centerer">
		<div style="padding:25px">
			<b><span style="font-size:18px;color:#990000;">Error</span>
			<br /><br />
			<span class="homeText">
				<b>An error has occurred when attempting to process your request.</b>
				<br /><br />
				You may click <a href="javascript:history.back();">here</a> to return to the last page you were viewing.
				<br /><br />
				If you have a question or comment, please email <a href="mailto:service@topcoder.com" class="bodyText">service@topcoder.com</a>.
				<br /><br /><br /><br /><br /><br />
				<%= new Date().toString() %>
				<br /><br /><br /><br /><br /><br /><br /><br />&#160;
			</span></b>
		</div>
	</div>

	<jsp:include page="/includes/inc_footer.jsp" />
</body>
</html:html>