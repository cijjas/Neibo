import {Post} from "./post";
import {User} from "./user";

export interface Comment {
    commentId: number;
    comment: string;
    date: Date;
    user: User;
    post: Post;
    self: string;
}
