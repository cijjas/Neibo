import { User } from "./user";
import { Links } from "./links";

export interface PostDto {
  title: string;
  descriptions: string;
  date: Date;
  _links: Links;
}

export interface Post {
  title: string;
  descriptions: string;
  date: Date;
  postImage: Uint8Array;
  channel: string;
  likeCount: number;
  authorUser: User;
}
