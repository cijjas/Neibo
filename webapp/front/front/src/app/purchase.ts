import {User} from "./user";
import {Product} from "./product";

export interface Purchase {
    purchaseId: number;
    units: number;
    purchaseDate: Date;
    product: Product;
    user: User;
    self: string;
}
