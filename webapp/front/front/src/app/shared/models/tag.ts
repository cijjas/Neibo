import { Links } from "./links"
import { PostDto } from "./post"

export interface Tag {
    tag: string
    posts: PostDto[]
    self: string
}

export interface TagDto {
    tag: string
    _links: Links
}

