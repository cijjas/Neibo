<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
        <title>Neibo - <spring:message  code="Calendar"/>r</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
        <link rel="preconnect" href="https://fonts.gstatic.com">
        <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
        <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
    </head>

    <body class="${loggedUser.darkMode ? 'dark-mode' : ''}">
        <%@ include file="/WEB-INF/jsp/components/displays/navbar.jsp" %>
        <div class="container f-c-c-c" >
            <div class="row w-100">
                <div class="col-lg-8  w-100 pl-0">
                    <%@ include file="/WEB-INF/jsp/components/widgets/calendar/calendarBox.jsp" %>
                </div>
                <div class="col-lg-4  h-100 w-100 pr-0">
                    <div class="f-c-c-c">
                        <div class="upper-feed-buttons-box w-100" style="padding: 20px">
                            <div class="f-c-c-c w-100">
                                <div>
                                    <p style=" font-size: 32px;color: var(--primary); text-align: center">
                                        <spring:message code="Events"/>

                                    </p>
                                    <p style=" font-size: 16px;">
                                        <spring:message code="date.format">
                                            <spring:argument value="${selectedDay}"/>
                                            <spring:argument value="${selectedMonth}"/>
                                            <spring:argument value="${fn:replace(selectedYear, '.', '')}" />
                                        </spring:message>
                                    </p>
                                </div>
                                <div class="divider m-b-20 "></div>
                                <%@include file="/WEB-INF/jsp/components/widgets/calendar/eventsIndex.jsp"%>

                            </div>

                        </div>
                    </div>

                </div>

            </div>

        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>

        <%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>
    </body>

</html>
