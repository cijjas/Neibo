<c:forEach var="amenity" items="${amenities}">
    <div class="cool-static-container m-b-20" style="word-wrap: break-word;" aria-hidden="true">

        <c:choose>
            <c:when test='${loggedUser.role == "ADMINISTRATOR"}'>
                <div class="row">
                    <div class="col-md-8">
                        <h2><c:out value="${amenity.name}" /></h2>
                    </div>
                    <div class="col-md-4 text-right">
                        <a href="${pageContext.request.contextPath}/admin/delete-amenity/${amenity.amenityId}" class="btn btn-link">
                            <i class="fas fa-trash" style="color: var(--error);"></i>
                        </a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div >
                    <h2><c:out value="${amenity.name}" /></h2>
                </div>
            </c:otherwise>
        </c:choose>
        <p class="mb-3" style="color:var(--lighttext);"><c:out value="${amenity.description}"/></p>

        <div class="d-flex flex-column justify-content-center align-items-center w-100">
            <div class="cool-table w-100 ">
                <table class="table-striped w-100">
                    <tr>
                        <th><spring:message code="AmenityHours"/></th>
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
                                <span>${time.value.key}</span>
                            </td>
                            <c:forEach items="${daysPairs}" var="day">
                                <td>
                                    <c:set var="available" value="false"/>
                                    <c:forEach items="${amenity.availableShifts}" var="shift">
                                        <c:if test="${shift.day == day.value && shift.startTime == time.value.value}">
                                            <c:set var="available" value="true"/>
                                            <span style="color: var(--primary);" class="col-12">
                                                                <i class="fa-solid fa-check"></i>
                                                            </span>
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${!available}">
                                                        <span style="color: var(--error);" class="col-12">
                                                            <i class="fa-solid fa-xmark"></i>
                                                        </span>
                                    </c:if>
                                </td>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </div>
    </div>
</c:forEach>