import { Links } from "./links"

export interface Language {
    language: string
    self: string
}

export interface LanguageDto {
    language: string
    _links: Links
}
