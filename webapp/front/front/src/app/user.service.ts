import { HttpClient, HttpParams } from '@angular/common/http'
import { UserForm } from './userForm'
import { User } from './user'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class UserService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getUsers(neighborhoodId: number, userRole: string, page: number, size: number): Observable<User[]> {
        const params = new HttpParams().set('userRole', userRole).set('page', page.toString()).set('size', size.toString())

        return this.http.get<User[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/users`, { params })
    }

    public getUser(neighborhoodId: number, userId: number): Observable<User> {
        return this.http.get<User>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/users/${userId}`)
    }

    public addUser(neighborhoodId: number, user: UserForm): Observable<UserForm> {
        return this.http.post<UserForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/users`, user)
    }

    public updateUser(neighborhoodId: number, user: UserForm): Observable<UserForm> {
        return this.http.patch<UserForm>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/users/${user.userId}`, user)
    }

}
