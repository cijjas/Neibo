<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
    <title><spring:message code="Edit.listing"/></title>
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



            <div class="cool-static-container w-100">
                <div class="f-r-s-c w-100">
                    <a href="${contextPath}/marketplace/products/appliances/${product.productId}" class="marketplace-back">
                        <i class="fa-solid fa-arrow-left"></i>
                    </a>
                </div>
                <div class="f-c-c-c">
                    <div class="f-r-c-c">
                    <span class="font-size-24">
                    <spring:message code="Edit.listing"/>
                    </span>
                    </div>
                    <div class="divider w-75 mb-3"></div>
                </div>



                <form:form modelAttribute="listingForm" name="listingForm" id="listingForm" method="post" action="${contextPath}/marketplace/products/${product.department.department.departmentUrl}/${product.productId}/edit" enctype="multipart/form-data">
                    <form:errors cssClass="error" element="p"/>
                    <div class="f-r-c-c w-100">
                        <div class="f-c-c-c w-75">
                            <div class="w-100 f-r-c-c">
                                    <%--TITLE--%>
                                <div class="w-75">
                                    <spring:message code="Title" var="titlePlaceholder"/>
                                    <form:input
                                            path="title"
                                            type="text"
                                            class="cool-input marketplace-input font-weight-bold"
                                            name="title-field"
                                            id="title-field"
                                            value="${product.name}"
                                            placeholder="${titlePlaceholder}"/>
                                    <form:errors path="title" cssClass="error" element="p"/>
                                </div>

                                    <%--PRICE--%>
                                <div class="w-25">
                                    <form:input
                                            path="price"
                                            type="text"
                                            class="cool-input marketplace-input font-weight-bold font-size-14"
                                            name="currency-field"
                                            id="currency-field"
                                            pattern=""
                                            value="${product.price}"
                                            data-type="currency"
                                            placeholder="$1,000.00"/>
                                    <form:errors path="price" cssClass="error" element="p"/>
                                </div>




                            </div>


                                        <form:input path="imageFiles" id="image-input-id" type="file" multiple="multiple" accept="image/*" data-max_length="3" class="upload__inputfile" hidden="hidden"/>
                                        <form:errors path="imageFiles" cssClass="error" element="p"/>



                                <%--Description--%>
                            <spring:message code='Description' var="descriptionPlaceholder"/>
                            <form:textarea
                                    id="description-textarea"
                                    path="description"
                                    class="cool-input marketplace-input textarea-min-max"
                                    rows="5"
                                    placeholder="${descriptionPlaceholder}"/>
                            <form:errors path="description" cssClass="error" element="p"/>
                            <script>
                                document.getElementById('description-textarea').value = "${product.description}";
                            </script>
                                <%--Department--%>
                            <label for="department"></label>
                            <form:select path="departmentId" class="cool-input marketplace-input font-weight-bold font-size-12" name="department" id="department">
                                <c:forEach items="${departmentList}" var="department">
                                    <c:if test="${department.key eq product.department.departmentId}">
                                        <option value="${department.key}" selected="selected"><spring:message code="${department.value}"/></option>
                                    </c:if>
                                    <c:if test="${department.key ne product.department.departmentId}">
                                        <option value="${department.key}"><spring:message code="${department.value}"/></option>
                                    </c:if>
                                </c:forEach>
                            </form:select>

                            <form:errors path="departmentId" cssClass="error" element="p"/>

                            <%--Condition--%>
                            <div class="f-r-c-c w-100 font-size-16 font-weight-normal g-05">
                                <spring:message code="This.item.is"/>
                                <div class="w-25">
                                    <label for="condition"></label>
                                    <form:select path="used" class="cool-input marketplace-input font-weight-bold font-size-14" name="condition" id="condition">
                                        <c:if test="${product.used eq true}">
                                            <option value="${false}"><spring:message code="New"/></option>
                                            <option value="${true}" selected="selected"><spring:message code="Used"/></option>
                                        </c:if>
                                        <c:if test="${product.used ne true}">
                                            <option value="${false}" selected="selected"><spring:message code="New"/></option>
                                            <option value="${true}" ><spring:message code="Used"/></option>
                                        </c:if>
                                    </form:select>

                                    <form:errors path="used" cssClass="error" element="p"/>
                                </div>
                            </div>

                                <%-- Quantity --%>
                            <div class="f-c-c-c w-100 g-0">
                                <div class="f-r-c-c w-100 font-size-16 font-weight-normal g-05">
                                    <spring:message code="Quantity"/>
                                    <div class="">
                                        <label for="condition"></label>
                                        <form:select path="quantity" class="cool-input marketplace-input quantity-input font-weight-bold font-size-14" name="condition" id="condition">
                                            <c:forEach begin="1" end="100" varStatus="loop">
                                                <c:choose>
                                                    <c:when test="${loop.index == product.remainingUnits}">
                                                        <form:option value="${loop.index}" selected="selected">${loop.index}</form:option>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <form:option value="${loop.index}">${loop.index}</form:option>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </form:select>
                                    </div>
                                </div>
                                <form:errors path="quantity" cssClass="error pt-1" element="p"/>
                            </div>


                        </div>
                    </div>


                    <div class="f-r-c-c mt-4 ">
                        <button  class="cool-button marketplace-button cool-button p-3  font-size-14 font-weight-bolder" style="width: 200px" >
                            <spring:message code="SaveChanges"/>
                        </button>
                    </div>
                </form:form>


            </div>
            <script>
                window.onload = function () {
                    const currencyInput = document.getElementById('currency-field');
                    formatCurrency(currencyInput, "blur");
                }
                const currencyInput = document.getElementById('currency-field');
                currencyInput.addEventListener('keyup', (e) => {
                    formatCurrency(currencyInput, "focus");
                });
                currencyInput.addEventListener('blur', (e) => {
                    formatCurrency(currencyInput, "blur");
                });

                function formatNumber(n) {
                    return n.replace(/\D/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ",");
                }

                function formatCurrency(input, blur) {
                    let input_val = input.value;

                    if (input_val === "") {
                        return;
                    }

                    const original_len = input_val.length;

                    let caret_pos = input.selectionStart;

                    if (input_val.indexOf(".") >= 0) {
                        const decimal_pos = input_val.indexOf(".");

                        let left_side = input_val.substring(0, decimal_pos);
                        let right_side = input_val.substring(decimal_pos);

                        left_side = formatNumber(left_side);

                        right_side = formatNumber(right_side);

                        if (blur === "blur") {
                            right_side += "00";

                        }

                        right_side = right_side.substring(0, 2);

                        input_val = "$" + left_side + "." + right_side;
                    } else {
                        input_val = formatNumber(input_val);
                        input_val = "$" + input_val;

                        // Final formatting
                        if (blur === "blur") {
                            input_val += ".00";
                        }
                    }

                    input.value = input_val;

                    // Put the caret back in the right position
                    const updated_len = input_val.length;
                    caret_pos = updated_len - original_len + caret_pos;
                    input.setSelectionRange(caret_pos, caret_pos);
                }
            </script>


            <script>
                let imgArray = [];

                document.addEventListener('DOMContentLoaded', async function () {

                   /* await preloadDefaultImages();
                    ImgUpload();*/
                /*    triggerFileInputChange();*/

                });
                /*function triggerFileInputChange() {
                    const inputElement = document.getElementById('image-input-id');
                    console.log(imgArray);
                    // Clear existing files
                    inputElement.value = null;

                    // Add preloaded files from imgArray
                    for (let i = 0; i < imgArray.length; i++) {
                        const file = imgArray[i];
                        console.log(new File([file], file.name, {type: file.type}));
                    }
                    console.log(inputElement.files);

                    // Trigger the 'change' event
                    inputElement.dispatchEvent(new EventForm('change'));
                }*/
                /*function ImgUpload() {
                    let imgWrap = '';

                    const uploadInputFiles = document.querySelectorAll('.upload__inputfile');
                    uploadInputFiles.forEach(function (inputFile) {
                        inputFile.addEventListener('load', function (e) {

                        });

                        inputFile.addEventListener('change', function (e) {
                            imgWrap = this.closest('.upload__box').querySelector('.upload__img-wrap');
                            const maxLength = this.getAttribute('data-max_length');
                            const files = e.target.files;
                            const filesArr = Array.from(files);
                            let iterator = 0;
                            filesArr.forEach(function (f) {
                                if (!f.type.match('image.*')) {
                                    return;
                                }
                                if (imgArray.length + 1 > maxLength) {
                                    return false;
                                } else {
                                    const length = imgArray.length + 1;
                                    if (length >= 3) {
                                        const dummyUpload = document.getElementById('dummy-upload');
                                        dummyUpload.style.display = 'none';
                                    }

                                    imgArray.push(f);
                                    const addPhotoText = document.getElementById('add-photo-text');
                                    addPhotoText.innerHTML = 'Add photo (' + length + '/3)';

                                    const reader = new FileReader();
                                    reader.onload = function (e) {
                                        const html = "<div class='upload__img-box'><div style='background-image: url(" + e.target.result + ")' data-number='" + document.querySelectorAll('.upload__img-close').length + "' data-file='" + f.name + "' class='img-bg'><div class='upload__img-close'></div></div></div>";
                                        imgWrap.innerHTML += html;
                                        iterator++;
                                    }
                                    reader.readAsDataURL(f);
                                }
                            });
                        });
                    });

                    document.body.addEventListener('click', function (e) {
                        if (e.target.classList.contains('upload__img-close')) {
                            const file = e.target.parentElement.getAttribute('data-file');
                            imgArray = imgArray.filter(function (item) {
                                return item.name !== file;
                            });
                            const length = imgArray.length + 1;

                            const addPhotoText = document.getElementById('add-photo-text');
                            addPhotoText.innerHTML = 'Add photo (' + imgArray.length + '/3)';

                            if (length <= 3) {
                                const dummyUpload = document.getElementById('dummy-upload');
                                dummyUpload.style.display = 'block';
                            }
                            e.target.parentElement.parentElement.remove();
                        }
                    });
                }


                async function preloadDefaultImages() {
                    const defaultImages = [
                        "${product.primaryPicture.imageId}",
                        "${product.secondaryPicture.imageId}",
                        "${product.tertiaryPicture.imageId}"
                    ];

                    const imgWrap = document.querySelector('.upload__img-wrap');
                    let iterator = 0;

                    for (let i = 0; i < defaultImages.length; i++) {
                        const imageUrl = defaultImages[i].trim();
                        if (imageUrl !== "") {
                            const response = await fetch("${contextPath}/images/" + imageUrl);
                            const blob = await response.blob();

                            const dummyFile = new File([blob], "default_image_" + i + ".png", { type: "image/png" });
                            const length = imgArray.length + 1;

                            if (length >= 3) {
                                const dummyUpload = document.getElementById('dummy-upload');
                                dummyUpload.style.display = 'none';
                            }

                            imgArray.push(dummyFile);
                            const addPhotoText = document.getElementById('add-photo-text');
                            addPhotoText.innerHTML = 'Add photo (' + length + '/3)';

                            const reader = new FileReader();
                            reader.onload = function (e) {
                                const html = "<div class='upload__img-box'><div style='background-image: url(" + URL.createObjectURL(blob) + ")' data-number='" + iterator + "' data-file='" + dummyFile.name + "' class='img-bg'><div class='upload__img-close'></div></div></div>";
                                imgWrap.innerHTML += html;
                                iterator++;
                            };
                            reader.readAsDataURL(dummyFile);

                        }
                    }
                }*/
            </script>

        </div>
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
