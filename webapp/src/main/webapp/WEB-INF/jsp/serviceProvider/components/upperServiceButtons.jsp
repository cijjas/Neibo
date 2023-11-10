<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="w-100" style="position: relative; z-index: 2" >
    <div class="upper-feed-buttons-box"  >

        <h1 style="padding-top: 5px;"><spring:message code="Profession"/></h1>

        <div class="custom-dropdown-alternative">
            <c:choose>
                <c:when test="${profession ==  ''}">
                    <div class="dropdown-toggle"><spring:message code="All"/></div>
                </c:when>
                <c:otherwise>
                    <div class="dropdown-toggle"><spring:message code="${profession}"/></div>
                </c:otherwise>
            </c:choose>
            <ul class="dropdown-options">
                <li data-value="0"><a href="${contextPath}/"><spring:message code="All"/></a></li>
                <c:forEach items="${professionList}" var="profession">
                    <li>
                        <a href="${contextPath}?profession=${profession}"><spring:message code="${profession}" /></a>
                    </li>
                </c:forEach>
            </ul>
        </div>

    </div>
</div>
