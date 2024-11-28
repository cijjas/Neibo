import { Links } from "./links"

export interface TransactionType {
    transactionType: string
    self: string
}

export interface TransactionTypeDto {
    transactionType: string
    _links: Links
}
