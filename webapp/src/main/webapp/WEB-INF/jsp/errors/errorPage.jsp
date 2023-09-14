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

<html lang="en">

<%@ include file="/WEB-INF/jsp/components/head.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/error.css"/>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <title>${errorCode} - Error Page</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet"/>
    <link rel="icon" href="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDU1IiBoZWlnaHQ9IjEwNTUiIHZpZXdCb3g9IjAgMCAxMDU1IDEwNTUiIGZpbGw9Im5vbmUiPgo8cGF0aCBkPSJNNDM5IDcxMy41QzMzNS40NzEgNzA3Ljc0OCAyNTAuMzczIDc5OC4xNTQgMjE0LjczMSA4NTAuMDU0QzIxMi43NzkgODUyLjg5NiAyMDguMjY2IDg1MC44ODcgMjA5LjMyMSA4NDcuNjA1QzI0MS4zNDQgNzQ4LjAyNCAzMzkuOTkxIDY2NS4xNDcgMzk5LjUgNjYwQzQ5MiA2NTIgNTExLjc4NiA2ODQuNjk4IDY3MCA2OTJDNzcyLjg5IDY5Ni43NDkgODI5LjU1IDY2NS42NTUgODY0LjgyNCA2MzIuNzk2Qzg2Ny41NDIgNjMwLjI2NSA4NzQuMDAzIDYzNC45MzMgODcyLjAyOCA2MzguMDc5Qzg0Ni4zMDUgNjc5LjA2MyA3OTkuMDI4IDczMiA2OTkgNzMyQzU0NC41NzEgNzMyIDU5MiA3MjIgNDM5IDcxMy41WiIgZmlsbD0iIzlhZDM3OSIvPgo8cGF0aCBkPSJNNDgxIDgwNC42NjNDNDE2LjQ3MyA3OTguOTQxIDM1Mi42MzcgODM3LjUxNyAzMTguMTg4IDg3My4wM0MzMTUuNjE5IDg3NS42NzkgMzA4LjE1MSA4NzEuNDczIDMwOS44MjIgODY4LjE4MkMzNDEuMDM5IDgwNi42ODYgMzk2Ljc1OCA3NzIuNjQ1IDQzNS41IDc2OC4xNjNDNDk2IDc2MS4xNjMgNDk5LjUzIDc3MS41NDcgNjEyLjUgNzkzLjY2M0M2OTQuMDY3IDgwOS42MzEgNzIwLjUwMyA4MDQuNzM4IDc0NS41MDIgNzg1LjczOEM3NDguMTM1IDc4My43MzcgNzUxLjkyNCA3ODYuNjkxIDc1MC4yNzcgNzg5LjU1OEM3MzYuMTQyIDgxNC4xNjkgNzExLjc0OCA4MzcuMjU2IDYzMSA4MjcuMTYzQzUyMC43MjIgODEzLjM3OCA1ODIuNSA4MTMuNjYzIDQ4MSA4MDQuNjYzWiIgZmlsbD0iIzlhZDM3OSIvPgo8cGF0aCBkPSJNNDE4LjUgNjE2LjVDMzExLjQ4OCA1OTkuMzQ4IDIwNi4wMTEgNzEwLjkxMyAxNzAuNTkzIDc1Ni42MzhDMTY4LjcxOCA3NTkuMDU5IDE2NC43NzMgNzU3LjQ5MyAxNjUuMTI4IDc1NC40NTJDMTc5LjQ5IDYzMS40MDUgMjQ3LjQ2IDU3Ni4wMjggMzMxIDU2MEM0MTcgNTQzLjUgNDY1LjEyNCA1ODUuMzQ2IDYyMyA1OThDNzIzLjQ0NSA2MDYuMDUxIDgwNy43MjIgNTk0LjUwNSA4NTUuOTk3IDU3MC41NzRDODU5LjMzNSA1NjguOTE5IDg2Ni43NjQgNTc2LjQ0MSA4NjQuMTgzIDU3OS4xMjdDODMxLjA0MiA2MTMuNjIyIDc3NC41NzMgNjQzLjUgNjczIDY0My41QzUxOC41NzEgNjQzLjUgNTYyIDYzOS41IDQxOC41IDYxNi41WiIgZmlsbD0iIzlhZDM3OSIvPgo8cGF0aCBkPSJNNzE4LjY3MSAzODAuNTA4QzY4Ni4yNzEgNDU0LjEwOCA2OTIuOTQ0IDU0MC41MDggNjk3LjI3NyA1NzQuNTA4QzY4MS4yNzcgNTc0LjUwOCA2NDMuMjc3IDU4MS41MDggNTczLjE3MSA1NjcuNTA4QzQ1MS4yNzcgNTM4LjUwOCA0NDEuNzc3IDUyMS41MDggMzI5LjY3MSA1MjcuMDA4QzMyMi41MiA1MjcuMzU5IDMyNy42NzEgNDg2LjUwOCAzMjkuNjcxIDQ2MS4wMDhDMzMxLjY3MSA0MzUuNTA4IDM1NC4xNzEgNDA2LjUwOCAzNzkuNjcxIDM4MC41MDhDNDA1LjE3MSAzNTQuNTA4IDQ1MC4xNzEgMzM1LjAwOCA0ODAuNjcxIDMzMC41MDhDNTExLjE3MSAzMjYuMDA4IDUyMi4xNzEgMjcxLjAwOCA1MzcuNjcxIDI0MC4wMDhDNTUzLjE3MSAyMDkuMDA4IDY1My4zMjYgMTU3Ljg1MiA3MDcuMTcxIDE4Mi41MDhDNzczLjc3NyAyMTMuMDA4IDgyNS4xNzEgMjUxLjAwOCA4MzkuNjcxIDI2NC41MDhDODU0LjE3MSAyNzguMDA4IDg0OC4xNzEgMjkyLjUwOCA4MzQuNjcxIDMzNS4wMDhDODIzLjg3MSAzNjkuMDA4IDgxMC41MDQgMzc5LjUwOCA4MDUuMTcxIDM4MC41MDhDNzYyLjc3MSAzOTIuMTA4IDcyOS44MzcgMzg1LjM0MSA3MTguNjcxIDM4MC41MDhaIiBmaWxsPSIjOWFkMzc5Ii8+CjxlbGxpcHNlIGN4PSI1OTYuNDI4IiBjeT0iMTk5LjM0MyIgcng9IjM2LjUiIHJ5PSIzMi41IiB0cmFuc2Zvcm09InJvdGF0ZSgzMy45NDcxIDU5Ni40MjggMTk5LjM0MykiIGZpbGw9IiM5YWQzNzkiLz4KPC9zdmc+">
