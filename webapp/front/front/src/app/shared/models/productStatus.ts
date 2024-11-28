import { Links } from "./links"

export interface ProductStatus {
    productStatus: string
    self: string
}

export interface ProductStatusDto {
    productStatus: string
    _links: Links
}
