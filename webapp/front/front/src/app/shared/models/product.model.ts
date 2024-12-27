import { Department, Inquiry, User } from "./index";

export interface Product {
  name: string;
  description: string;
  price: number;
  used: boolean;
  stock: number;
  inquiries: string;
  totalRequests: number;
  createdAt: Date;
  firstImage: string;
  secondImage: string;
  thirdImage: string;
  seller: User;
  department: Department;
  self: string;
}
