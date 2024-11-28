import { Links } from "./links"

export interface UserRole {
    role: string
    self: string
}

export interface UserRoleDto {
    role: string
    _links: Links
}
