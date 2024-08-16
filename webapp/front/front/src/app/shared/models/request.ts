import { Links } from "./links"
import { ProductDto } from "./product"
import { UserDto } from "./user"

export interface Request {
    message: string
    requestDate: Date
    purchaseDate: Date
    fulfilled: boolean
    units: number
    product: ProductDto
    user: UserDto
    self: string
}

export interface RequestDto {
    message: string
    requestDate: Date
    purchaseDate: Date
    fulfilled: boolean
    units: number
    _links: Links
}

export interface RequestForm {
    requestId: number
    requestMessage: string
    self: string
}

