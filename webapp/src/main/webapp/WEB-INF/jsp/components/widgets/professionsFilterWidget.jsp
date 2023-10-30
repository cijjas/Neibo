<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="grey-static-container ">
    <div class="f-c-c-s g-0">

        <div class="f-r-sb-c w-100 ">
            <h3 class="pt-1 pb-1 m-1"><spring:message code="Filter.by.professions"/></h3>


        </div>

        <div class="m-b-10 w-100">
            <div class="d-flex flex-column justify-content-center align-items-center  w-100">
                <c:set var="val"><spring:message code="EnterAProfession"/></c:set>
                <input id="niakaniaka" type="hidden" value="<c:out value='${val}'/>"/>

                <label for="profession-input2" class="w-100">
                    <input type="text" id="profession-input2">
                </label>
            </div>
        </div>
        <div class="m-b-10 professions-row">
            <c:forEach var="profession" items="${professionList}" varStatus="loop">
                <c:choose>
                    <c:when test="${loop.index < 20}">
                        <a class="profession-option" onclick="addProfessionToApply('${profession}', professionInput2)"><c:out value="${profession}"/></a>
                    </c:when>
                    <c:otherwise>
                        <a class="profession-option" onclick="addProfessionToApply('${profession}',professionInput2)" style="display: none"><c:out value="${profession}"/></a>
                    </c:otherwise>
                </c:choose>

            </c:forEach>
        </div>

        <div hidden="hidden" id="applied-professions" data-professions="${appliedProfessions}"></div>

        <script>
            // Function to initialize applied professions from the data attribute
            function initializeAppliedProfessions() {
                const appliedProfessionsDiv = document.getElementById('applied-professions');
                const professionsString = appliedProfessionsDiv.getAttribute('data-professions');

                if (professionsString) {
                    // Use a regular expression to match professions
                    const professionRegex = /\w+/g; // This regex matches one or more word characters

                    // Extract professions from the string
                    const professionsArray = professionsString.match(professionRegex);

                    if (professionsArray) {
                        // Loop through the professions and add them
                        professionRegex.forEach(function (professionText) {
                            addProfessionToApply(professionText, professionInput2);
                        });
                    }
                }
            }

            // Call the function to initialize applied professions when the page loads
            window.addEventListener('load', initializeAppliedProfessions);
        </script>
        <div class="w-100">
            <div class="f-r-sb-c  g-1">
                <a class="cool-button red font-weight-bold" onclick="clearAllProfessions()">
                    <spring:message code="Clear"/>
                    <i class="fa-solid fa-xmark"></i>
                </a>
                <a class="cool-button cool-small on-bg font-weight-bold " onclick="applyProfessionsAsFilter()">
                    <spring:message code="Apply"/>
                </a>
            </div>

        </div>

    </div>
</div>


<script src="${pageContext.request.contextPath}/resources/js/professionsWidgetHandler.js"></script>

<script>

    function clearAllProfessions() {
        professionInput2.clearAllProfessions();
    }

    function filterProfessions(professions, query) {
        const professionList = document.querySelectorAll('.profession-option');

        professionList.forEach(function (professionOption) {
            const professionText = professionOption.textContent;
            const shouldShow = professionText.toLowerCase().includes(query.toLowerCase());

            if (shouldShow) {
                professionOption.style.display = 'block'; // Show the element

            } else {
                professionOption.style.display = 'none'; // Hide the element
            }
        });
    }

    // Function to clear the filtered professions container
    function clearFilteredProfessions() {
        const filteredProfessionsContainer = document.querySelector('.filtered-professions');
        filteredProfessionsContainer.innerHTML = '';
    }

    const professionInput2 = new ProfessionsInput({
        selector: 'profession-input2',
        wrapperClass: 'professions-input-wrapper',
        duplicate: false,
        max: 5
    });

    // Add a profession from the dropdown
    function addProfessionToApply(professionText, professionInput) {
        professionInput.addProfession(professionText); // Assuming professionInput2 is your ProfessionsInput instance
    }

    function applyProfessionsAsFilter() {
        const professionsArray = professionInput2.arr;
        const professionsString = professionsArray.join(',');

        // Create a form element
        const professionsFilterForm = document.createElement('form');
        professionsFilterForm.setAttribute('method', 'POST');
        professionsFilterForm.setAttribute('action', '${pageContext.request.contextPath}/services/apply-professions-as-filter'); // Set your form action URL

        // Create a hidden input field for the current URL
        const currentUrlInput = document.createElement('input');
        currentUrlInput.setAttribute('type', 'hidden');
        currentUrlInput.setAttribute('name', 'currentUrl');
        currentUrlInput.setAttribute('value', window.location.href); // Get the current URL

        // Create a hidden input field for professions
        const professionsFilterInput = document.createElement('input');
        professionsFilterInput.setAttribute('type', 'hidden');
        professionsFilterInput.setAttribute('name', 'professions');
        professionsFilterInput.setAttribute('value', professionsString);

        // Append the input fields to the form
        professionsFilterForm.appendChild(currentUrlInput);
        professionsFilterForm.appendChild(professionsFilterInput);

        // Append the form to the document body (or any other suitable container)
        document.body.appendChild(professionsFilterForm);

        // Submit the form
        professionsFilterForm.submit();
    }


</script>
