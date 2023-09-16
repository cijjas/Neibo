<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="page-content page-container" id="page-content">
    <div class="padding">
        <div class="row container d-flex justify-content-center">
            <div class="col-md-12 col-sm-6 grid-margin stretch-card">
                <nav>
                    <ul class="pagination d-flex justify-content-center flex-wrap pagination-flat pagination-success">
                        <li class="page-item">
                            <c:choose>
                                <c:when test="${page > 1}">
                                    <a class="page-link" href="?page=${page - 1}" data-abc="true"><i class="fa fa-angle-left"></i></a>
                                </c:when>
                                <c:otherwise>
                                    <span class="page-link disabled" aria-disabled="true"><i class="fa fa-angle-left"></i></span>
                                </c:otherwise>
                            </c:choose>
                        </li>

                        <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                            <li class="page-item ${page == pageNumber ? 'active' : ''}">
                                <c:choose>
                                    <c:when test="${page == pageNumber}">
                                        <span class="page-link">${pageNumber}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="page-link" href="?page=${pageNumber}" data-abc="true">${pageNumber}</a>
                                    </c:otherwise>
                                </c:choose>
                            </li>
                        </c:forEach>

                        <li class="page-item">
                            <c:choose>
                                <c:when test="${page < totalPages}">
                                    <a class="page-link" href="?page=${page + 1}" data-abc="true"><i class="fa fa-angle-right"></i></a>
                                </c:when>
                                <c:otherwise>
                                    <span class="page-link disabled" aria-disabled="true"><i class="fa fa-angle-right"></i></span>
                                </c:otherwise>
                            </c:choose>
                        </li>
                    </ul>
                </nav>
            </div>
        </div>
    </div>
</div>
