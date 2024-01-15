import { HttpClient } from '@angular/common/http';
import {Role} from './role'
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({providedIn: 'root'})
export class RoleService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) { }

    public getRoles(): Observable<Role[]> {    
        return this.http.get<Role[]>(`${this.apiServerUrl}/roles`);
    }

    public getRole(roleId: number): Observable<Role> {    
        return this.http.get<Role>(`${this.apiServerUrl}/roles/${roleId}`);
    }
}