import { HttpClient, HttpParams } from '@angular/common/http'
import { Like, LikeDto, LikeForm } from '../models/like'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { map } from 'rxjs/operators'

@Injectable({providedIn: 'root'})
export class LikeService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getLikes(postId : number, userId : number, page : number, size : number): Observable<Like[]> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString())
            .set('postId', postId.toString())
            .set('userId', userId.toString())
        return this.http.get<Like[]>(`${this.apiServerUrl}/likes`)
    }

    public isLikedByUser(postId: number, userId: number): Observable<boolean> {
        const params = new HttpParams()
          .set('postId', postId.toString())
          .set('userId', userId.toString());
    
        // Assuming your API response is of type 'Like' or an empty object '{}'
        return this.http.get<{} | Like>(`${this.apiServerUrl}/likes/isLikedByUser`, { params }).pipe(
          map((likeResponse: {} | Like) => {
            // Check if 'likeResponse' is not an empty object and return true, otherwise return false
            return Object.keys(likeResponse).length !== 0;
          })
        );
      }

    public addLike(like: LikeForm): Observable<LikeForm> {
        return this.http.post<LikeForm>(`${this.apiServerUrl}/likes`, like)
    }

}
