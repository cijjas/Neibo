<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="currentUrl" value="${pageContext.request.requestURL}"/>

<div class="pagination">
    <c:url var="prevUrl" value="${pageContext.request.contextPath}/">
        <c:param name="page" value="${page - 1}" />
        <c:if test="${sortBy != null}">
            <c:param name="sortBy" value="${sortBy}" />
        </c:if>
    </c:url>
    <c:choose>
        <c:when test="${page > 1}">
            <a href="${prevUrl}" class="small-a">
                <button type="button" class="pagination-button small-button">
                    <i class="fas fa-chevron-left"></i>
                </button>
            </a>
        </c:when>
        <c:otherwise>
            <button type="button" class="pagination-button small-button" disabled>
                <i class="fas fa-chevron-left"></i>
            </button>
        </c:otherwise>
    </c:choose>


    <c:choose>
        <c:when test="${totalPages >= 1}">
            <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                <c:url var="pageUrl" value="${pageContext.request.contextPath}/">
                    <c:param name="page" value="${pageNumber}" />
                    <c:if test="${sortBy != null}">
                        <c:param name="sortBy" value="${sortBy}" />
                    </c:if>
                </c:url>
                <a href="${pageUrl}" data-abc="true" class="${page == pageNumber ? 'active' : ''}">
                    <button type="button" class="pagination-button ${page == pageNumber ? 'active' : ''}">
                            ${pageNumber}
                    </button>
                </a>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <!-- Handle the case when totalPages is 1 or 0 -->
        </c:otherwise>
    </c:choose>

    <c:url var="nextUrl" value="${pageContext.request.contextPath}/">
        <c:param name="page" value="${page + 1}" />
        <c:if test="${sortBy != null}">
            <c:param name="sortBy" value="${sortBy}" />
        </c:if>
    </c:url>
    <c:choose>
        <c:when test="${page < totalPages}">
            <a href="${nextUrl}" class="small-a">
                <button type="button" class="pagination-button small-button">
                    <i class="fas fa-chevron-right"></i>
                </button>
            </a>
        </c:when>
        <c:otherwise>
            <button type="button" class="pagination-button small-button" disabled>
                <i class="fas fa-chevron-right"></i>
            </button>
        </c:otherwise>
    </c:choose>

</div>
