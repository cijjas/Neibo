import { UserForm } from "./userForm"
import { ProductForm } from "./productForm"

export interface RequestForm {
    requestId: number
    requestMessage: string
    self: string
}
