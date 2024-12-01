import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Post } from '../../models/index';
import { ChannelDto, ImageDto, PostDto, UserDto, LikeCountDto } from '../../dtos/app-dtos';
import { mapUser } from './user.service';
import { parseLinkHeader } from './utils';
import { HateoasLinksService } from '../index.service';


@Injectable({ providedIn: 'root' })
export class PostService {

    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService
    ) { }

    public getPost(postUrl: string): Observable<Post> {
        return this.http.get<PostDto>(postUrl).pipe(
            mergeMap((postDto: PostDto) => mapPost(this.http, postDto))
        );
    }

    public getPosts(
        postsUrl: string,
        queryParams: {
            page?: number;
            size?: number;
            inChannel?: string;
            withTags?: string[];
            withStatus?: string;
            postedBy?: string;
        } = {}
    ): Observable<{ posts: Post[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());
        if (queryParams.inChannel) params = params.set('inChannel', queryParams.inChannel);
        if (queryParams.withTags) params = params.set('withTags', queryParams.withTags.join(','));
        if (queryParams.withStatus) params = params.set('withStatus', queryParams.withStatus);
        if (queryParams.postedBy) params = params.set('postedBy', queryParams.postedBy);

        return this.http
            .get<PostDto[]>(postsUrl, { params, observe: 'response' })
            .pipe(
                mergeMap((response) => {
                    const postsDto = response.body || [];
                    const linkHeader = response.headers.get('Link');
                    const paginationInfo = parseLinkHeader(linkHeader);

                    const postObservables = postsDto.map((postDto) => mapPost(this.http, postDto));

                    return forkJoin(postObservables).pipe(
                        map((posts) => ({
                            posts,
                            totalPages: paginationInfo.totalPages,
                            currentPage: paginationInfo.currentPage,
                        }))
                    );
                })
            );
    }


    /**
    * Creates a new post.
    * @param postForm The form data for creating a post.
    * @returns An Observable of the created Post.
    */
    public createPost(postForm: PostDto): Observable<Post> {
        if (postForm.imageFile) {
            return this.uploadImage(postForm.imageFile).pipe(
                mergeMap((imageUrl: string) => {
                    // Transform PostForm to PostDto
                    const postDto: PostDto = {
                        ...postForm,
                        image: imageUrl, // Use the URL returned from upload
                    };
                    return this.sendCreatePostRequest(postDto);
                })
            );
        } else {
            // Transform PostForm to PostDto without image
            const postDto: PostDto = {
                ...postForm,
            };
            return this.sendCreatePostRequest(postDto);
        }
    }


    /**
     * Sends the create post request to the backend.
     * @param postDto The post data mapped from the form.
     * @returns An Observable of the created Post.
     */
    private sendCreatePostRequest(postDto: PostDto): Observable<Post> {
        const createPostUrl = this.linkService.getLink('neighborhood:posts');

        return this.http.post<PostDto>(createPostUrl, postDto).pipe(
            mergeMap((postDto: PostDto) => mapPost(this.http, postDto))
        );
    }

    /**
     * Uploads an image to the backend.
     * @param image The image file to upload.
     * @returns An Observable of the image URL or URN.
     */
    private uploadImage(image: File): Observable<string> {
        const uploadUrl = this.linkService.getLink('neighborhood:images');

        const formData: FormData = new FormData();
        formData.append('image', image, image.name);

        // After creating the image, the URL of the image is returned in the Location header of the Response
        return this.http.post(uploadUrl, formData, { observe: 'response' }).pipe(
            map(response => {
                const locationHeader = response.headers.get('Location');
                if (locationHeader) {
                    return locationHeader;
                } else {
                    throw new Error('Location header not found in the response');
                }
            })
        );
    }

}

export function mapPost(http: HttpClient, postDto: PostDto): Observable<Post> {
    return forkJoin([
        http.get<ChannelDto>(postDto._links.channel),
        http.get<LikeCountDto>(postDto._links.likeCount),
        http.get<UserDto>(postDto._links.postUser).pipe(mergeMap(userDto => mapUser(http, userDto)))
    ]).pipe(
        map(([channelDto, likeCountDto, user]) => {
            return {
                title: postDto.title,
                body: postDto.body,
                createdAt: postDto.creationDate,
                image: postDto._links.postImage,
                channel: channelDto.name,
                likeCount: likeCountDto.count,
                comments: postDto._links.comments,
                author: user,
                self: postDto._links.self
            } as Post;
        })
    );
}
