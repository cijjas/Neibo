import { UserDto } from "./user"
import { ImageDto } from "./image"
import { TagDto } from "./tag"
import { CommentDto } from "./comment"
import { Links } from "./links"
import { LikeDto } from "./like"
import { ChannelDto } from "./channel"

export interface Post {
    title: string
    description: string
    date: Date
    user: UserDto
    channel: ChannelDto
    postPicture: ImageDto
    comments: CommentDto[]
    tags: TagDto[]
    likes: LikeDto[]
    subscribers: UserDto[]
    self: string
}

export interface PostDto {
    title: string
    description: string
    date: Date
    _links: Links
}


export interface PostForm {
    postId: number
    subject: string
    message: string
    tags: string
    imageFile: string
    channel: number
    self: string
}
