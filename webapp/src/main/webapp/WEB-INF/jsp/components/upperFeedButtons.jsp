<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<script>
    // JavaScript function to toggle button selection
    function toggleButtonSelection(buttonId) {
        const buttons = document.querySelectorAll('.filter-button');

        buttons.forEach(function (button) {
            if (button.id === buttonId) {
                button.classList.add('selected');
            } else {
                button.classList.remove('selected');
            }
        });
    }
</script>



<div class="upper-feed-buttons-box ">
    <div class="d-flex flex-wrap">
        <a href="?sortBy=datedesc" class="filter-button selected">
                <i class="fa-solid fa-newspaper"></i>
                <span><spring:message code="Latest"/></span>
        </a>

        <a class="dropdown-toggle filter-button" href="#" id="dropdown07XL" data-bs-toggle="dropdown" aria-expanded="false">Tag</a>
        <ul class="dropdown-menu" aria-labelledby="filter-by-tag">
            <c:forEach var="tag" items="${tagList}">
                <li><a class="dropdown-item" href="?page=1&sortBy=tag${tag.tag}" data-tag="${tag.tag}">#${tag.tag}</a></li>
            </c:forEach>
        </ul>
<%--
        <div class="form-row">
            <label>
                <select  class="cool-input"  >
                    <c:forEach var="entry" items="${tagList}">
                        <option>${entry.tag}</option>
                    </c:forEach>
                </select>
            </label>
        </div>
--%>
    </div>
    <a href="${pageContext.request.contextPath}/publish" class="filter-button" >
        <spring:message code="CreateNewPost.button"/>
        <i class="fa-solid fa-plus"></i>
    </a>
</div>
