import { Links } from "./links"

export interface requestStatus {
    requestStatus: string
    self: string
}

export interface RequestStatusDto {
    requestStatus: string
    _links: Links
}
