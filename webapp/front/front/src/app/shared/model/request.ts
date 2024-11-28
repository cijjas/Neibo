import { User } from "./user";
import { Links } from "./links";

export interface RequestDto {
  message: string;
  unitsRequested: number;
  requestDate: Date;
  purchaseDate: Date;
  _links: Links;
}

export interface Request {
  message: string;
  unitsRequested: number;
  requestDate: Date;
  purchaseDate: Date;
  requestStatus: 'FULFILLED' | 'WAITING';
  requestingUser: User;
}
