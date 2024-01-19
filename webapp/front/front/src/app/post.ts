import {User} from "./user";
import {Channel} from "./channel";
import {Image} from "./image";
import {Tag} from "./tag";
import {Like} from "./like";

export interface Post {
    postId: number;
    title: string;
    description: string;
    date: Date;
    user: User;
    channel: Channel;
    postPicture: Image;
    comments: Comment[];
    tags: Tag[];
    likes: Like[];
    subscribers: User[];
    self: string;
}
