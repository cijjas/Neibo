import { Links } from "./links"

export interface Image {
    image: string
    self: string
}

export interface ImageDto {
    image: string
    _links: Links
}

export interface ImageForm {
    image: string
    self: string
}
