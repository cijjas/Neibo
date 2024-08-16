import { UserDto } from "./user"
import { InquiryDto } from "./inquiry"
import { DepartmentDto } from "./department"
import { RequestDto } from "./request"
import { Links } from "./links"
import { ImageDto } from "./image"

export interface Product {
    name: string
    description: string
    price: number
    used: boolean
    remainingUnits: number
    creationDate: Date
    primaryPicture: ImageDto
    secondaryPicture: ImageDto
    tertiaryPicture: ImageDto
    seller: UserDto
    department: DepartmentDto
    inquiries: InquiryDto[]
    requests: RequestDto[]
    self: string;
}

export interface ProductDto {
    name: string
    description: string
    price: number
    used: boolean
    remainingUnits: number
    creationDate: Date
    _links: Links
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
