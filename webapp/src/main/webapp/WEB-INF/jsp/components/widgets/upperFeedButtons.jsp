<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<c:if test="${channel != 'Announcements'}">
    <div class="upper-feed-buttons-box ">
        <div class="f-r-s-c">
            <a onclick="hotPosts()" class="cool-feed-button rounded" >
                <spring:message code="Hot"/>
                <i class="fa-solid fa-fire ml-1"></i>
            </a>
            <a onclick="trendingPosts()" class="cool-feed-button rounded" >
                <spring:message code="Trending"/>
                <i class="fa-solid fa-arrow-trend-up ml-1"></i>
            </a>
        </div>


        <a onclick="publishInChannel()" class="cool-feed-button" >
            <spring:message code="CreateNewPost.button"/>
            <i class="fa-solid fa-plus"></i>
        </a>
    </div>
</c:if>


<script>
    function publishInChannel(){
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
