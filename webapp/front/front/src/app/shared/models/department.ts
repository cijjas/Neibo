import { Links } from "./links"

export interface Department {
    department: String
    self: string
}

export interface DepartmentDto {
    department: string
    _links: Links
}
