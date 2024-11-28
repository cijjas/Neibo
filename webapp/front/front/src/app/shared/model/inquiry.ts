import { User } from "./user";
import { Links } from "./links";

export interface InquiryDto {
  message: string;
  reply: string;
  date: Date;
  _links: Links;
}

export interface Inquiry {
  message: string;
  reply: string;
  date: Date;
  inquirer: User;
  replier: User;
}
