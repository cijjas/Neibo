import { UserDto } from "./user"
import { ReviewDto } from "./review"
import { Profession } from "./profession"
import { NeighborhoodDto } from "./neighborhood"

export interface Worker {
    workerId: number
    phoneNumber: string
    businessName: string
    address: string
    bio: string
    user: UserDto
    backgroundPicture: string
    reviews: ReviewDto[]
    professions: Profession[]
    workerNeighborhoods: NeighborhoodDto[]
    self: string
}

export interface WorkerDto {
    workerId: number
    phoneNumber: string
    businessName: string
    address: string
    bio: string
    user: string
    backgroundPicture: string
    reviews: string
    professions: string
    workerNeighborhoods: string
    self: string
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

