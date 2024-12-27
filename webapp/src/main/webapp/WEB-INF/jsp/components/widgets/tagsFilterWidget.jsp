<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="grey-static-container ">
    <div class="f-c-c-s g-0">

        <div class="f-r-sb-c w-100 ">
            <h3 class="pt-1 pb-1 m-1"><spring:message code="Filter.by.tags"/></h3>
        </div>

        <div class="m-b-10 w-100">
            <div class="d-flex flex-column justify-content-center align-items-center  w-100">
                <c:set var="val"><spring:message code="EnterATag"/></c:set>
                <input id="niakaniaka" type="hidden" value="<c:out value='${val}'/>"/>

                <label for="tag-input2" class="w-100">
                    <input type="text" id="tag-input2">
                </label>
            </div>
        </div>
        <div class="m-b-10 tags-row tags">
            <c:forEach var="tag" items="${tagList}" varStatus="loop">
                <c:choose>
                    <c:when test="${loop.index < 20}">
                        <a class="tag-option" onclick="addTagToApply('${tag.tag}', tagInput2)"><c:out value="${tag.tag}"/></a>
                    </c:when>
                    <c:otherwise>
                        <a class="tag-option" onclick="addTagToApply('${tag.tag}',tagInput2)" style="display: none"><c:out value="${tag.tag}"/></a>
                    </c:otherwise>
                </c:choose>

            </c:forEach>
        </div>

        <div hidden="hidden" id="applied-tags" data-tags="${appliedTags}"></div>

        <script>
            // Function to initialize applied tags from the data attribute
            function initializeAppliedTags() {
                const appliedTagsDiv = document.getElementById('applied-tags');
                const tagsString = appliedTagsDiv.getAttribute('data-tags');

                if (tagsString) {
                    // Use a regular expression to match alphanumeric tags
                    const tagRegex = /\w+/g; // This regex matches one or more word characters

                    // Extract alphanumeric tags from the string
                    const tagsArray = tagsString.match(tagRegex);

                    if (tagsArray) {
                        // Loop through the tags and add them
                        tagsArray.forEach(function (tagText) {
                            addTagToApply(tagText, tagInput2);
                        });
                    }
                }
            }

            // Call the function to initialize applied tags when the page loads
            window.addEventListener('load', initializeAppliedTags);
        </script>
        <div class="w-100">
            <div class="f-r-sb-c  g-1">
                <a class="cool-button red font-weight-bold" onclick="clearAllTags()">
                    <spring:message code="Clear"/>
                    <i class="fa-solid fa-xmark"></i>
                </a>
                <a class="cool-button cool-small on-bg font-weight-bold " onclick="applyTagsAsFilter()">
                    <spring:message code="Apply"/>
                </a>
            </div>

        </div>

    </div>
</div>


<script src="${pageContext.request.contextPath}/resources/js/tagsWidgetHandler.js"></script>

<script>

    function clearAllTags() {
        tagInput2.clearAllTags();
    }

    function filterTags(tags, query) {
        const tagList = document.querySelectorAll('.tag-option');

        tagList.forEach(function (tagOption) {
            const tagText = tagOption.textContent;
            const shouldShow = tagText.toLowerCase().includes(query.toLowerCase());

            if (shouldShow) {
                tagOption.style.display = 'block'; // Show the element

            } else {
                tagOption.style.display = 'none'; // Hide the element
            }
        });
    }

    // Function to clear the filtered tags container
    function clearFilteredTags() {
        const filteredTagsContainer = document.querySelector('.filtered-tags');
        filteredTagsContainer.innerHTML = '';
    }

    const tagInput2 = new TagsInput({
        selector: 'tag-input2',
        wrapperClass: 'tags-input-wrapper',
        duplicate: false,
        max: 5
    });

    // Add a tag from the dropdown
    function addTagToApply(tagText, tagInput) {
        tagInput.addTag(tagText); // Assuming tagInput2 is your TagsInput instance
    }

    function applyTagsAsFilter() {
        const tagsArray = tagInput2.arr;
        const tagsString = tagsArray.join(',');

        // Create a form element
        const tagsFilterForm = document.createElement('form');
        tagsFilterForm.setAttribute('method', 'POST');
        tagsFilterForm.setAttribute('action', '${pageContext.request.contextPath}/apply-tags-as-filter'); // Set your form action URL

        // Create a hidden input field for the current URL
        const currentUrlInput = document.createElement('input');
        currentUrlInput.setAttribute('type', 'hidden');
        currentUrlInput.setAttribute('name', 'currentUrl');
        currentUrlInput.setAttribute('value', window.location.href); // Get the current URL

        // Create a hidden input field for tags
        const tagsFilterInput = document.createElement('input');
        tagsFilterInput.setAttribute('type', 'hidden');
        tagsFilterInput.setAttribute('name', 'tags');
        tagsFilterInput.setAttribute('value', tagsString);

        // Append the input fields to the form
        tagsFilterForm.appendChild(currentUrlInput);
        tagsFilterForm.appendChild(tagsFilterInput);

        // Append the form to the document body (or any other suitable container)
        document.body.appendChild(tagsFilterForm);

        // Submit the form
        tagsFilterForm.submit();
    }


</script>
