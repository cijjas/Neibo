<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8"%>


<nav class="navbar navbar-expand-lg custom-navbar">
    <div class="container-xl">

        <%@include file="tooltips.jsp"%>

        <div class="d-flex justify-content-between align-items-center w-100">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                <svg class="svg-logo" width="55" height="54" viewBox="0 0 711 676" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M277 516.5C173.471 510.748 88.3725 601.154 52.731 653.054C50.7792 655.896 46.266 653.887 47.3215 650.605C79.3442 551.024 177.991 468.147 237.5 463C330 455 349.786 487.698 508 495C610.89 499.749 667.55 468.655 702.824 435.796C705.542 433.265 712.003 437.933 710.028 441.079C684.305 482.063 637.028 535 537 535C382.571 535 430 525 277 516.5Z" fill="var(--background)"/>
                    <path d="M328 606.663C263.473 600.941 199.637 639.517 165.188 675.03C162.619 677.679 155.151 673.473 156.822 670.182C188.039 608.686 243.758 574.645 282.5 570.163C343 563.163 346.53 573.547 459.5 595.663C541.067 611.631 567.503 606.738 592.502 587.738C595.135 585.737 598.924 588.691 597.277 591.558C583.142 616.169 558.748 639.256 478 629.163C367.722 615.378 429.5 615.663 328 606.663Z" fill="var(--background)"/>
                    <path d="M253.5 431.5C146.488 414.348 41.0106 525.913 5.59293 571.638C3.71802 574.059 -0.226579 572.493 0.128361 569.452C14.4897 446.405 82.4602 391.028 166 375C252 358.5 300.124 400.346 458 413C558.445 421.051 642.722 409.505 690.997 385.574C694.335 383.919 701.764 391.441 699.183 394.127C666.042 428.622 609.573 458.5 508 458.5C353.571 458.5 397 454.5 253.5 431.5Z" fill="var(--background)"/>
                    <path d="M546.671 228.508C514.271 302.108 520.944 388.508 525.277 422.508C509.277 422.508 471.277 429.508 401.171 415.508C279.277 386.508 269.777 369.508 157.671 375.008C150.52 375.359 155.671 334.508 157.671 309.008C159.671 283.508 182.171 254.508 207.671 228.508C233.171 202.508 278.171 183.008 308.671 178.508C339.171 174.008 350.171 119.008 365.671 88.008C381.171 57.008 481.326 5.85161 535.171 30.508C601.777 61.008 653.171 99.008 667.671 112.508C682.171 126.008 676.171 140.508 662.671 183.008C651.871 217.008 638.504 227.508 633.171 228.508C590.771 240.108 557.837 233.341 546.671 228.508Z" fill="var(--background)"/>
                    <ellipse cx="424.428" cy="47.3431" rx="36.5" ry="32.5" transform="rotate(33.9471 424.428 47.3431)" fill="var(--background)"/>
                </svg>
                neibo
            </a>
            <a href="${pageContext.request.contextPath}/publish">
                <button type="button" class="btn btn-light mr-2 square-button" data-bs-toggle="tooltip" data-bs-placement="right" title="<spring:message code="CreateNewPost.tooltip"/>" data-bs-animation="true">
                    <i class="fas fa-plus"></i>
                </button>
            </a>
        </div>
    </div>
</nav>

<div class="wave">
    <svg data-name="Layer 1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 120" preserveAspectRatio="none">
        <path
                d="M321.39,56.44c58-10.79,114.16-30.13,172-41.86,82.39-16.72,168.19-17.73,250.45-.39C823.78,31,906.67,72,985.66,92.83c70.05,18.48,146.53,26.09,214.34,3V0H0V27.35A600.21,600.21,0,0,0,321.39,56.44Z"
                fill="var(--primary)"
        ></path>
    </svg>

</div>

<script>
    // Get the current URL
    const currentURL = window.location.href;

    // Use the URL API to parse the URL
    const url = new URL(currentURL);

    // Extract the domain (first segment)
    const domain = url.hostname;

    // Output the domain
    console.log(domain);

</script>
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
