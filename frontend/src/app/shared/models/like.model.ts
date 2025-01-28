import { Post } from "./index";

export interface Like {
  date: Date;
  post: Post;
  self: string;
}
