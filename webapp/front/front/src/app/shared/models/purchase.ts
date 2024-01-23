import { UserDto } from "./user"
import { ProductDto } from "./product"

export interface Purchase {
    purchaseId: number
    units: number
    purchaseDate: Date
    product: ProductDto
    user: UserDto
    self: string
}

export interface PurchaseDto {
    purchaseId: number
    units: number
    purchaseDate: Date
    product: string
    user: string
    self: string
}
