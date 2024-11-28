import { User } from "./user";
import { Links } from "./links";

export interface ProductDto {
  name: string;
  description: string;
  price: number;
  used: boolean;
  remainingUnits: number;
  date: Date;
  _links: Links;
}

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
}
