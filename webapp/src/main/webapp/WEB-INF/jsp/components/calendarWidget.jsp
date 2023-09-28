<html lang="en" dir="ltr">
<head>
  <meta charset="utf-8">
  <title>Dynamic Calendar JavaScript</title>
  <link href="${pageContext.request.contextPath}/resources/css/calendarWidget.css" rel="stylesheet"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <!-- Google Font Link for Icons -->
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Rounded:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200">
</head>

  <div class="wrapper">
    <header>
      <p class="current-date"></p>
      <div class="icons">
        <span id="prev" class="material-symbols-rounded">chevron_left</span>
        <span id="next" class="material-symbols-rounded">chevron_right</span>
      </div>
    </header>
    <div class="calendar">
      <ul class="weeks">
        <li>S</li>
        <li>M</li>
        <li>T</li>
        <li>W</li>
        <li>T</li>
        <li>F</li>
        <li>S</li>
      </ul>
      <ul class="days"></ul>
    </div>
  </div>

</html>

<script>
  const daysTag = document.querySelector(".days"),
          currentDate = document.querySelector(".current-date"),
          prevNextIcon = document.querySelectorAll(".icons span");
  // getting new date, current year and month
  let date = new Date(),
          currYear = date.getFullYear(),
          currMonth = date.getMonth();
  // storing full name of all months in array
  const months = ["January", "February", "March", "April", "May", "June", "July",
    "August", "September", "October", "November", "December"];
  const renderCalendar = () => {
    let firstDayOfMonth = new Date(currYear, currMonth, 1).getDay(), // getting first day of month
            lastDateOfMonth = new Date(currYear, currMonth + 1, 0).getDate(), // getting last date of month
            lastDayOfMonth = new Date(currYear, currMonth, lastDateOfMonth).getDay(), // getting last day of month
            lastDateOfLastMonth = new Date(currYear, currMonth, 0).getDate(); // getting last date of previous month
    let liTag = "";
    for (let i = firstDayOfMonth; i > 0; i--) { // creating li of previous month last days
      liTag += `<li class="inactive">\${lastDateOfLastMonth - i + 1}</li>`;
      console.log(lastDateOfLastMonth - i + 1)
    }
    for (let i = 1; i <= lastDateOfMonth; i++) { // creating li of all days of current month
      // adding active class to li if the current day, month, and year matched
      let isToday = i === date.getDate() && currMonth === new Date().getMonth() && currYear === new Date().getFullYear() ? "active" : "";
      liTag += `<li class="\${isToday}">\${i}</li>`;
      console.log(i);
    }
    for (let i = lastDayOfMonth; i < 6; i++) { // creating li of next month first days
      liTag += `<li class="inactive">\${i - lastDayOfMonth + 1}</li>`;
      console.log(i - lastDayOfMonth + 1);
    }
    currentDate.innerText = `\${months[currMonth]} \n \${currYear}`; // passing current mon and yr as currentDate text
    daysTag.innerHTML = liTag;
  }
  renderCalendar();
  prevNextIcon.forEach(icon => { // getting prev and next icons
    icon.addEventListener("click", () => { // adding click event on both icons
      // if clicked icon is previous icon then decrement current month by 1 else increment it by 1
      currMonth = icon.id === "prev" ? currMonth - 1 : currMonth + 1;
      if(currMonth < 0 || currMonth > 11) { // if current month is less than 0 or greater than 11
        // creating a new date of current year & month and pass it as date value
        date = new Date(currYear, currMonth, new Date().getDate());
        currYear = date.getFullYear(); // updating current year with new date year
        currMonth = date.getMonth(); // updating current month with new date month
      } else {
        date = new Date(); // pass the current date as date value
      }
      renderCalendar(); // calling renderCalendar function
    });
  });
</script>
