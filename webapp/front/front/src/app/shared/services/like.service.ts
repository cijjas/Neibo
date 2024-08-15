import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Like, LikeDto, LikeForm } from '../models/like'
import { Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map, mergeMap } from 'rxjs/operators'
import { PostDto } from '../models/post'
import { UserDto } from '../models/user'
import { LikeCount, LikeCountDto } from '../models/likeCount'

@Injectable({providedIn: 'root'})
export class LikeService {
  private apiServerUrl = environment.apiBaseUrl
  private headers: HttpHeaders

  constructor(
      private http: HttpClient,
      private loggedInService: LoggedInService,
  ) { 
      this.headers = new HttpHeaders({
          'Authorization': this.loggedInService.getAuthToken()
      })
  }

    public getLikes(neighborhood: string, post : string, user : string, page : number, size : number): Observable<Like[]> {
        let params = new HttpParams()
        if(page) params = params.set('page', page.toString())
        if(size) params = params.set('size', size.toString())
        if(post) params = params.set('onPost', post)
        if(user) params = params.set('likedBy', user)

        return this.http.get<LikeDto[]>(`${this.apiServerUrl}/likes`, { params, headers: this.headers }).pipe(
          mergeMap((likesDto: LikeDto[]) => {
              const likeObservables = likesDto.map(likeDto =>
                  forkJoin([
                      this.http.get<PostDto>(likeDto._links.post),
                      this.http.get<UserDto>(likeDto._links.user)
                  ]).pipe(
                      map(([post, user]) => {
                          return {
                              likeDate: likeDto.likeDate,
                              post: post,
                              user: user,
                              self: likeDto._links.self
                          } as Like;
                      })
                  )
              );

               return forkJoin(likeObservables);
          })
      );

    }

    public getLikeCount(post : string, user : string): Observable<LikeCount> {
      let params = new HttpParams()
      if(post) params = params.set('onPost', post)
      if(user) params = params.set('likedBy', user)

      const likeCountDto$ = this.http.get<LikeCountDto>(`${this.apiServerUrl}/likes/count`, { params, headers: this.headers })

        return likeCountDto$.pipe(
            map((likeCountDto: LikeCountDto) => {
                return {
                    likeCount: likeCountDto.likeCount,
                    self: likeCountDto._links.self
                } as LikeCount;
            })
        );

    }

    public isLikedByUser(post: string, user: string): Observable<boolean> {
        const params = new HttpParams()
          .set('onPost', post)
          .set('likedBy', user)

        // Assuming your API response is of type 'Like' or an empty object '{}'
        return this.http.get<{} | Like>(`${this.apiServerUrl}/likes/isLikedByUser`, { params }).pipe(
          map((likeResponse: {} | Like) => {
            // Check if 'likeResponse' is not an empty object and return true, otherwise return false
            return Object.keys(likeResponse).length !== 0;
          })
        );
    }


    public addLike(like: LikeForm): Observable<LikeForm> {
        return this.http.post<LikeForm>(`${this.apiServerUrl}/likes`, like, { headers: this.headers })
    }

    public deleteLike(like: string): Observable<void> {
      return this.http.delete<void>(like, { headers: this.headers })
    }

}
