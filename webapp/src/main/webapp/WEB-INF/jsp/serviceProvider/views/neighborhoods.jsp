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
    <title><spring:message code="Join.neighborhoods"/></title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/service.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/calendarWidget.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
</head>

<body class="${loggedUser.darkMode ? 'dark-mode' : ''}">


<div style=" position: absolute; width: 100%; height: 100%; top:0; left:0; z-index: 1">

    <%@ include file="/WEB-INF/jsp/components/displays/navbar.jsp" %>
    <div class="container">
        <div class="row">
            <div class="column-left">
                <%@ include file="/WEB-INF/jsp/components/widgets/leftColumn.jsp" %>
            </div>
            <div class="column-middle">
                <div class="cool-static-container mb-4">
                    <c:choose>
                        <c:when test="${empty associatedNeighborhoods}">
                            <div class="f-c-c-c w-100 mt-3" style="text-align: center">
                                <div class="w-75">
                                    <spring:message code="No.associated.neighborhoods"/>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="f-c-s-c mt-2 w-100">
                                <span class="font-weight-bold font-size-20" ><spring:message
                                        code="My.Neighborhoods"/></span>
                                <table class="table-striped w-100 mt-3">
                                    <thead>
                                    <tr>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="entry" items="${associatedNeighborhoods}">
                                        <tr>
                                            <td style="text-align: start;"><span class="pl-2"><c:out
                                                    value="${entry.name}"/></span></td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/services/neighborhoods/remove/${entry.neighborhoodId}"
                                                   class="btn btn-link">
                                                    <i class="fas fa-trash" style="color: var(--error);"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>

                            </div>

                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="cool-static-container " style="overflow: visible">
                    <c:choose>
                        <c:when test="${empty otherNeighborhoods}">
                            <div class="f-c-c-c w-100 mb-3" style="text-align: center">
                                <div class="w-75">
                                    <spring:message code="No.new.neighborhoods"/>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <form:form class="f-c-c-c w-100" method="post" action="neighborhoods" modelAttribute="neighborhoodsForm">
                                <form:errors cssClass="error" element="p"/>

                                <span class="font-weight-bold font-size-20" >
                                    <spring:message code="Join.neighborhoods"/>
                                </span>

                                <div class="neighborhoods-select mb-4 w-100">
                                    <form:hidden path="neighborhoodIds" id="selectedNeighborhoods" name="neighborhoodIds"/>
                                    <div class="f-r-c-c w-100">
                                        <div class="container m-0" >
                                            <div class="select-btn n-workers" style="width: 100%;">
                                                <span class="btn-text" id="neighborhoods-button"><spring:message
                                                        code="Select.neighborhood"/></span>
                                                <span class="arrow-dwn">
                                            <i class="fa-solid fa-chevron-down"></i>
                                        </span>
                                            </div>
                                            <ul class="list-items n-workers" >
                                                <c:forEach var="neighborhood" items="${otherNeighborhoods}">
                                                    <li class="item"
                                                        data-neighborhood-id="${neighborhood.neighborhoodId}">
                                                <span class="checkbox ">
                                                    <i class="fa-solid fa-check check-icon"></i>
                                                </span>
                                                        <span class="item-text"><c:out
                                                                value="${neighborhood.name}"/></span>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                        <button type="submit"
                                                class="cool-button cool-small on-bg font-weight-bold w-25">
                                            <spring:message code="Add"/>
                                            <i class="fa-solid fa-share ml-1"></i>
                                        </button>
                                    </div>
                                </div>
                                <script>
                                    function submitNeighborhoods() {
                                        const form = document.createElement("form");
                                        form.method = "POST";
                                        form.action = "${pageContext.request.contextPath}/services/neighborhoods";
                                        const hiddenField = document.createElement("input");
                                        hiddenField.type = "hidden";
                                        hiddenField.name = "neighborhoodIds";
                                        hiddenField.value = document.getElementById("selectedNeighborhoods").value;
                                        form.appendChild(hiddenField);
                                        document.body.appendChild(form);
                                        form.submit();
                                    }
                                </script>
                                <script>
                                    const btnText = document.querySelector(".btn-text");
                                    const arrow = document.querySelector(".arrow-dwn");
                                    const arrowIcon = document.querySelector(".arrow-dwn i");

                                    const selectBtn = document.querySelector(".select-btn"),
                                        items = document.querySelectorAll(".item"),
                                        selectedProfessions = document.getElementById("selectedNeighborhoods");
                                    const listContainer = document.querySelector(".list-items");

                                    items.forEach(item => {
                                        item.addEventListener("click", () => {
                                            item.classList.toggle("checked");
                                            updateSelectedProfessions();
                                        });
                                    });

                                    function updateSelectedProfessions() {
                                        const checkedItems = document.querySelectorAll(".item.checked");
                                        const selectedProfessionIds = Array.from(checkedItems).map(item => item.getAttribute("data-neighborhood-id"));
                                        const selectedProfessionTexts = Array.from(checkedItems).map(item => item.querySelector(".item-text").innerText);

                                        selectedProfessions.value = selectedProfessionIds.join(",");

                                        const btnText = document.querySelector(".btn-text");
                                        if (selectedProfessionIds.length > 0) {
                                            if (selectedProfessionTexts.length > 1) {
                                                btnText.innerText = "(" + selectedProfessionIds.length + ") " + selectedProfessionTexts.join(", ");
                                            } else {
                                                btnText.innerText = selectedProfessionTexts.join(", ");
                                            }
                                            btnText.classList.add("c-text");

                                        } else {
                                            btnText.classList.remove("c-text");
                                            btnText.innerText = `<spring:message code="Select.neighborhood"/>`;
                                        }
                                    }

                                    selectBtn.addEventListener("click", () => {
                                        selectBtn.classList.toggle("open");
                                    });

                                    // Close the list when clicking outside
                                    document.addEventListener("click", (event) => {
                                        if (!listContainer.contains(event.target) && event.target !== selectBtn && event.target !== btnText && event.target !== arrow && event.target !== arrowIcon) {
                                            selectBtn.classList.remove("open");
                                        }
                                    });

                                    selectBtn.addEventListener("focus", () => {
                                        selectBtn.classList.add("open");
                                    });


                                    // Initial update of selected professions on page load
                                    updateSelectedProfessions();
                                </script>

                                <div class="w-100 f-c-c-c" style="font-size: 14px; text-align: center">
                                    <span class="font-weight-normal">
                                        <spring:message code="Select.neighborhood.message.1"/>
                                    </span>
                                </div>
                            </form:form>
                        </c:otherwise>
                    </c:choose>
                </div>
            <div class="column-right">
                <%@ include file="/WEB-INF/jsp/components/widgets/calendar/calendarWidget.jsp" %>
            </div>
        </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm"
            crossorigin="anonymous"></script>

    <%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>
</div>

</body>

</html>