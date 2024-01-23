import { UserDto } from "./user"
import { InquiryDto } from "./inquiry"
import { Department } from "./department"
import { RequestDto } from "./request"

export interface Product {
    productId: number
    name: string
    description: string
    price: number
    used: boolean
    remainingUnits: number
    primaryPicture: string
    secondaryPicture: string
    tertiaryPicture: string
    seller: UserDto
    department: Department
    inquiries: InquiryDto[]
    requests: RequestDto[]
    self: string;
}

export interface ProductDto {
    productId: number
    name: string
    description: string
    price: number
    used: boolean
    remainingUnits: number
    primaryPicture: string
    secondaryPicture: string
    tertiaryPicture: string
    seller: string
    department: string
    inquiries: string
    requests: string
    self: string
}

export interface ProductForm {
    productId: number
    title: string
    price: number
    imageFiles: string[]
    description: string
    departmentId: number
    quantity: number
    used: boolean
    self: string
  }
