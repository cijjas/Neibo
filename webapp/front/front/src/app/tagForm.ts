import {PostForm} from "./postForm"

export interface TagForm {
    tagId: number
    tag: string
    posts: PostForm[]
    self: string
}
