import { Product, User } from "./index";

export interface Request {
  message: string;
  unitsRequested: number;
  createdAt: Date;
  fulfilledAt: Date;
  requestStatus: string;
  requestingUser: User;
  product: Product; // creo que va a ser necesario
  self: string;
}
