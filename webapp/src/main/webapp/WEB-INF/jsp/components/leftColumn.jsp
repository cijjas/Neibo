<%@ page contentType="text/html;charset=UTF-8" language="java" %>



<div class="left-column-container">


    <div class="left-box" >
        <a id="announcementsButton" href="${pageContext.request.contextPath}/announcements" class="cool-left-button  ${channel == 'Announcements' ? 'active' : ''}">
            <i class="fas fa-bullhorn"></i>
            <spring:message code="Announcements"/>
        </a>
        <a id="feedButton" href="${pageContext.request.contextPath}/" class="cool-left-button  ${channel == 'Feed' ? 'active' : ''}">
            <i class="fas fa-comments"></i> <spring:message code="Feed"/>
        </a>
        <a id="Forum" href="${pageContext.request.contextPath}/forum" class="cool-left-button ${channel == 'Forum' ? 'active' : ''}" >
            <i class="fas fa-envelope"></i> <spring:message code="Complains"/>
        </a>
    </div>

<%--    <div class="sticky-bottom-button">--%>
<%--        <button id="language-toggle" class="cool-button " >Language</button>--%>
<%--        <button id="dark-mode-toggle" class="cool-button " >Toggle Dark Mode</button>--%>
<%--    </div>--%>


</div>


