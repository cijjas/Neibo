import { User } from "./index";

export interface Product {
  name: string;
  description: string;
  price: number;
  used: boolean;
  stock: number;
  createdAt: Date;
  firstImage: string;
  secondImage: string;
  thirdImage: string;
  seller: User;
  department: string;
  self: string;
}
