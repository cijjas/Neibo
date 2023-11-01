<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<c:if test="${channel != 'Announcements'}">
    <div class="upper-feed-buttons-box  ">
        <div class="f-r-s-c filters g-05" >
            <a onclick="newestPosts()"
                   class="cool-feed-button  rounded ${(param.postStatus == 'none' || param.postStatus eq null) ? 'active' : ''}  ">
                <i class="fa-solid fa-arrow-up-wide-short"></i>
                <spring:message code="Newest"/>
            </a>
            <a onclick="trendingPosts()"
               class="cool-feed-button  rounded ${param.postStatus == 'trending' ? 'active' : ''}  ">
                <i class="fa-solid fa-arrow-trend-up ml-1"></i>
                <spring:message code="Trending"/>
            </a>
            <a onclick="hotPosts()" class="coolest-button  ${param.postStatus == 'hot' ? 'active' : ''}">
                <i class="fa-solid fa-fire ml-1"></i>
                <spring:message code="Hot"/>
            </a>

        </div>
        <c:if test='${loggedUser.role != "ADMINISTRATOR"}'>
            <a onclick="publishInChannel()" class="cool-feed-button p-3 ">
                <i class="fa-solid fa-plus"></i>

                <c:choose>
                    <c:when test="${channel eq 'Feed'}">
                            <spring:message code="Create.post.button"/>
                    </c:when>
                    <c:when test="${channel eq 'Complaints'}">
                            <spring:message code="Create.complaint.button"/>
                    </c:when>
                </c:choose>
            </a>
        </c:if>
    </div>
</c:if>


<script>
    function publishInChannel() {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '${pageContext.request.contextPath}/publish-to-channel'; // Replace with your desired URL

        // Optionally, you can add any form data or parameters here
        // For example, adding a hidden input field with a value
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'channel';
        input.value = '${channel}';

        // Append the input field to the form
        form.appendChild(input);

        // Append the form to the document and submit it
        document.body.appendChild(form);
        form.submit();
    }

    function hotPosts() {
        const currentUrl = new URL(window.location.href);
        currentUrl.searchParams.set('postStatus', 'hot');
        window.location.href = currentUrl.toString();
    }

    function newestPosts(){
        const currentUrl = new URL(window.location.href);
        currentUrl.searchParams.set('postStatus', 'none');
        window.location.href = currentUrl.toString();
    }

    function trendingPosts() {
        const currentUrl = new URL(window.location.href);
        currentUrl.searchParams.set('postStatus', 'trending');
        window.location.href = currentUrl.toString();
    }


    function addQueryParams(url, queryParams) {
        if (url.includes("?")) {
            // URL already has query parameters, so append with '&'
            return url + "&" + queryParams;
        } else {
            // URL doesn't have query parameters, so append with '?'
            return url + "?" + queryParams;
        }
    }


</script>
