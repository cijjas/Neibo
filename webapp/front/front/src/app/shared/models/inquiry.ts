import { Links } from "./links"
import { ProductDto } from "./product"
import { UserDto } from "./user"

export interface Inquiry {
    message: string
    reply: string
    inquiryDate: Date
    product: ProductDto
    user: UserDto
    self: string
}

export interface InquiryDto {
    message: string
    reply: string
    date: Date
    _links: Links
}

export interface InquiryForm {
    inquiryId: number
    message: string
    self: string
}
