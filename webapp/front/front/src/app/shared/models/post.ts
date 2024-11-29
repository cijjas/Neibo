import { User } from "./user";

export interface Post {
  title: string;
  body: string;
  date: Date;
  postImage: Uint8Array;
  channel: string;
  likeCount: number;
  author: User;
  self: string;
}
