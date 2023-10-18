<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="currentUrl" value="${pageContext.request.requestURL}"/>
<c:set var="baseUrl"
       value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}"/>

<div class="pagination">
    <c:url var="prevUrl" value="${pageContext.request.contextPath}">
        <c:param name="page" value="${page - 1}"/>
        <c:if test="${param.tag ne null}">
            <c:forEach var="tag" items="${paramValues.tag}">
                <c:param name="tag" value="${tag}"/>
            </c:forEach>
        </c:if>
    </c:url>

    <a class="pagination-button small-a ${page <= 1 ? 'disabled' : ''}"
       href="${baseUrl}${contextPath}/${prevUrl}"
       onclick="return ${page <= 1 ? 'false' : 'true'}">
        <i class="fas fa-chevron-left"></i>
    </a>

    <c:forEach begin="1" end="${totalPages}" var="pageNumber">
        <c:url var="pageUrl" value="${pageContext.request.contextPath}">
            <c:param name="page" value="${pageNumber}"/>
            <c:if test="${param.tag ne null}">
                <c:forEach var="tag" items="${paramValues.tag}">
                    <c:param name="tag" value="${tag}"/>
                </c:forEach>
            </c:if>
        </c:url>
        <a class="pagination-button ${page == pageNumber ? 'active' : ''}"
           href="${baseUrl}${contextPath}/${pageUrl}">
                ${pageNumber}
        </a>
    </c:forEach>

    <c:url var="nextUrl" value="${pageContext.request.contextPath}">
        <c:param name="page" value="${page + 1}"/>
        <c:if test="${param.tag ne null}">
            <c:forEach var="tag" items="${paramValues.tag}">
                <c:param name="tag" value="${tag}"/>
            </c:forEach>
        </c:if>
    </c:url>
    <a class="pagination-button small-a ${page >= totalPages ? 'disabled' : ''}"
       href="${baseUrl}${contextPath}/${nextUrl}"
       onclick="return ${page >= totalPages ? 'false' : 'true'}">
        <i class="fas fa-chevron-right"></i>
    </a>
</div>



