<html lang="en" dir="ltr">
<head>
  <meta charset="utf-8">
  <title>Dynamic Calendar JavaScript</title>
  <link href="${pageContext.request.contextPath}/resources/css/calendarBox.css" rel="stylesheet"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <!-- Google Font Link for Icons -->
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Rounded:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200">
</head>

<div class="wrapper">
  <header>
    <div class="icons">
      <span id="prev" class="material-symbols-rounded">chevron_left</span>
    </div>
    <p class="current-date"></p>
    <div class="icons">
      <span id="next" class="material-symbols-rounded">chevron_right</span>
    </div>
  </header>
  <div class="divider"></div>
  <div class="calendar">
    <ul class="weeks">
      <li>Sunday</li>
      <li>Monday</li>
      <li>Tuesday</li>
      <li>Wednesday</li>
      <li>Thursday</li>
      <li>Friday</li>
      <li>Saturday</li>
    </ul>
    <ul class="days"></ul>
  </div>
</div>

</html>

<script>
  const daysTag = document.querySelector(".days");
  const currentDate = document.querySelector(".current-date");
  const prevNextIcon = document.querySelectorAll(".icons span");

  // Access the event timestamps array passed from the Java controller
  let eventTimestamps = ${eventDates}; // Assuming eventDates is an array of timestamps

  // getting new date, current year, and month
  let date = new Date();
  let currYear = date.getFullYear();
  let currMonth = date.getMonth();

  // storing full name of all months in array
  const months = ["January", "February", "March", "April", "May", "June", "July",
    "August", "September", "October", "November", "December"];

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
      let isToday = i === date.getDate() && currMonth === new Date().getMonth() && currYear === new Date().getFullYear() ? "active" : "";
      let dayDate = new Date(currYear, currMonth, i); // Create a Date object for the current day
      let isEventDate = eventTimestamps.some(eventTimestamp => {
        const eventDate = new Date(eventTimestamp);
        return dayDate.getDate() === eventDate.getDate() &&
                dayDate.getMonth() === eventDate.getMonth() &&
                dayDate.getFullYear() === eventDate.getFullYear();
      });
      liTag += `<li class="\${isToday} \${isEventDate ? 'event' : ''}">\${i}</li>`;
    }

    for (let i = lastDayOfMonth; i < 6; i++) {
      liTag += `<li class="inactive">\${i - lastDayOfMonth + 1}</li>`;
    }

    currentDate.innerText = `\${months[currMonth]} \${currYear}`;
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
</script>
