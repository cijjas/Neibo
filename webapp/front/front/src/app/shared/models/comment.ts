import { Links } from "./links"
import { PostDto } from "./post"
import { UserDto } from "./user"

export interface Comment {
    comment: string
    date: Date
    user: UserDto
    post: PostDto
    self: string
}

export interface CommentDto {
    comment: string
    date: Date
    _links: Links
}

export interface CommentForm {
    commentId: number
    comment: string
    self: string
}
