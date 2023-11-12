<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
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
  <title><c:out value="${product.name}"/></title>
</head>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page" />

<body class="${loggedUser.darkMode ? 'dark-mode' : ''}">
<%@ include file="/WEB-INF/jsp/components/displays/navbar.jsp" %>
<div class="container">
  <div class="row init ">
    <div class="column-left " >
      <%@ include file="/WEB-INF/jsp/components/widgets/leftColumn.jsp" %>
    </div>

    <div id="center-grid" class="column-center pl-3 ">
      <%@ include file="/WEB-INF/jsp/marketplace/components/upperMarketplaceButtons.jsp" %>

      <div class="cool-static-container w-100 p-0">
        <div class="container ">
          <div class="row">
            <%--LEFT SIDE--%>
            <div class="col-8 pt-3 pb-3 pr-0"  >
              <div class="row w-100 h-100" >
                <div class="col-3 product-small-img f-c-s-c " >
                  <img
                          id="primary-product-image-${product.productId}"
                          src=""
                          class="placeholder active"
                          alt="product_image_${product.productId}"
                  />
                  <c:if test="${not empty product.secondaryPicture.imageId}">
                    <img
                            id="secondary-product-image-${product.productId}"
                            src=""
                            class="placeholder "
                            alt="product_image_${product.productId}"

                    />
                  </c:if>

                  <c:if test="${not empty product.tertiaryPicture.imageId}">

                    <img
                            id="tertiary-product-image-${product.productId}"
                            src=""
                            class="placeholder"
                            alt="product_image_${product.productId}"

                    />
                  </c:if>

                </div>
                <div class="col-9 h-100 w-100 p-0 ">
                    <img
                            id="imgBox"
                            src=""
                            class="placeholder product-image w-100 h-100"
                            alt="product_image_${product.productId}"
                            style="border-radius: var(--border-light);"
                    />
                </div>

              </div>
            </div>
            <script src="${pageContext.request.contextPath}/resources/js/fetchLibrary.js"></script>
            <script>

              // Hover event listeners
              document.querySelectorAll(".product-small-img img").forEach((img) => {
                img.addEventListener("mouseover", () => {
                  const prevActive = document.querySelector(".product-small-img img.active");
                  if (prevActive) {
                    prevActive.classList.remove("active");
                  }
                  img.classList.add("active");
                  const fullImg = document.getElementById("imgBox");
                  fullImg.src = img.src;
                  fullImg.alt = img.alt;
                });
              });


              var primaryImageId = ${empty product.primaryPicture.imageId ? -2 : product.primaryPicture.imageId};
              var secondaryImageId = ${empty product.secondaryPicture.imageId ? -2 : product.secondaryPicture.imageId};
              var tertiaryImageId = ${empty product.tertiaryPicture.imageId ? -2 : product.tertiaryPicture.imageId};

              document.addEventListener("DOMContentLoaded", function () {
                // Check for the existence of primary image and load if not null

                // Check for the existence of secondary image and load if not null
                if (secondaryImageId !== -2) {
                  getImageInto("secondary-product-image-${product.productId}", secondaryImageId, "${pageContext.request.contextPath}");
                }

                // Check for the existence of tertiary image and load if not null
                if (tertiaryImageId !== -2) {
                  getImageInto("tertiary-product-image-${product.productId}", tertiaryImageId, "${pageContext.request.contextPath}");
                }

                getImageInto("primary-product-image-${product.productId}", primaryImageId, "${pageContext.request.contextPath}");
                getImageInto("imgBox", primaryImageId, "${pageContext.request.contextPath}");
              });


            </script>
            <%--RIGHT SIDE--%>
            <div class="col-4 pt-3 pb-3" >
              <div class="f-c-s-s w-100 g-05 p-3 pt-4" style="
              border: 1px solid var(--lightertext);
              border-radius: var(--border-radius);
              box-shadow: -2px 4px 0 0 var(--lila)">
              <span class="font-weight-normal font-size-16 f-r-sb-c w-100">
                <c:choose>
                  <c:when test="${product.used}">
                      <spring:message code="Used"/>
                  </c:when>
                  <c:otherwise>
                      <spring:message code="New"/>
                  </c:otherwise>
                </c:choose>
                <div class="department-tag" onclick='window.location.href = "${contextPath}/marketplace/products/${product.department.department.departmentUrl}"  '>
                  <spring:message code="${product.department.department}"/>
                </div>
              </span>
              <h1 class="font-size-20 font-weight-bold mt-2" style="line-height: 1.2;">
                <c:out value="${product.name}"/>
              </h1>
              <div class="f-r-c-c g-0">
                <span class="price font-size-30 font-weight-normal">
                    <c:out value="${product.priceIntegerString}"/>
                </span>
                <div class="f-c-s-c pl-1" style="height: 26px">
                <span class="cents c-light-text font-size-20 font-weight-normal">
                    <c:out value="${product.priceDecimalString}"/>
                </span>
                </div>
              </div>
              <button id="request-button" onclick="openRequestDialog()" class="mt-4 w-100 cool-button marketplace-button pure filled-interesting square-radius font-size-14 font-weight-bold">
                <spring:message code="Request"/>
              </button>
              <script>
                function openRequestDialog() {
                  const dialog = document.getElementById('request-dialog');
                  dialog.style.display = 'flex';
                }

                function closeRequestDialog() {
                  const dialog = document.getElementById('request-dialog');
                  dialog.style.display = 'none';
                }
              </script>

            </div>
            </div>
          </div>
          <%--Description--%>
          <div class="row pt-2 pl-2 pr-2" style="background-color: var(--on-onbackground);">
            <div class="col-12 pt-3 pb-3 h-100" >
              <div class="f-c-s-s">
                <span class="c-text font-size-16 g-05"><spring:message code="Description"/></span>
                <p class="c-light-text font-weight-normal" >
                  <c:out value="${product.description}"/>
                </p>
              </div>

              <div class="divider w-100 mt-3 mb-3"></div>

              <div class="f-c-s-s">

                <c:if test="${loggedUser.userId != product.seller.userId}">
                  <span class="c-text font-size-16 g-05"><spring:message code="Ask.the.seller"/></span>
                  <form:form class="f-r-sb-c w-100" id="questionForm" method="post" action="${pageContext.request.contextPath}/marketplace/products/${department}/${product.productId}/ask" modelAttribute="questionForm">
                    <c:set var="askAQuestion">
                      <spring:message code="Ask.a.question"/>
                    </c:set>
                    <form:input path="questionMessage" type="text" class="cool-input marketplace-input background" placeholder="${askAQuestion}"/>
                    <a onclick="submitQuestion()"   id="ask-button" class="cool-button marketplace-button pure square-radius font-size-14 font-weight-bold">
                      <spring:message code="Ask"/>
                    </a>
                    <script>
                      function submitQuestion(){
                        const form = document.forms['questionForm'];
                        form.submit();
                      }
                    </script>
                  </form:form>
                </c:if>



                <c:choose>
                  <c:when test="${not empty questions}">

                    <spring:message code="Recently.asked.questions"/>
                    <c:forEach var="question" items="${questions}">
                      <div class="f-c-s-s w-100 g-05">
                        <div class="f-r-s-s ">
                          <span id="question-span-${question.inquiryId}" class="font-weight-normal c-text font-size-14">
                            <c:out value="${question.message}"/>
                          </span>
                            <%--REQUESTER and not replied--%>
                          <c:if test="${loggedUser.userId == product.seller.userId && empty question.reply}">
                            <a class="btn p-0 " id="reply-button-${question.inquiryId}" onclick="showReplyDialog('${question.message}',  ${question.inquiryId})">
                              <i class="fa-solid fa-reply c-light-text lila-hover"></i>
                            </a>
                          </c:if>
                        </div>
                        <c:if test="${not empty question.reply}">
                          <div class="f-r-s-s w-100 g-05">
                            <i class="fa-solid fa-l c-light-text pl-2"></i>
                            <span class="font-weight-normal c-light-text font-size-12">
                              <c:out value="${question.reply}"/>
                            </span>
                          </div>
                        </c:if>
                      </div>
                    </c:forEach>
                    <div id="reply-dialog" class="dialog reply-dialog" style="display: none">
                      <div class="cool-static-container small-size-container">
                        <div class="f-c-c-c w-100">

                          <span class="c-light-text">
                            <spring:message code="Question"/>
                          </span>

                          <span class="font-size-14 c-lila" id="question-t">
                          </span>
                          <span id="id-t" hidden="hidden">
                          </span>

                          <a class="close-button marketplace" onclick="closeReplyDialog()">
                            <i class="fas fa-close"></i>
                          </a>
                          <script>
                            function showReplyDialog(message, id){
                              const questionT = document.getElementById('question-t');
                              questionT.innerHTML = message;
                              const idT = document.getElementById('id-t');
                              idT.innerHTML = id;
                              const dialog = document.getElementById('reply-dialog');
                              dialog.style.display = 'flex';
                            }
                            function closeReplyDialog(){
                              const dialog = document.getElementById('reply-dialog');
                              dialog.style.display = 'none';
                            }
                            function submitReply() {
                              // You can access the current questionId from the id-t span
                              document.getElementById('replying-id').value = getId();
                              const form = document.forms['replyForm'];
                              form.submit();
                            }
                          </script>

                          <form:form class="f-r-c-c w-100" name="replyForm" id="replyForm" method="post" action="${pageContext.request.contextPath}/marketplace/products/${department}/${product.productId}/reply/${question.inquiryId}" modelAttribute="replyForm">
                            <c:set var="yourReply">
                              <spring:message code='Your.reply'/>
                            </c:set>
                            <form:hidden id="replying-id" path="inquiryId" value=""/>
                            <form:input path="replyMessage" type="text" class="cool-input marketplace-input background" placeholder="${yourReply}"/>
                            <a onclick="submitReply()" id="reply-button" class="cool-button marketplace-button pure square-radius  font-weight-bold f-c-c-c">
                              <spring:message code="Reply"/>
                            </a>
                          </form:form>
                        </div>
                      </div>
                    </div>
                    </c:when>
                    <c:otherwise>
                        <div class="f-c-s-s w-100 ">
                            <div class="f-r-s-c w-100 g-05">
                              <span class="c-text font-size-14 font-weight-bold ">
                                <spring:message code="No.one.asked"/>
                              </span>
                              <span class="c-light-text font-weight-normal font-size-12">
                                <spring:message code="Ask.first"/>
                              </span>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
              </div>
            </div>
          </div>
        </div>

      </div>

    </div>
  </div>
