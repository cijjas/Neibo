<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<a href="${pageContext.request.contextPath}/user">
    <div class="grey-container" style="width: 200px; ">
        <div class="row">
            <div class="col-4">
                <div >
                </div>
            </div>
            <div class="col-8">
                <div class="column justify-content-center align-items-start">
                    <h5>${loggedUser.name}</h5>
                    <h6>${loggedUser.surname}</h6>
                </div>
            </div>
        </div>
    </div>
</a>
