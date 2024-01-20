export interface PostDto {
    postId: number;
    title: string;
    description: string;
    date: Date;
    user: string;
    channel: string;
    postPicture: string;
    comments: Comment[];
    tags: string;
    likes: string;
    subscribers: string;
    self: string;
}
