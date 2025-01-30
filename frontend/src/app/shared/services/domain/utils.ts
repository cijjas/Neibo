// uitls.ts

export function parseLinkHeader(header: string | null): {
  totalPages: number;
  currentPage: number;
} {
  let totalPages = 1;
  let currentPage = 1;

  if (header) {
    // Split the header into individual links
    const links = header.split(',');

    let lastPage = 1;
    let selfPage = 1;

    links.forEach(link => {
      const match = link.match(/<([^>]+)>;\s*rel="([^"]+)"/);
      if (match) {
        const url = match[1];
        const rel = match[2];

        // Extract the page parameter from the URL
        const pageMatch = url.match(/[\?&]page=(\d+)/);
        if (pageMatch) {
          const page = parseInt(pageMatch[1], 10);
          if (rel === 'last') {
            lastPage = page;
          } else if (rel === 'self') {
            selfPage = page;
          }
        }
      }
    });

    totalPages = lastPage;
    currentPage = selfPage;
  }

  return { totalPages, currentPage };
}

export function formatName(name: string): string {
  return name
    .split('_') // Split by underscore
    .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()) // Capitalize each word
    .join(' '); // Join words with spaces
}

/**
 * Formats a time string (e.g., "13:00:00") into a more user-friendly format (e.g., "1:00 PM").
 * Supports localization and customizable time formats.
 *
 * @param timeString - The time string in "HH:mm:ss" or "HH:mm" format.
 * @param locale - (Optional) Locale string for formatting. Defaults to the user's locale.
 * @param options - (Optional) Intl.DateTimeFormat options for customization.
 * @returns A formatted time string or an error message if input is invalid.
 */
export function formatTime(
  timeString: string,
  locale: string = navigator.language,
  options?: Intl.DateTimeFormatOptions,
): string {
  // Default formatting options if none are provided
  const defaultOptions: Intl.DateTimeFormatOptions = {
    hour: 'numeric',
    minute: 'numeric',
    hour12: true, // Change to false for 24-hour format
  };

  // Merge default options with any user-provided options
  const formatOptions = { ...defaultOptions, ...options };

  // Regular expression to validate and parse the time string
  const timeRegex = /^([01]\d|2[0-3]):([0-5]\d)(?::([0-5]\d))?$/;
  const match = timeString.match(timeRegex);

  if (!match) {
    console.error(
      `Invalid time format: "${timeString}". Expected "HH:mm:ss" or "HH:mm".`,
    );
    return 'Invalid time';
  }

  const [_, hours, minutes, seconds] = match;

  // Create a Date object with today's date and the parsed time
  const now = new Date();
  now.setHours(parseInt(hours, 10));
  now.setMinutes(parseInt(minutes, 10));
  now.setSeconds(seconds ? parseInt(seconds, 10) : 0);
  now.setMilliseconds(0);

  try {
    const formatter = new Intl.DateTimeFormat(locale, formatOptions);
    return formatter.format(now);
  } catch (error) {
    console.error(`Error formatting time: ${error}`);
    return 'Invalid time';
  }
}

/**
 * Formats a Date object or a YYYY-MM-DD string into a more user-friendly format.
 *
 * @param dateInput - A Date object or a "YYYY-MM-DD" string.
 * @param format - The output format: "DD MMM YYYY" (e.g., "06 Feb 2025") or "DD MMM" (e.g., "06 Feb").
 * @param locale - (Optional) Locale string (default: user's locale).
 * @returns A formatted date string or "Invalid date" if input is incorrect.
 */
export function formatDate(
  dateInput: Date | string,
  format: 'DD MMM YYYY' | 'DD MMM' = 'DD MMM YYYY',
  locale: string = navigator.language,
): string {
  let date: Date;

  // If input is already a Date object, use it directly
  if (dateInput instanceof Date) {
    date = dateInput;
  }
  // If input is a string, validate and parse it
  else if (typeof dateInput === 'string') {
    const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
    if (!dateRegex.test(dateInput)) {
      console.warn(
        `Invalid date format: "${dateInput}". Expected "YYYY-MM-DD".`,
      );
      return 'Invalid date';
    }
    const [year, month, day] = dateInput.split('-').map(Number);
    date = new Date(year, month - 1, day); // Convert to Date object
  } else {
    console.error(`Invalid date input:`, dateInput);
    return 'Invalid date';
  }

  // Define format options
  const options: Intl.DateTimeFormatOptions =
    format === 'DD MMM YYYY'
      ? { day: '2-digit', month: 'short', year: 'numeric' }
      : { day: '2-digit', month: 'short' };

  try {
    return new Intl.DateTimeFormat(locale, options).format(date);
  } catch (error) {
    console.error(`Error formatting date: ${error}`);
    return 'Invalid date';
  }
}
