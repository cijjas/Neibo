<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html lang="en">


<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/calendarWidget.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
    <title><spring:message code="CreateNewAmenity.button"/></title>
</head>


<body class="body ${loggedUser.darkMode ? 'dark-mode' : ''}">
<%@ include file="/WEB-INF/jsp/components/displays/navbar.jsp" %>
<div class="container">
    <div class="row init">
        <div class="column-left">
            <%@ include file="/WEB-INF/jsp/admin/components/controlPanelLeftButtons.jsp" %>
        </div>

        <div class="column-middle">
            <div class="cool-static-container shifts-reservation ">
                <h2 class="card-title"><spring:message code="CreateNewAmenity.button"/></h2>
                <div class="divider"></div>
                <div>
                    <form:form method="post" action="${pageContext.request.contextPath}/admin/create-amenity"
                               modelAttribute="amenityForm">
                        <div class="d-flex flex-column justify-content-between align-items-center mb-2 mt-2">
                            <div class="form-row">
                                <spring:message code="Name" var="namePlaceholder"/>
                                <form:input path="name" class="cool-input" placeholder="${namePlaceholder}"/>
                                <div class="form-row form-error">
                                    <form:errors path="name" cssClass="error" element="p"/>
                                </div>
                            </div>
                        </div>

                        <div class="d-flex flex-column justify-content-between align-items-center">
                            <spring:message code="Description" var="descriptionPlaceholder"/>
                            <form:textarea path="description" class="cool-input textarea-min-max" rows="5"
                                           placeholder="${descriptionPlaceholder}"/>
                            <div class="form-row form-error">
                                <form:errors path="description" cssClass="error" element="p"/>
                            </div>
                        </div>
                        <p class="m-t-40"><spring:message code="AmenityHours"/></p>
                        <div class="divider"></div>


                        <div class="f-c-c-c w-100 m-t-40">
                            <form:errors cssClass="error" element="p"/>
                        </div>

                        <!-- Your other form elements here -->
                        <div class="f-r-sb-c w-100 pl-3 pr-3">
                            <a onclick="check9to5()" class="w-100 cool-button cool-small on-bg">9-17</a>
                            <a onclick="uncheckWeekends()" class="w-100 cool-button red "><spring:message
                                    code="Clear.weekend"/></a>

                            <a onclick="clearAllCheckedHours()" class="w-100 cool-button red "><spring:message
                                    code="Clear.all"/></a>
                        </div>

                        <div class="cool-table w-100 p-4">

                            <table>
                                <tr>
                                    <th><spring:message code="Row"/></th>
                                    <th><spring:message code="Monday.abbr"/></th>
                                    <th><spring:message code="Tuesday.abbr"/></th>
                                    <th><spring:message code="Wednesday.abbr"/></th>
                                    <th><spring:message code="Thursday.abbr"/></th>
                                    <th><spring:message code="Friday.abbr"/></th>
                                    <th><spring:message code="Saturday.abbr"/></th>
                                    <th><spring:message code="Sunday.abbr"/></th>
                                </tr>

                                <c:forEach items="${timesPairs}" var="time">
                                    <tr>
                                        <td>
                                            <label class="container">
                                                <input type="checkbox" onclick="toggleRow(this)"/>
                                            </label>
                                        </td>
                                        <c:forEach items="${daysPairs}" var="day">
                                            <td>
                                                <div class="cat creation">
                                                    <label class="w-100">
                                                        <input type="checkbox" name="selectedShifts"
                                                               value="${day.key}-${time.key}"/>
                                                        <span>${time.value.key}</span>
                                                    </label>
                                                </div>
                                            </td>
                                        </c:forEach>
                                    </tr>

                                </c:forEach>


                            </table>
                            <script>
                                const daysInAWeek = 7;
                                const nineAm = 9;
                                const fivePm = 17;
                                const monday9am = daysInAWeek * nineAm;
                                const sunday5pm = daysInAWeek * fivePm - 1;

                                function check9to5() {
                                    // Loop through the checkboxes and check them for hours between 9 am (index 18) and 7 pm (index 30).
                                    var checkboxes = document.querySelectorAll('input[name="selectedShifts"]');
                                    checkboxes.forEach(function (checkbox, index) {
                                        if (index >= monday9am && index <= sunday5pm) {
                                            if (index % daysInAWeek === 5 || index % daysInAWeek === 6) {
                                                return;
                                            }
                                            checkbox.checked = true;
                                        }
                                    });

                                    // Check row-selection checkboxes
                                    var rowCheckboxes = document.querySelectorAll('td:first-child input[type="checkbox"]');
                                    rowCheckboxes.forEach(function (checkbox, index) {
                                        if (index >= nineAm && index <= fivePm - 1) {
                                            checkbox.checked = true;
                                        }
                                    });
                                    checkCheckboxes()
                                }

                                function clearAllCheckedHours() {
                                    var checkboxes = document.querySelectorAll('input[name="selectedShifts"]');
                                    checkboxes.forEach(function (checkbox) {
                                        checkbox.checked = false;
                                    });

                                    // Uncheck row-selection checkboxes
                                    var rowCheckboxes = document.querySelectorAll('td:first-child input[type="checkbox"]');
                                    rowCheckboxes.forEach(function (checkbox) {
                                        checkbox.checked = false;
                                    });
                                    checkCheckboxes()
                                }

                                function toggleRow(checkbox) {
                                    checkbox.checked = !checkbox.checked;
                                    var row = checkbox.parentElement.parentElement.parentElement;
                                    var checkboxes = row.querySelectorAll('input[type="checkbox"]');
                                    checkboxes.forEach(function (checkbox) {

                                        checkbox.checked = !checkbox.checked;
                                    });
                                    checkCheckboxes()
                                }

                                function uncheckWeekends() {
                                    var checkboxes = document.querySelectorAll('input[name="selectedShifts"]');
                                    checkboxes.forEach(function (checkbox, index) {
                                        if (index % daysInAWeek === 5 || index % daysInAWeek === 6) {
                                            checkbox.checked = false;
                                        }
                                    });
                                    checkCheckboxes()
                                }

                                function checkCheckboxes() {
                                    const checkboxes = document.querySelectorAll('input[name="selectedShifts"]');
                                    const submitButton = document.getElementById("submit-checks");

                                    let anyCheckboxChecked = false;
                                    checkboxes.forEach(function (checkbox) {
                                        if (checkbox.checked) {
                                            anyCheckboxChecked = true;

                                        }
                                    });

                                    submitButton.disabled = !anyCheckboxChecked;
                                }

                                // Attach an event listener to the checkboxes to call the checkCheckboxes function
                                const checkboxes = document.querySelectorAll('input[name="selectedShifts"]');
                                checkboxes.forEach(function (checkbox) {
                                    checkbox.addEventListener('change', checkCheckboxes);
                                });

                                // Call checkCheckboxes initially to set the initial state of the submit button
                                checkCheckboxes();
                            </script>
                        </div>

                        <div class="f-c-c-c">
                            <div class="f-r-c-c" style="gap: 5px">
                                <spring:message code="Remember.message.1"/>
                                <span class="c-primary font-weight-bolder ">
                                    <spring:message code="Remember.message.2"/>
                                </span>
                                <spring:message code="Remember.message.3"/>

                            </div>
                        </div>

                        <%--Submit button --%>
                        <div class="d-flex justify-content-end m-t-40 ">
                            <button id="submit-checks" onclick="load()" type="submit"
                                    class="cool-button cool-small on-bg w-25" disabled style="height:40px;">
                                <spring:message code="Create.verb"/></button>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>

        <div class="column-right">
            <%@ include file="/WEB-INF/jsp/components/widgets/calendar/calendarWidget.jsp" %>
        </div>
    </div>
</div>
<script>
    function load() {
        document.getElementById("loader-container").style.display = "flex";
    }
</script>


<div id="loader-container" class="loader-container">
    <div class="cool-static-container medium-size-container">
        <div style="font-weight: bold; font-size: 16px"><spring:message code="Creating.amenity"/>...</div>
        <div class="loader" style="margin-top: 20px"></div>
    </div>
</div>


<!-- Bootstrap JS and jQuery -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm"
        crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>
<script>
    const wave3= document.getElementById('wave3');
    const wave2= document.getElementById('wave2');
    wave2.classList.add('admin');
    wave3.classList.add('admin');
</script>
</body>
</html>
