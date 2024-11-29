import { User } from "./user";

export interface Inquiry {
  message: string;
  reply: string;
  date: Date;
  inquirer: User;
  replier: User;
  self: string;
}
