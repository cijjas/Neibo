import { ProductDto } from "./product"
import { UserDto } from "./user"

export interface Inquiry {
    inquiryId: number
    message: string
    reply: string
    inquiryDate: Date
    product: ProductDto
    user: UserDto
    self: string
}

export interface InquiryDto {
    inquiryId: number
    message: string
    reply: string
    inquiryDate: Date
    product: string
    user: string
    self: string
}

export interface InquiryForm {
    inquiryId: number
    message: string
    self: string
}
