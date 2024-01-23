import { UserDto } from "./user"
import { PostDto } from "./post"

export interface Like {
    likeId: number
    post: PostDto
    user: UserDto
    self: string
}

export interface LikeDto {
    likeId: number
    post: string
    user: string
    self: string
}

export interface LikeForm {
    likeId: number
    postId: number
    self: string
}
