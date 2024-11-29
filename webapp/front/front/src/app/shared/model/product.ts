import { User } from "./user";

export interface Product {
  name: string;
  description: string;
  price: number;
  used: boolean;
  remainingUnits: number;
  creationDate: Date;
  image: Uint8Array;
  seller: User;
  department: string;
  self: string;
}
