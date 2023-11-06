<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="currentUrl" value=""/>
<c:set var="baseUrl"
       value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}"/>

<div class="rejected-switch">
    <c:url var="verifiedUrl" value="">
        <c:param name="verified" value="true"/>
    </c:url>

    <a class="switch-button ${verified == true ? 'active' : ''}"
       href="${baseUrl}${contextPath}/${verifiedUrl}">
        <spring:message code="Verified"/>
    </a>

    <c:url var="rejectedUrl" value="">
        <c:param name="verified" value="false"/>
    </c:url>

    <a class="switch-button ${verified == false ? 'rejected' : ''}"
       href="${baseUrl}${contextPath}/${rejectedUrl}">
        <spring:message code="Rejected"/>
    </a>
</div>



