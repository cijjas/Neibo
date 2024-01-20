import { UserDto } from "./user"
import { Channel } from "./channel"
import { ImageDto } from "./image"
import { TagDto } from "./tag"
import { LikeDto } from "./like"
import { CommentDto } from "./comment"

export interface Post {
    postId: number
    title: string
    description: string
    date: Date
    user: UserDto
    channel: Channel
    postPicture: ImageDto
    comments: CommentDto[]
    tags: TagDto[]
    likes: LikeDto[]
    subscribers: UserDto[]
    self: string
}

export interface PostDto {
    postId: number
    title: string
    description: string
    date: Date
    user: string
    channel: string
    postPicture: string
    comments: string
    tags: string
    likes: string
    subscribers: string
    self: string
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
  
