import { Links } from "./links"

export interface Role {
    role: string
    self: string
}

export interface RoleDto {
    role: string
    _links: Links
}
