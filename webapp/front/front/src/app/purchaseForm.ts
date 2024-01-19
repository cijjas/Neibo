import { UserForm } from "./userForm"
import { ProductForm } from "./productForm"

export interface PurchaseForm {
    purchaseId: number
    units: number
    purchaseDate: Date
    product: ProductForm
    user: UserForm
    self: string
}
