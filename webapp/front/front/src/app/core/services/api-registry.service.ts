import { Injectable } from "@angular/core";
import { ApiEndpoint } from "./api-endpoint.model";

@Injectable({
    providedIn: 'root',
})
export class ApiRegistry {
    private endpoints: Map<string, ApiEndpoint> = new Map();

    registerEndpoint(name: string, baseUri: string, template: string | null = null): void {
        this.endpoints.set(name, new ApiEndpoint(baseUri, template));
        console.log(this.endpoints);
    }

    getEndpoint(name: string): ApiEndpoint | undefined {
        return this.endpoints.get(name);
    }
}
