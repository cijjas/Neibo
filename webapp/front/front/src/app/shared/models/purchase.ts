import { UserDto } from "./user"
import { ProductDto } from "./product"
import { Links } from "./links"

export interface Purchase {
    units: number
    purchaseDate: Date
    product: ProductDto
    user: UserDto
    self: string
}

export interface PurchaseDto {
    units: number
    purchaseDate: Date
    _links: Links
}
