import { User } from "./user";

export interface Product {
  name: string;
  description: string;
  price: number;
  used: boolean;
  remainingUnits: number;
  creationDate: Date;
  firstImage: string;
  secondImage: string;
  thirdImage: string;
  seller: User;
  department: string;
  self: string;
}
