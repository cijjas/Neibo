import {Product} from "./product";
import {User} from "./user";

export interface Inquiry {
    inquiryId: number;
    message: string;
    reply: string;
    inquiryDate: Date;
    product: Product;
    user: User;
    self: string;
}