</head>

<body class="body">
    <%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>


    <!-- Error Card -->
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="error-card">
                    <div class="card-body ">
                        <div class="flex-container">
                            <h1 class="error-card-title">${errorCode}</h1>

                            <svg width="170" height="170" viewBox="0 0 771 771" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M234.236 390.397C271.466 474.967 235.577 635.287 218.584 673.535C235.577 683.264 349.063 733.237 429.62 717.151C569.682 683.828 623.765 621.363 674.039 553.758C681.274 539.504 673.746 517.645 671.448 488.344C669.149 459.043 653.066 420.272 623.765 390.397C594.464 360.521 542.757 338.115 507.711 332.944C472.665 327.773 460.025 264.575 442.215 228.955C424.404 193.334 309.321 134.553 247.45 162.884C170.916 197.93 111.862 241.594 95.2007 257.107C78.5395 272.619 89.3346 297.914 104.847 346.749C111.41 346.972 117.656 347.204 123.595 347.444C144.194 350.449 174.023 357.86 173.291 363.356C172.217 371.409 116.22 365.369 113.57 366.376C125.052 381.896 128.323 384.122 130.201 385.155L134.843 390.397C183.563 403.726 221.405 395.951 234.236 390.397Z" fill="#DE6F6F"/>
                                <path d="M339.908 205.649C351.425 222.759 376.339 226.143 395.554 213.208C414.769 200.273 421.009 175.917 409.492 158.808C397.974 141.699 373.061 138.315 353.846 151.25C334.631 164.185 328.391 188.54 339.908 205.649Z" fill="#DE6F6F"/>
                                <path fill-rule="evenodd" clip-rule="evenodd" d="M385.5 739C580.733 739 739 580.733 739 385.5C739 190.267 580.733 32 385.5 32C190.267 32 32 190.267 32 385.5C32 580.733 190.267 739 385.5 739ZM385.5 771C598.406 771 771 598.406 771 385.5C771 172.594 598.406 0 385.5 0C172.594 0 0 172.594 0 385.5C0 598.406 172.594 771 385.5 771Z" fill="#DE6F6F"/>
                            </svg>

                            <h3 class="error-message">
                                <span class="font-weight-bold"><spring:message code="Error"/></span> ${errorMsg} <!-- Replace with your error message -->
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