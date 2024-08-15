import { UserDto } from "./user"
import { PostDto } from "./post"
import { Links } from "./links"

export interface Like {
    likeDate: Date
    post: PostDto
    user: UserDto
    self: string
}

export interface LikeDto {
    likeDate: Date
    _links: Links
}

export interface LikeForm {
    likeId: number
    postId: number
    self: string
}
