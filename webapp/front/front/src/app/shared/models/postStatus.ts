import { Links } from "./links"

export interface PostStatus {
    postStatus: string
    self: string
}

export interface PostStatusDto {
    postStatus: string
    _links: Links
}
