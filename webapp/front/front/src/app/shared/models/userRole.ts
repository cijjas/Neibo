import { Links } from "./links"

export interface UserRole {
    userRole: string
    self: string
}

export interface UserRoleDto {
    userRole: string
    _links: Links
}
