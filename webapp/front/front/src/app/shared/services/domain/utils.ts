export function parseLinkHeader(header: string | null): { totalPages: number; currentPage: number } {
    let totalPages = 1;
    let currentPage = 1;

    if (header) {
        // Split the header into individual links
        const links = header.split(',');

        let lastPage = 1;
        let selfPage = 1;

        links.forEach((link) => {
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
        .split('_')                         // Split by underscore
        .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())  // Capitalize each word
        .join(' ');                         // Join words with spaces
}