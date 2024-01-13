import {User} from "./user";
import {Inquiry} from "./inquiry";
import {Department} from "./department";

export interface Product {
  productId: number;
  name: string;
  description: string;
  price: number;
  used: boolean;
  remainingUnits: number;
  primaryPicture: string;
  secondaryPicture: string;
  tertiaryPicture: string;
  seller: User;
  department: Department;
  inquiries: Inquiry[];
  requests: Request[];
  self: string;
}
