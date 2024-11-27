import { Links } from './links'

export interface LikeCount {
    likeCount: number;
    self: string;
}

export interface LikeCountDto {
    likeCount: number;
    _links?: Links;
}
