// Wrap the entire script in a function
(function() {
    const postDateElements = document.querySelectorAll('.post-date');
    console.log(postDateElements);
// Iterate through each element and update the date
    postDateElements.forEach((postDateElement) => {
        const dateString = postDateElement.getAttribute('data-post-date');
        postDateElement.textContent = formatDateToRelativeTime(dateString);
    });
    function formatDateToRelativeTime(dateString) {
        const currentDate = new Date();
        const currenDateTime = currentDate.getTime();
        const providedDate = new Date(dateString);
        const providedDateTime = providedDate.getTime();
        const timeDifferenceInSeconds = (currenDateTime - providedDateTime)/1000 ;
        let result;

        if (timeDifferenceInSeconds < 60) {
            result = Math.floor(timeDifferenceInSeconds) + ' second' + (timeDifferenceInSeconds !== 1 ? 's' : '') + ' ago';
        } else if (timeDifferenceInSeconds < 3600) {
            const minutesAgo = Math.floor(timeDifferenceInSeconds / 60);
            result = minutesAgo + ' minute' + (minutesAgo !== 1 ? 's' : '') + ' ago';
        } else if (timeDifferenceInSeconds < 86400) {
            const hoursAgo = Math.floor(timeDifferenceInSeconds / 3600);
            result = hoursAgo + ' hour' + (hoursAgo !== 1 ? 's' : '') + ' ago';
        } else {
            const daysAgo = Math.floor(timeDifferenceInSeconds / 86400);
            result = daysAgo + ' day' + (daysAgo !== 1 ? 's' : '') + ' ago';
        }

        return result  ;
    }
})();
