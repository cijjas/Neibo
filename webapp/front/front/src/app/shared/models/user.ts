import { NeighborhoodDto } from "./neighborhood"
import { ImageDto } from "./image"
import { CommentDto } from "./comment"
import { PostDto } from "./post"
import { BookingDto } from "./booking"
import { ProductDto } from "./product"
import { EventDto } from "./event"
import { Links } from "./links"
import { UserRoleDto } from "./userRole"

export interface User {
    mail: string
    name: string
    surname: string
    //password: string
    neighborhood: NeighborhoodDto
    darkMode: boolean
    phoneNumber: string
    profilePicture: ImageDto
    identification: number
    creationDate: Date
    comments: CommentDto[]
    posts: PostDto[]
    bookings: BookingDto[]
    //subscribedPosts: PostDto[]
    likedPosts: PostDto[]
    purchases: ProductDto[]
    sales: ProductDto[]
    //eventsSubscribed: EventDto[]
    userRole: UserRoleDto
    self: string
}

export interface UserDto {
    mail: string
    name: string
    surname: string
    //password: string
    darkMode: boolean
    phoneNumber: string
    identification: number
    creationDate: Date
    _links: Links
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
}
