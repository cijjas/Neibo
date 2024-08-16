import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { Role, RoleDto } from '../models/role'
import { Observable, map } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'

@Injectable({providedIn: 'root'})
export class RoleService {
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

    public getRoles(): Observable<Role[]> {
        return this.http.get<RoleDto[]>(`${this.apiServerUrl}/roles`, { headers: this.headers }).pipe(
            map((roleDtos: RoleDto[]) => {
                return roleDtos.map((roleDto: RoleDto) => {
                    return {
                        role: roleDto.role,
                        self: roleDto._links.self
                    } as Role;
                });
            })
        );
    }

    public getRole(role: string): Observable<Role> {
        const roleDto$ = this.http.get<RoleDto>(role, { headers: this.headers })

        return roleDto$.pipe(
            map((roleDto: RoleDto) => {
                return {
                    role: roleDto.role,
                    self: roleDto._links.self
                } as Role;
            })
        );
    }
}
