import { Links } from "./links"

export interface Profession {
    profession: string
    self: string
}

export interface ProfessionDto {
    profession: string
    _links: Links
}
