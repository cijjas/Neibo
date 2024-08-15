import { WorkerDto } from "./worker"
import { UserDto } from "./user"
import { Links } from "./links"

export interface Review {
    rating: number
    review: string
    date: Date
    worker: WorkerDto
    user: UserDto
    self: string
}

export interface ReviewDto {
    rating: number
    review: string
    date: Date
    _links: Links
}

export interface ReviewForm {
    reviewId: number
    rating: number
    review: string
    self: string
  }
