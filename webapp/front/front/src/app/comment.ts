import { PostDto } from "./post"
import { UserDto } from "./user"

export interface Comment {
    commentId: number
    comment: string
    date: Date
    user: UserDto
    post: PostDto
    self: string
}

export interface CommentDto {
    commentId: number
    comment: string
    date: Date
    user: string
    post: string
    self: string
}

export interface CommentForm {
    commentId: number
    comment: string
    self: string
}
