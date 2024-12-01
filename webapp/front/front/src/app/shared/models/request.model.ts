import { User } from "./index";

export interface Request {
  message: string;
  unitsRequested: number;
  createdAt: Date;
  fulfilledAt: Date;
  requestStatus: 'FULFILLED' | 'WAITING';
  requestingUser: User;
  self: string;
}
