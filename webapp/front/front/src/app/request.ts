import { ProductDto } from "./product"
import { UserDto } from "./user"

export interface Request {
    requestId: number
    message: string
    requestDate: Date
    fulfilled: boolean
    product: ProductDto
    user: UserDto
    self: string
}

export interface RequestDto {
    requestId: number
    message: string
    requestDate: Date
    fulfilled: boolean
    product: string
    user: string
    self: string
}

export interface RequestForm {
    requestId: number
    requestMessage: string
    self: string
}

