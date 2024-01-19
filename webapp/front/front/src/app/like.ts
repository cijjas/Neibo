import {User} from "./user";
import {Post} from "./post";

export interface Like {
    likeId: number;
    post: Post;
    user: User;
    self: string;
}
