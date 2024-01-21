import { WorkerDto } from "./worker"
import { UserDto } from "./user"

export interface Review {
    reviewId: number
    rating: number
    review: string
    date: Date
    worker: WorkerDto
    user: UserDto
    self: string
}

export interface ReviewDto {
    reviewId: number
    rating: number
    review: string
    date: Date
    worker: string
    user: string
    self: string
}

export interface ReviewForm {
    reviewId: number
    rating: number
    review: string
    self: string
  }  
