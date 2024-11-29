  import { User } from "./user";

  export interface Post {
    title: string;
    description: string;
    date: Date;
    postImage: Uint8Array;
    channel: string;
    likeCount: number;
    authorUser: User;
    self: string;
  }
