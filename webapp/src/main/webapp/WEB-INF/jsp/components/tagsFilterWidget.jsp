<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="grey-static-container ">
    <div class="column d-flex justify-content-center align-items-start">
        <h3 class="m-b-10"><spring:message code="Filter.by.tags"/></h3>

        <div class="m-b-10" >
            <c:set var="val"><spring:message code="EnterATag"/></c:set>
            <input id="niakaniaka" type="hidden" value="${val}"/>

            <label for="tag-input2">
                <input type="text" id="tag-input2" >
            </label>
        </div>
        <div class="m-b-10 tags-row tags" >
            <c:forEach var="tag" items="${tagList}" >
                <a class="tag-option"  onclick="addTagFromDropdown('${tag.tag}')" >${tag.tag}</a>
            </c:forEach>
        </div>
        <div class=" tags-row submit-tags ">
            <a class="w-100 cool-button cool-small on-bg grey" onclick="applyTagsAsFilter()">
                <spring:message code="Apply"/>
            </a>
        </div>

    </div>
</div>


<script src="${pageContext.request.contextPath}/resources/js/tagsWidgetHandler.js"></script>

<script>
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
    function addTagFromDropdown(tagText) {
        console.log(tagText);
        tagInput2.addTag(tagText); // Assuming tagInput2 is your TagsInput instance
    }

    function applyTagsAsFilter() {
        const tagsArray = tagInput2.arr;
        const tagsString = tagsArray.join(',');

        // Create a form element
        const tagsFilterForm = document.createElement('form');
        tagsFilterForm.setAttribute('method', 'POST');
        tagsFilterForm.setAttribute('action', '/applyTagsFilter'); // Set your form action URL

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
