import { NeighborhoodDto } from "./neighborhood"
import { ProductDto } from "./product"
import { PostDto} from "./post"
import { ImageDto } from "./image"
import { BookingDto } from "./booking"
import { CommentDto } from "./comment"
import { EventDto } from "./event"

export interface User {
    userId: number
    mail: string
    name: string
    surname: string
    password: string
    neighborhood: NeighborhoodDto
    darkMode: boolean
    phoneNumber: string
    profilePicture: ImageDto
    identification: number
    comments: CommentDto[]
    posts: PostDto[]
    bookings: BookingDto[]
    subscribedPosts: PostDto[]
    likedPosts: PostDto[]
    purchases: ProductDto[]
    sales: ProductDto[]
    eventsSubscribed: EventDto[]
    self: string
}

export interface UserDto {
    userId: number
    mail: string
    name: string
    surname: string
    password: string
    neighborhood: string
    darkMode: boolean
    phoneNumber: string
    profilePicture: string
    identification: number
    comments: string
    posts: string
    bookings: string
    subscribedPosts: string
    likedPosts: string
    purchases: string
    sales: string
    eventsSubscribed: string
    self: string
}

export interface UserForm {
    userId: number
    name: string
    suername: string
    neighborhoodId: number
    mail: string
    password: string
    identification: string
    language: string
    languageId: number
    userRoleId: number
    darkMode: boolean
    profilePicture: string
    self: string
}

