import {Post} from "./post";

export interface Tag {
    tagId: number;
    tag: string;
    posts: Post[];
    self: string;
}
