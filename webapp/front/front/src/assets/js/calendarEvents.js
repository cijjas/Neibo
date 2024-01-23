async function assignEventTimestamps() {
    eventTimestamps = await getEventTimestamps();
    loadCalendar();
}

async function getEventTimestamps() {
    try {
        const response = await fetch("${pageContext.request.contextPath}/endpoint/get-event-timestamps");

        if (response.ok) {
            const timestampString = await response.text();
            const eventTimestamps = timestampString.split(',').map(Number); // Parse values as numbers

            return eventTimestamps;
        } else {
            throw new Error("Failed to fetch event data from the endpoint.");
        }
    } catch (error) {
        console.error(error.message);
        return []; // Return an empty array or handle the error as needed
    }
}


function loadCalendar() {


    const daysTag = document.querySelector(".days");
    const currentDate = document.querySelector(".current-date");
    const prevNextIcon = document.querySelectorAll(".icons span");


    // getting new date, current year, and month
    let date = new Date();
    let currYear = date.getFullYear();
    let currMonth = date.getMonth();

    // storing full name of all months in array
    const months = [
        "<spring:message code='month.january' />",
        "<spring:message code='month.february' />",
        "<spring:message code='month.march' />",
        "<spring:message code='month.april' />",
        "<spring:message code='month.may' />",
        "<spring:message code='month.june' />",
        "<spring:message code='month.july' />",
        "<spring:message code='month.august' />",
        "<spring:message code='month.september' />",
        "<spring:message code='month.october' />",
        "<spring:message code='month.november' />",
        "<spring:message code='month.december' />"
    ];

    const renderCalendar = () => {
        let firstDayOfMonth = new Date(currYear, currMonth, 1).getDay();
        let lastDateOfMonth = new Date(currYear, currMonth + 1, 0).getDate();
        let lastDayOfMonth = new Date(currYear, currMonth, lastDateOfMonth).getDay();
        let lastDateOfLastMonth = new Date(currYear, currMonth, 0).getDate();
        let liTag = "";

        for (let i = firstDayOfMonth; i > 0; i--) {
            liTag += `<li class="inactive">\${lastDateOfLastMonth - i + 1}</li>`;
        }

        for (let i = 1; i <= lastDateOfMonth; i++) {
            let isToday = i === date.getDate() && currMonth === date.getMonth() && currYear === date.getFullYear();
            let dayDate = new Date(currYear, currMonth, i); // Create a Date object for the current day
            let isEventDate = eventTimestamps.some(eventTimestamp => {
                const eventDate = new Date(eventTimestamp);
                return dayDate.getDate() === eventDate.getDate() &&
                    dayDate.getMonth() === eventDate.getMonth() &&
                    dayDate.getFullYear() === eventDate.getFullYear();
            });

            let classNames = isToday ? "today" : "";

            if (isEventDate && !isToday) {
                classNames = "event";
            }

            // Create a link with the timestamp as a query parameter
            liTag += `<li class="\${classNames}" onclick="window.location.href='${pageContext.request.contextPath}/calendar?timestamp=\${dayDate.getTime()}'">\${i}</li>`;

        }


        for (let i = lastDayOfMonth; i < 6; i++) {
            liTag += `<li class="inactive">\${i - lastDayOfMonth + 1}</li>`;
        }

        currentDate.innerText = `\${months[currMonth]}\n\${currYear}`;
        daysTag.innerHTML = liTag;
    };

    renderCalendar();

    prevNextIcon.forEach(icon => {
        icon.addEventListener("click", () => {
            currMonth = icon.id === "prev" ? currMonth - 1 : currMonth + 1;
            if (currMonth < 0 || currMonth > 11) {
                date = new Date(currYear, currMonth, new Date().getDate());
                currYear = date.getFullYear();
                currMonth = date.getMonth();
            } else {
                date = new Date();
            }
            renderCalendar();
        });
    });
}