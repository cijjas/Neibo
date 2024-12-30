import { parse } from 'uri-template'; // Import the 'parse' function directly

export class ApiEndpoint {
    private uriTemplate: { expand: (values: Record<string, unknown>) => string } | null; // Adjusted type

    constructor(public baseUri: string, template: string | null = null) {
        // Initialize the template only if one is provided
        this.uriTemplate = template ? parse(template) : null;
    }

    hasQueryParams(): boolean {
        return !!this.uriTemplate;
    }

    buildUri(params: Record<string, string | number> = {}): string {
        if (this.uriTemplate) {
            return this.uriTemplate.expand(params); // Use the 'expand' method from the parsed result
        }
        return this.baseUri; // Return the base URI if no template is provided
    }
}
