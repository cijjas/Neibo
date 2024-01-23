import { PostDto } from "./post"

export interface Tag {
    tagId: number
    tag: string
    posts: PostDto[]
    self: string
}

export interface TagDto {
    tagId: number
    tag: string
    posts: string
    self: string
}

