import { User } from "./user"
import { Product } from "./product"

export interface Request {
    requestId: number
    requestMessage: string
    self: string
}
