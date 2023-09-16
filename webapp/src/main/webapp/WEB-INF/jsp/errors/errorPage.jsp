<%--
  Created by IntelliJ IDEA.
  User: chrij
  Date: 9/12/2023
  Time: 1:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ page isErrorPage="true" %>
<html lang="en">

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/error.css"/>

<%@ include file="/WEB-INF/jsp/components/head.jsp" %>

<body class="body">
<%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>

<!-- Error Card -->
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="error-card">
                <div class="card-body ">
                    <div class="flex-container">
                        <h1 class="error-card-title">
                            <c:choose>
                                <c:when test="${not empty errorMsg}">
                                    ${errorCode}
                                </c:when>
                                <c:otherwise>
                                    <%= request.getAttribute("javax.servlet.error.status_code") %>
                                </c:otherwise>
                            </c:choose>
                        </h1>

                        <svg width="170" height="170" viewBox="0 0 771 771" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M234.236 390.397C271.466 474.967 235.577 635.287 218.584 673.535C235.577 683.264 349.063 733.237 429.62 717.151C569.682 683.828 623.765 621.363 674.039 553.758C681.274 539.504 673.746 517.645 671.448 488.344C669.149 459.043 653.066 420.272 623.765 390.397C594.464 360.521 542.757 338.115 507.711 332.944C472.665 327.773 460.025 264.575 442.215 228.955C424.404 193.334 309.321 134.553 247.45 162.884C170.916 197.93 111.862 241.594 95.2007 257.107C78.5395 272.619 89.3346 297.914 104.847 346.749C111.41 346.972 117.656 347.204 123.595 347.444C144.194 350.449 174.023 357.86 173.291 363.356C172.217 371.409 116.22 365.369 113.57 366.376C125.052 381.896 128.323 384.122 130.201 385.155L134.843 390.397C183.563 403.726 221.405 395.951 234.236 390.397Z" fill="#DE6F6F"/>
                            <path d="M339.908 205.649C351.425 222.759 376.339 226.143 395.554 213.208C414.769 200.273 421.009 175.917 409.492 158.808C397.974 141.699 373.061 138.315 353.846 151.25C334.631 164.185 328.391 188.54 339.908 205.649Z" fill="#DE6F6F"/>
                            <path fill-rule="evenodd" clip-rule="evenodd" d="M385.5 739C580.733 739 739 580.733 739 385.5C739 190.267 580.733 32 385.5 32C190.267 32 32 190.267 32 385.5C32 580.733 190.267 739 385.5 739ZM385.5 771C598.406 771 771 598.406 771 385.5C771 172.594 598.406 0 385.5 0C172.594 0 0 172.594 0 385.5C0 598.406 172.594 771 385.5 771Z" fill="#DE6F6F"/>
                        </svg>

                        <h3 class="error-message">
                            <span class="font-weight-bold"><spring:message code="Error"/>: </span>
                            <c:choose>
                                <c:when test="${not empty errorMsg}">
                                    ${errorMsg}
                                </c:when>
                                <c:otherwise>
                                    <%= request.getAttribute("javax.servlet.error.message") %>
                                </c:otherwise>
                            </c:choose>
                        </h3>


                        <svg class="error-wave" width="3032" height="331" viewBox="0 0 3032 331" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M607 154.344V330.344H2123V154.344C2053 177.344 1875.2 225.644 1724 234.844C1535 246.344 1446 59.8436 1211.5 8.34356C1023.9 -32.8564 730.333 88.5102 607 154.344Z" fill="#FF8888"/>
                            <path d="M607 154.344V330.344H2123V154.344C2053 177.344 1875.2 225.644 1724 234.844C1535 246.344 1446 59.8436 1211.5 8.34356C1023.9 -32.8564 730.333 88.5102 607 154.344Z" fill="url(#paint0_radial_0_1)" fill-opacity="0.2"/>
                            <path d="M2123 154.344V330.344H3032V166.231C2947.46 110.834 2861.09 37.6822 2727.5 8.34356C2539.9 -32.8564 2246.33 88.5102 2123 154.344Z" fill="#FF8888"/>
                            <path d="M2123 154.344V330.344H3032V166.231C2947.46 110.834 2861.09 37.6822 2727.5 8.34356C2539.9 -32.8564 2246.33 88.5102 2123 154.344Z" fill="url(#paint1_radial_0_1)" fill-opacity="0.2"/>
                            <path d="M1516 154.344V330.344H3032V154.344C2962 177.344 2784.2 225.644 2633 234.844C2444 246.344 2392 79.5001 2157.5 28.0001C1969.9 -13.2 1656.5 105 1516 154.344Z" fill="#DE6F6F"/>
                            <path d="M1516 154.344V330.344H3032V154.344C2962 177.344 2784.2 225.644 2633 234.844C2444 246.344 2392 79.5001 2157.5 28.0001C1969.9 -13.2 1656.5 105 1516 154.344Z" fill="#DE6F6F"/>
                            <path d="M0 154.344V330.344H1516V154.344C1446 177.344 1268.2 225.644 1117 234.844C928 246.344 876 79.5001 641.5 28.0001C453.9 -13.2 140.5 105 0 154.344Z" fill="#DE6F6F"/>
                            <defs>
                                <radialGradient id="paint0_radial_0_1" cx="0" cy="0" r="1" gradientUnits="userSpaceOnUse" gradientTransform="translate(1686 208.5) rotate(177.946) scale(837.038 3841.3)">
                                    <stop stop-color="#FF0000"/>
                                    <stop offset="1" stop-color="white" stop-opacity="0"/>
                                </radialGradient>
                                <radialGradient id="paint1_radial_0_1" cx="0" cy="0" r="1" gradientUnits="userSpaceOnUse" gradientTransform="translate(3202 208.5) rotate(177.946) scale(837.038 3841.3)">
                                    <stop stop-color="#FF0000"/>
                                    <stop offset="1" stop-color="white" stop-opacity="0"/>
                                </radialGradient>
                            </defs>
                        </svg>

                            <div class="options">
                            <a href="${pageContext.request.contextPath}/" class="goback-button"><spring:message code="GoBackToMainPage"/></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</div>

</body>
</html>