</div>

<script>
  document.addEventListener("DOMContentLoaded", function () {
    const requestError = ${requestError};


    if(requestError){
      document.getElementById('boxy').style.top = '27%';
      openRequestDialog();
    }
  });
</script>


<div id="request-dialog" class="dialog" style="display: none" >
  <div class="dialog-content marketplace" >
    <div class="dialog-header">
      <div class="dialog-svg">
        <svg width="282" height="148" viewBox="0 0 282 148" fill="none" xmlns="http://www.w3.org/2000/svg">
          <g filter="url(#filter0_d_0_1)">
            <path d="M166 81C166 75.4771 170.477 71 176 71H266C271.523 71 276 75.4772 276 81V116.265C276 121.788 271.523 126.265 266 126.265H264.094C261.659 126.265 259.466 127.737 258.543 129.99L255.703 136.919C254.674 139.43 251.497 140.21 249.423 138.461L249.069 138.162L238.238 128.725C236.417 127.139 234.083 126.265 231.669 126.265H176C170.477 126.265 166 121.788 166 116.265V81Z" fill="#7D7AE3"></path>
          </g>
          <g filter="url(#filter1_d_0_1)">
            <path d="M221 14C221 8.47715 216.523 4 211 4H16C10.4772 4 6 8.47715 6 14V97.5243C6 103.047 10.4772 107.524 16 107.524H33.178C35.58 107.524 37.7505 108.957 38.6948 111.165L45.2444 126.483C47.5005 131.759 54.1745 133.418 58.6379 129.811V129.811L82.5442 109.848C84.3425 108.347 86.6109 107.524 88.9537 107.524H211C216.523 107.524 221 103.047 221 97.5243V14Z" fill="#7D7AE3"></path>
          </g>
          <defs>
            <filter id="filter0_d_0_1" x="160" y="67" width="122" height="80.4038" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
              <feFlood flood-opacity="0" result="BackgroundImageFix"></feFlood>
              <feColorMatrix in="SourceAlpha" type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"></feColorMatrix>
              <feOffset dy="2"></feOffset>
              <feGaussianBlur stdDeviation="3"></feGaussianBlur>
              <feComposite in2="hardAlpha" operator="out"></feComposite>
              <feColorMatrix type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.25 0"></feColorMatrix>
              <feBlend mode="normal" in2="BackgroundImageFix" result="effect1_dropShadow_0_1"></feBlend>
              <feBlend mode="normal" in="SourceGraphic" in2="effect1_dropShadow_0_1" result="shape"></feBlend>
            </filter>
            <filter id="filter1_d_0_1" x="0" y="0" width="227" height="139.736" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
              <feFlood flood-opacity="0" result="BackgroundImageFix"></feFlood>
              <feColorMatrix in="SourceAlpha" type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"></feColorMatrix>
              <feOffset dy="2"></feOffset>
              <feGaussianBlur stdDeviation="3"></feGaussianBlur>
              <feComposite in2="hardAlpha" operator="out"></feComposite>
              <feColorMatrix type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.25 0"></feColorMatrix>
              <feBlend mode="normal" in2="BackgroundImageFix" result="effect1_dropShadow_0_1"></feBlend>
              <feBlend mode="normal" in="SourceGraphic" in2="effect1_dropShadow_0_1" result="shape"></feBlend>
            </filter>
          </defs>
        </svg>

      </div>

      <div class="boxy p-2" id="boxy">
        <div style="position: relative; color:var(--always-background)" class="f-c-s-s g-05">
          <h3 class="font-size-20 font-weight-bold" ><spring:message code="Request"/></h3>
          <p style="text-align: start" class="font-weight-normal">
            <spring:message code="Request.buy.message"/>
          </p>
        </div>

      </div>
      <a class="close-button marketplace" onclick="closeRequestDialog()">
        <i class="fas fa-close"></i>
      </a>
    </div>
    <form:form class="f-c-c-c w-100" id="requestForm" name="requestForm" method="post" action="${contextPath}/marketplace/products/${product.department.department.departmentUrl}/${product.productId}/request" modelAttribute="requestForm" enctype="multipart/form-data">
        <div class="form-group w-75" >
          <c:set var="messagePlaceholder">
            <spring:message code="Message"/>
          </c:set>
          <form:textarea  path="requestMessage" class="cool-input marketplace-input" id="request-message" name="message" rows="5" placeholder="${messagePlaceholder}"/>
          <form:errors path="requestMessage" cssClass="error" element="p" cssStyle="padding-left: 5px"/>
        </div>
        <button type="submit"  onclick="document.getElementById('loader-container').style.display = 'flex';" class=" w-75 cool-button marketplace-button pure filled-interesting square-radius font-size-14 font-weight-bold">
          <spring:message code="Send"/>
        </button>

    </form:form>

  </div>
</div>

<div id="loader-container" class="loader-container ">
  <div class="cool-static-container small-size-container">

    <div style="font-weight: bold; font-size: 16px"><spring:message code="Sending.your.message"/>...</div>
    <div class="loader marketplace" style="margin-top: 20px"></div>
  </div>
</div>



<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm"
        crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>
<script>
  const wave3= document.getElementById('wave3');
  const wave2= document.getElementById('wave2');
  wave2.classList.add('lila');
  wave3.classList.add('lila');
</script>
</body>
</html>
