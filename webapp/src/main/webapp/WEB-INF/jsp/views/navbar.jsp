<%--
  Created by IntelliJ IDEA.
  User: chrij
  Date: 9/9/2023
  Time: 3:51 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<nav class="navbar navbar-expand-lg custom-navbar">
    <div class="container-fluid">
        <c:url var="logoImage" value="/resources/images/logo_neibo.svg"/>

        <a class="navbar-brand" href="#">
            <img src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDU1IiBoZWlnaHQ9IjEwNTUiIHZpZXdCb3g9IjAgMCAxMDU1IDEwNTUiIGZpbGw9Im5vbmUiIHRyYW5zZm9ybT0ibWF0cml4KDEsIDAsIDAsIDEsIDAsIDApIj4KPHBhdGggZD0iTTQzOSA3MTMuNUMzMzUuNDcxIDcwNy43NDggMjUwLjM3MyA3OTguMTU0IDIxNC43MzEgODUwLjA1NEMyMTIuNzc5IDg1Mi44OTYgMjA4LjI2NiA4NTAuODg3IDIwOS4zMjEgODQ3LjYwNUMyNDEuMzQ0IDc0OC4wMjQgMzM5Ljk5MSA2NjUuMTQ3IDM5OS41IDY2MEM0OTIgNjUyIDUxMS43ODYgNjg0LjY5OCA2NzAgNjkyQzc3Mi44OSA2OTYuNzQ5IDgyOS41NSA2NjUuNjU1IDg2NC44MjQgNjMyLjc5NkM4NjcuNTQyIDYzMC4yNjUgODc0LjAwMyA2MzQuOTMzIDg3Mi4wMjggNjM4LjA3OUM4NDYuMzA1IDY3OS4wNjMgNzk5LjAyOCA3MzIgNjk5IDczMkM1NDQuNTcxIDczMiA1OTIgNzIyIDQzOSA3MTMuNVoiIGZpbGw9IiNlYmVhZTkiLz4KPHBhdGggZD0iTTQ4MSA4MDQuNjYzQzQxNi40NzMgNzk4Ljk0MSAzNTIuNjM3IDgzNy41MTcgMzE4LjE4OCA4NzMuMDNDMzE1LjYxOSA4NzUuNjc5IDMwOC4xNTEgODcxLjQ3MyAzMDkuODIyIDg2OC4xODJDMzQxLjAzOSA4MDYuNjg2IDM5Ni43NTggNzcyLjY0NSA0MzUuNSA3NjguMTYzQzQ5NiA3NjEuMTYzIDQ5OS41MyA3NzEuNTQ3IDYxMi41IDc5My42NjNDNjk0LjA2NyA4MDkuNjMxIDcyMC41MDMgODA0LjczOCA3NDUuNTAyIDc4NS43MzhDNzQ4LjEzNSA3ODMuNzM3IDc1MS45MjQgNzg2LjY5MSA3NTAuMjc3IDc4OS41NThDNzM2LjE0MiA4MTQuMTY5IDcxMS43NDggODM3LjI1NiA2MzEgODI3LjE2M0M1MjAuNzIyIDgxMy4zNzggNTgyLjUgODEzLjY2MyA0ODEgODA0LjY2M1oiIGZpbGw9IiNlYmVhZTkiLz4KPHBhdGggZD0iTTQxOC41IDYxNi41QzMxMS40ODggNTk5LjM0OCAyMDYuMDExIDcxMC45MTMgMTcwLjU5MyA3NTYuNjM4QzE2OC43MTggNzU5LjA1OSAxNjQuNzczIDc1Ny40OTMgMTY1LjEyOCA3NTQuNDUyQzE3OS40OSA2MzEuNDA1IDI0Ny40NiA1NzYuMDI4IDMzMSA1NjBDNDE3IDU0My41IDQ2NS4xMjQgNTg1LjM0NiA2MjMgNTk4QzcyMy40NDUgNjA2LjA1MSA4MDcuNzIyIDU5NC41MDUgODU1Ljk5NyA1NzAuNTc0Qzg1OS4zMzUgNTY4LjkxOSA4NjYuNzY0IDU3Ni40NDEgODY0LjE4MyA1NzkuMTI3QzgzMS4wNDIgNjEzLjYyMiA3NzQuNTczIDY0My41IDY3MyA2NDMuNUM1MTguNTcxIDY0My41IDU2MiA2MzkuNSA0MTguNSA2MTYuNVoiIGZpbGw9IiNlYmVhZTkiLz4KPHBhdGggZD0iTTcxOC42NzEgMzgwLjUwOEM2ODYuMjcxIDQ1NC4xMDggNjkyLjk0NCA1NDAuNTA4IDY5Ny4yNzcgNTc0LjUwOEM2ODEuMjc3IDU3NC41MDggNjQzLjI3NyA1ODEuNTA4IDU3My4xNzEgNTY3LjUwOEM0NTEuMjc3IDUzOC41MDggNDQxLjc3NyA1MjEuNTA4IDMyOS42NzEgNTI3LjAwOEMzMjIuNTIgNTI3LjM1OSAzMjcuNjcxIDQ4Ni41MDggMzI5LjY3MSA0NjEuMDA4QzMzMS42NzEgNDM1LjUwOCAzNTQuMTcxIDQwNi41MDggMzc5LjY3MSAzODAuNTA4QzQwNS4xNzEgMzU0LjUwOCA0NTAuMTcxIDMzNS4wMDggNDgwLjY3MSAzMzAuNTA4QzUxMS4xNzEgMzI2LjAwOCA1MjIuMTcxIDI3MS4wMDggNTM3LjY3MSAyNDAuMDA4QzU1My4xNzEgMjA5LjAwOCA2NTMuMzI2IDE1Ny44NTIgNzA3LjE3MSAxODIuNTA4Qzc3My43NzcgMjEzLjAwOCA4MjUuMTcxIDI1MS4wMDggODM5LjY3MSAyNjQuNTA4Qzg1NC4xNzEgMjc4LjAwOCA4NDguMTcxIDI5Mi41MDggODM0LjY3MSAzMzUuMDA4QzgyMy44NzEgMzY5LjAwOCA4MTAuNTA0IDM3OS41MDggODA1LjE3MSAzODAuNTA4Qzc2Mi43NzEgMzkyLjEwOCA3MjkuODM3IDM4NS4zNDEgNzE4LjY3MSAzODAuNTA4WiIgZmlsbD0iI2ViZWFlOSIvPgo8ZWxsaXBzZSBjeD0iNTk2LjQyOCIgY3k9IjE5OS4zNDMiIHJ4PSIzNi41IiByeT0iMzIuNSIgdHJhbnNmb3JtPSJyb3RhdGUoMzMuOTQ3MSA1OTYuNDI4IDE5OS4zNDMpIiBmaWxsPSIjZWJlYWU5Ii8+Cjwvc3ZnPg==" alt="Logo" height="70" >
            neibo
        </a>
        <%--
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
        --%>
        <a href="post">
            <button type="button" class="btn btn-light mr-2 square-button" data-bs-toggle="tooltip" data-bs-placement="right" title="Crear nueva publicación" data-bs-animation="true">
                <i class="fas fa-plus"></i>
            </button>
        </a>
    </div>
</nav>