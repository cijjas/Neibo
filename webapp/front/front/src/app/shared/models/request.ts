import { User } from "./user";

export interface Request {
  message: string;
  unitsRequested: number;
  requestDate: Date;
  purchaseDate: Date;
  requestStatus: 'FULFILLED' | 'WAITING';
  requestingUser: User;
  self: string;
}
