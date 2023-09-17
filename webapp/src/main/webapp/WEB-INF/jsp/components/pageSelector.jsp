<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="currentUrl" value="${pageContext.request.requestURL}"/>

<div class="pagination">
    <c:set var="sortByParam" value="${empty sortBy ? '' : 'sortBy=' + sortBy}" />

    <a  href="${pageContext.request.contextPath}/?page=${page - 1}&${sortByParam}" class="small-a">
        <button type="button" class="pagination-button small-button" ${page <= 1 ? 'disabled' : ''}>
            <i class="fas fa-chevron-left"></i>
        </button>
    </a>

    <c:forEach begin="1" end="${totalPages}" var="pageNumber">
        <a href="${pageContext.request.contextPath}/?page=${pageNumber}&${sortByParam}" data-abc="true" class="${page == pageNumber ? 'active' : ''}">
            <button type="button" class="pagination-button ${page == pageNumber ? 'active' : ''}" >${pageNumber}</button>
        </a>
    </c:forEach>

    <a href="${pageContext.request.contextPath}/?page=${page + 1}&${sortByParam}" class="small-a">
        <button type="button" class="pagination-button small-button"  ${page >= totalPages ? 'disabled' : ''}>
            <i class="fas fa-chevron-right"></i>
        </button>
    </a>
</div>
