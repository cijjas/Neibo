import { Post } from "./post";

export interface Like {
  date: Date;
  post: Post;
  self: string;
}
