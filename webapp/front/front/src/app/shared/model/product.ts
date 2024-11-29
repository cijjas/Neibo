import { User } from "./user";

export interface Product {
  name: string;
  description: string;
  price: number;
  used: boolean;
  remainingUnits: number;
  creationDate: Date;
  firstImage: Uint8Array;
  secondImage: Uint8Array;
  thirdImage: Uint8Array;
  seller: User;
  department: string;
  self: string;
}
