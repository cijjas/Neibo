<html lang="en" dir="ltr">
<head>
  <meta charset="utf-8">
  <title>Dynamic Calendar JavaScript</title>
  <link href="${pageContext.request.contextPath}/resources/css/calendarBox.css" rel="stylesheet"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <!-- Google Font Link for Icons -->
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Rounded:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200">
</head>

<div class="f-c-c-c">
  <div class="wrapper">

    <header class="f-r-sb-c w-50 m-t-40">
      <div class="icons">
        <span id="prev" ><i class="fa-solid fa-angle-left"></i></span>
      </div>
      <p class="current-date" style="font-weight: bold"></p>
      <div class="icons">
        <span id="next"  ><i  class="fa-solid fa-angle-right"></i></span>
      </div>
    </header>
    <div class="divider"></div>

    <div class="f-c-c-c h-100">
      <div class="calendar">
        <ul class="weeks">
          <li><spring:message code="days.sunday"/></li>
          <li><spring:message code="days.monday"/></li>
          <li><spring:message code="days.tuesday"/></li>
          <li><spring:message code="days.wednesday"/></li>
          <li><spring:message code="days.thursday"/></li>
          <li><spring:message code="days.friday"/></li>
          <li><spring:message code="days.saturday"/></li>
        </ul>
        <ul class="days"></ul>
      </div>
    </div>



  </div>
</div>


</html>

<script>
  const daysTag = document.querySelector(".days");
  const currentDate = document.querySelector(".current-date");
  const prevNextIcon = document.querySelectorAll(".icons span");

  // Access the event timestamps array passed from the Java controller
  let eventTimestamps = ${eventDates}; // Assuming eventDates is an array of timestamps

  // Access the selected timestamp passed from the Java controller
  const selectedTimestamp = ${selectedTimestamp}; // Assuming selectedTimestamp is a valid timestamp in milliseconds

  // Create a Date object from the selected timestamp
  let selectedDate = new Date(selectedTimestamp);

  // Get the year and month from the selected date
  let selectedYear = selectedDate.getFullYear();
  let selectedMonth = selectedDate.getMonth();

  // Create a Date object for the current date
  let date = new Date(); // This gets the current date

  // Storing the full name of all months in an array
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
    let firstDayOfMonth = new Date(selectedYear, selectedMonth, 1).getDay();
    let lastDateOfMonth = new Date(selectedYear, selectedMonth + 1, 0).getDate();
    let lastDayOfMonth = new Date(selectedYear, selectedMonth, lastDateOfMonth).getDay();
    let lastDateOfLastMonth = new Date(selectedYear, selectedMonth, 0).getDate();
    let liTag = "";

    for (let i = firstDayOfMonth; i > 0; i--) {
      liTag += `<li class="inactive">\${lastDateOfLastMonth - i + 1}</li>`;
    }

    for (let i = 1; i <= lastDateOfMonth; i++) {
      let dayDate = new Date(selectedYear, selectedMonth, i); // Create a Date object for the current day
      let isEventDate = eventTimestamps.some(eventTimestamp => {
        const eventDate = new Date(eventTimestamp);
        return dayDate.getDate() === eventDate.getDate() &&
                dayDate.getMonth() === eventDate.getMonth() &&
                dayDate.getFullYear() === eventDate.getFullYear();
      });

      let classNames = "";

      // Check if the day is active (selected)
      if (
              i === selectedDate.getDate() &&
              selectedMonth === selectedDate.getMonth() &&
              selectedYear === selectedDate.getFullYear()
      ) {
        classNames = "active";
      }
      // Check if the day is today
      else if (
              i === date.getDate() &&
              selectedMonth === date.getMonth() &&
              selectedYear === date.getFullYear()
      ) {
        classNames = "today";
      }
      // Check if the day has events
      else if (isEventDate) {
        classNames = "event";
      }

      // Create a link with the timestamp as a query parameter
      liTag += `<li class="\${classNames}" onclick="window.location.href='${pageContext.request.contextPath}/calendar?timestamp=\${dayDate.getTime()}'">\${i}</li>`;

    }


    for (let i = lastDayOfMonth; i < 6; i++) {
      liTag += `<li class="inactive">\${i - lastDayOfMonth + 1}</li>`;
    }

    currentDate.innerText = `\${months[selectedMonth]} \${selectedYear}`;
    daysTag.innerHTML = liTag;
  };

  renderCalendar();

  prevNextIcon.forEach(icon => {
    icon.addEventListener("click", () => {
      selectedMonth = icon.id === "prev" ? selectedMonth - 1 : selectedMonth + 1;
      if (selectedMonth < 0 || selectedMonth > 11) {
        selectedDate.setMonth(selectedMonth);
        selectedYear = selectedDate.getFullYear();
        selectedMonth = selectedDate.getMonth();
      } else {
        selectedDate = new Date();
      }
      renderCalendar();
    });
  });
</script>

