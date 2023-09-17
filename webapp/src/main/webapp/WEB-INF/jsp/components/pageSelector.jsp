<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="currentUrl" value="${pageContext.request.requestURL}"/>

<div class="pagination">
    <a class="pagination-button small-a ${page <= 1 ? 'disabled' : ''}"
       href="${pageContext.request.contextPath}/?page=${page - 1}${empty sortBy ? '' : '&sortBy=' + sortBy}"
       onclick="return ${page <= 1 ? 'false' : 'true'}">
        <i class="fas fa-chevron-left"></i>
    </a>

    <c:forEach begin="1" end="${totalPages}" var="pageNumber">
        <a class="pagination-button ${page == pageNumber ? 'active' : ''}"
           href="${pageContext.request.contextPath}/?page=${pageNumber}${empty sortBy ? '' : '&sortBy=' + sortBy}" >
            ${pageNumber}
        </a>
    </c:forEach>

    <a class="pagination-button small-a ${page >= totalPages ? 'disabled' : ''}"
       href="${pageContext.request.contextPath}/?page=${page + 1}${empty sortBy ? '' : '&sortBy=' + sortBy}"
       onclick="return ${page >= totalPages ? 'false' : 'true'}" >
            <i class="fas fa-chevron-right"></i>
    </a>
</div>


