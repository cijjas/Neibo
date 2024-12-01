import { User } from "./user";

export interface Post {
  title: string;
  body: string;
  createdAt: Date;
  image: string;
  channel: string;
  likeCount: number;
  comments: string;
  author: User;
  self: string;
}
