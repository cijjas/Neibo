import { User } from "./user";

export interface Post {
  title: string;
  body: string;
  date: Date;
  postImage: string;
  channel: string;
  likeCount: number;
  author: User;
  self: string;
}
