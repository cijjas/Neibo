import { UserDto } from "./user"
import { ReviewDto } from "./review"
import { ProfessionDto } from "./profession"
import { NeighborhoodDto } from "./neighborhood"
import { Links } from "./links"
import { ImageDto } from "./image"

export interface Worker {
    phoneNumber: string
    businessName: string
    address: string
    bio: string
    user: UserDto
    backgroundPicture: ImageDto
    reviews: ReviewDto[]
    professions: ProfessionDto[]
    workerNeighborhoods: NeighborhoodDto[]
    self: string
}

export interface WorkerDto {
    phoneNumber: string
    businessName: string
    address: string
    bio: string
    _links: Links
}

export interface WorkerForm {
    workerId: number
    name: string
    surname: string
    professionIds: number[]
    phoneNumber: string
    businessName: string
    address: string
    mail: string
    password: string
    identification: string
    language: string
    bio: string
    backgroundPicture: string
    self: string
}

