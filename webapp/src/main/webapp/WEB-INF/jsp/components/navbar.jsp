<%--
  Created by IntelliJ IDEA.
  User: chrij
  Date: 9/9/2023
  Time: 3:51 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<nav class="navbar navbar-expand-lg custom-navbar">
    <div class="container-xl">
        <c:url var="logoImage" value="/resources/images/logo_neibo.svg"/>

        <%@include file="tooltips.jsp"%>

        <div class="d-flex justify-content-between align-items-center w-100">
            <a class="navbar-brand" href="/">
                <img src="data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDU1IiBoZWlnaHQ9IjEwNTUiIHZpZXdCb3g9IjAgMCAxMDU1IDEwNTUiIGZpbGw9Im5vbmUiPgo8cGF0aCBkPSJNNDM5IDcxMy41QzMzNS40NzEgNzA3Ljc0OCAyNTAuMzczIDc5OC4xNTQgMjE0LjczMSA4NTAuMDU0QzIxMi43NzkgODUyLjg5NiAyMDguMjY2IDg1MC44ODcgMjA5LjMyMSA4NDcuNjA1QzI0MS4zNDQgNzQ4LjAyNCAzMzkuOTkxIDY2NS4xNDcgMzk5LjUgNjYwQzQ5MiA2NTIgNTExLjc4NiA2ODQuNjk4IDY3MCA2OTJDNzcyLjg5IDY5Ni43NDkgODI5LjU1IDY2NS42NTUgODY0LjgyNCA2MzIuNzk2Qzg2Ny41NDIgNjMwLjI2NSA4NzQuMDAzIDYzNC45MzMgODcyLjAyOCA2MzguMDc5Qzg0Ni4zMDUgNjc5LjA2MyA3OTkuMDI4IDczMiA2OTkgNzMyQzU0NC41NzEgNzMyIDU5MiA3MjIgNDM5IDcxMy41WiIgZmlsbD0iI2ZmZmVmOCIvPgo8cGF0aCBkPSJNNDgxIDgwNC42NjNDNDE2LjQ3MyA3OTguOTQxIDM1Mi42MzcgODM3LjUxNyAzMTguMTg4IDg3My4wM0MzMTUuNjE5IDg3NS42NzkgMzA4LjE1MSA4NzEuNDczIDMwOS44MjIgODY4LjE4MkMzNDEuMDM5IDgwNi42ODYgMzk2Ljc1OCA3NzIuNjQ1IDQzNS41IDc2OC4xNjNDNDk2IDc2MS4xNjMgNDk5LjUzIDc3MS41NDcgNjEyLjUgNzkzLjY2M0M2OTQuMDY3IDgwOS42MzEgNzIwLjUwMyA4MDQuNzM4IDc0NS41MDIgNzg1LjczOEM3NDguMTM1IDc4My43MzcgNzUxLjkyNCA3ODYuNjkxIDc1MC4yNzcgNzg5LjU1OEM3MzYuMTQyIDgxNC4xNjkgNzExLjc0OCA4MzcuMjU2IDYzMSA4MjcuMTYzQzUyMC43MjIgODEzLjM3OCA1ODIuNSA4MTMuNjYzIDQ4MSA4MDQuNjYzWiIgZmlsbD0iI2ZmZmVmOCIvPgo8cGF0aCBkPSJNNDE4LjUgNjE2LjVDMzExLjQ4OCA1OTkuMzQ4IDIwNi4wMTEgNzEwLjkxMyAxNzAuNTkzIDc1Ni42MzhDMTY4LjcxOCA3NTkuMDU5IDE2NC43NzMgNzU3LjQ5MyAxNjUuMTI4IDc1NC40NTJDMTc5LjQ5IDYzMS40MDUgMjQ3LjQ2IDU3Ni4wMjggMzMxIDU2MEM0MTcgNTQzLjUgNDY1LjEyNCA1ODUuMzQ2IDYyMyA1OThDNzIzLjQ0NSA2MDYuMDUxIDgwNy43MjIgNTk0LjUwNSA4NTUuOTk3IDU3MC41NzRDODU5LjMzNSA1NjguOTE5IDg2Ni43NjQgNTc2LjQ0MSA4NjQuMTgzIDU3OS4xMjdDODMxLjA0MiA2MTMuNjIyIDc3NC41NzMgNjQzLjUgNjczIDY0My41QzUxOC41NzEgNjQzLjUgNTYyIDYzOS41IDQxOC41IDYxNi41WiIgZmlsbD0iI2ZmZmVmOCIvPgo8cGF0aCBkPSJNNzE4LjY3MSAzODAuNTA4QzY4Ni4yNzEgNDU0LjEwOCA2OTIuOTQ0IDU0MC41MDggNjk3LjI3NyA1NzQuNTA4QzY4MS4yNzcgNTc0LjUwOCA2NDMuMjc3IDU4MS41MDggNTczLjE3MSA1NjcuNTA4QzQ1MS4yNzcgNTM4LjUwOCA0NDEuNzc3IDUyMS41MDggMzI5LjY3MSA1MjcuMDA4QzMyMi41MiA1MjcuMzU5IDMyNy42NzEgNDg2LjUwOCAzMjkuNjcxIDQ2MS4wMDhDMzMxLjY3MSA0MzUuNTA4IDM1NC4xNzEgNDA2LjUwOCAzNzkuNjcxIDM4MC41MDhDNDA1LjE3MSAzNTQuNTA4IDQ1MC4xNzEgMzM1LjAwOCA0ODAuNjcxIDMzMC41MDhDNTExLjE3MSAzMjYuMDA4IDUyMi4xNzEgMjcxLjAwOCA1MzcuNjcxIDI0MC4wMDhDNTUzLjE3MSAyMDkuMDA4IDY1My4zMjYgMTU3Ljg1MiA3MDcuMTcxIDE4Mi41MDhDNzczLjc3NyAyMTMuMDA4IDgyNS4xNzEgMjUxLjAwOCA4MzkuNjcxIDI2NC41MDhDODU0LjE3MSAyNzguMDA4IDg0OC4xNzEgMjkyLjUwOCA4MzQuNjcxIDMzNS4wMDhDODIzLjg3MSAzNjkuMDA4IDgxMC41MDQgMzc5LjUwOCA4MDUuMTcxIDM4MC41MDhDNzYyLjc3MSAzOTIuMTA4IDcyOS44MzcgMzg1LjM0MSA3MTguNjcxIDM4MC41MDhaIiBmaWxsPSIjZmZmZWY4Ii8+CjxlbGxpcHNlIGN4PSI1OTYuNDI4IiBjeT0iMTk5LjM0MyIgcng9IjM2LjUiIHJ5PSIzMi41IiB0cmFuc2Zvcm09InJvdGF0ZSgzMy45NDcxIDU5Ni40MjggMTk5LjM0MykiIGZpbGw9IiNmZmZlZjgiLz4KPC9zdmc+" alt="Logo" height="60" >

                neibo
            </a>
            <a href="publish">
                <button type="button" class="btn btn-light mr-2 square-button" data-bs-toggle="tooltip" data-bs-placement="right" title="Crear nueva publicación" data-bs-animation="true">
                    <i class="fas fa-plus"></i>
                </button>
            </a>
        </div>
    </div>
</nav>

<%--
<nav class="navbar navbar-expand-lg navbar-dark bg-dark" aria-label="Ninth navbar example">
    <div class="container-xl">
        <a class="navbar-brand" href="#">Container XL</a>

        <button class="navbar-toggler collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#navbarsExample07XL" aria-controls="navbarsExample07XL" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="navbar-collapse collapse" id="navbarsExample07XL" style="">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="#">Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#">Link</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link disabled" href="#" tabindex="-1" aria-disabled="true">Disabled</a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="dropdown07XL" data-bs-toggle="dropdown" aria-expanded="false">Dropdown</a>
                    <ul class="dropdown-menu" aria-labelledby="dropdown07XL">
                        <li><a class="dropdown-item" href="#">Action</a></li>
                        <li><a class="dropdown-item" href="#">Another action</a></li>
                        <li><a class="dropdown-item" href="#">Something else here</a></li>
                    </ul>
                </li>
            </ul>
            <form>
                <input class="form-control" type="text" placeholder="Search" aria-label="Search">
            </form>
        </div>
    </div>
</nav>
--%>
