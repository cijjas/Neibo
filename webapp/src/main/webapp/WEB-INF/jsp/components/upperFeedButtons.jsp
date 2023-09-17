<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="filter-options d-flex justify-content-between flex-wrap">
    <div class="d-flex flex-wrap">
        <a href="${pageContext.request.contextPath}/?sortBy=dateasc" style="text-decoration: none;">
            <button id="filter-by-date" class="filter-button selected" onclick="toggleButtonSelection('filter-by-date')">
                <i class="fa-solid fa-newspaper"></i>
                <span><spring:message code="Date"/></span>
            </button>
        </a>

        <button id="filter-by-tag" class="filter-button" onclick="toggleButtonSelection('filter-by-tag')">
            <a class="dropdown-toggle" href="#" id="dropdown07XL" data-bs-toggle="dropdown" aria-expanded="false">Tag</a>
            <ul class="dropdown-menu" aria-labelledby="filter-by-tag">
                <c:forEach var="tag" items="${tagList}">
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/?page=1&sortBy=tag${tag.tag}" data-tag="${tag.tag}">#${tag.tag}</a></li>
                </c:forEach>
            </ul>
        </button>
    </div>
    <a href="${pageContext.request.contextPath}/publish" style="text-decoration: none;">
        <button id="create-post-button" class="filter-button">
            <spring:message code="CreateNewPost.button"/>
            <i class="fa-solid fa-plus"></i>
        </button>
    </a>
</div>
