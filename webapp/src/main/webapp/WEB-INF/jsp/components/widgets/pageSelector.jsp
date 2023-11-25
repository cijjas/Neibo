<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="currentUrl" value=""/>
<c:set var="baseUrl"
       value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}"/>

<c:set var="paginationClass" value=""/>
<c:choose>
    <c:when test="${contextPath.startsWith('/marketplace')}">
        <c:set var="paginationClass" value="pagination-marketplace"/>
    </c:when>
    <c:otherwise>
        <c:set var="paginationClass" value="pagination"/>
    </c:otherwise>
</c:choose>

<div class="${paginationClass}">
    <c:url var="prevUrl" value="">
        <c:param name="page" value="${page - 1}"/>
        <c:if test="${param.tag ne null}">
            <c:forEach var="tag" items="${paramValues.tag}">
                <c:param name="tag" value="${tag}"/>
            </c:forEach>
        </c:if>
    </c:url>

    <a class="${paginationClass}-button small-a ${page <= 1 ? 'disabled' : ''}"
       href="${baseUrl}${contextPath}/${prevUrl}"
       onclick="return ${page <= 1 ? 'false' : 'true'}">
        <i class="fas fa-chevron-left"></i>
    </a>

    <c:forEach begin="1" end="${totalPages}" var="pageNumber">
        <c:url var="pageUrl" value="">
            <c:param name="page" value="${pageNumber}"/>
            <c:if test="${param.tag ne null}">
                <c:forEach var="tag" items="${paramValues.tag}">
                    <c:param name="tag" value="${tag}"/>
                </c:forEach>
            </c:if>
        </c:url>
        <a class="${paginationClass}-button ${page == pageNumber ? 'active' : ''}"
           href="${baseUrl}${contextPath}/${pageUrl}">
                ${pageNumber}
        </a>
    </c:forEach>

    <c:url var="nextUrl" value="">
        <c:param name="page" value="${page + 1}"/>
        <c:if test="${param.tag ne null}">
            <c:forEach var="tag" items="${paramValues.tag}">
                <c:param name="tag" value="${tag}"/>
            </c:forEach>
        </c:if>
    </c:url>
    <a class="${paginationClass}-button small-a ${page >= totalPages ? 'disabled' : ''}"
       href="${baseUrl}${contextPath}/${nextUrl}"
       onclick="return ${page >= totalPages ? 'false' : 'true'}">
        <i class="fas fa-chevron-right"></i>
    </a>
</div>



