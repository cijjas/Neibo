import { parse } from 'uri-template';

export class ApiEndpoint {
    private uriTemplate: { expand: (values: Record<string, unknown>) => string } | null;

    constructor(public baseUri: string, template: string | null = null) {
        // Only parse if we actually have a URI template with placeholders
        this.uriTemplate = template ? parse(template) : null;
    }

    hasQueryParams(): boolean {
        return !!this.uriTemplate;
    }

    /**
     * Filters out any null, undefined, or empty-string/empty-array parameters,
     * but leaves arrays as arrays for the URI template library to handle properly.
     */
    private filterParams(params: Record<string, unknown>): Record<string, unknown> {
        const filteredEntries = Object.entries(params).filter(([key, value]) => {
            if (value === undefined || value === null) {
                return false;
            }
            if (typeof value === 'string' && value.trim().length === 0) {
                return false;
            }
            if (Array.isArray(value) && value.length === 0) {
                return false;
            }
            return true;
        });

        return Object.fromEntries(filteredEntries);
    }

    /**
     * Build the final URI from the template + filtered parameters.
     * If no URI template is present, returns the baseUri as-is.
     */
    buildUri(params: Record<string, unknown> = {}): string {
        const filteredParams = this.filterParams(params);

        if (this.uriTemplate) {
            return this.uriTemplate.expand(filteredParams);
        }

        return this.baseUri;
    }
}
