import {User} from "./user";
import {Product} from "./product";

export interface Request {
    requestId: number;
    message: string;
    requestDate: Date;
    fulfilled: boolean;
    product: Product;
    user: User;
    self: string;
}
