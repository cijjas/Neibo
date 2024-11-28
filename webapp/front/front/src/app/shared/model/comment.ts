import { User } from "./user";
import { Links } from "./links";

export interface CommentDto {
  comment: string;
  date: Date;
  _links: Links;
}

export interface Comment {
  comment: string;
  date: Date;
  author: User;
}
