import { Post } from "./post";
import { Links } from "./links";

export interface LikeDto {
  date: Date;
  _links: Links;
}

export interface Like {
  date: Date;
  post: Post;
}
