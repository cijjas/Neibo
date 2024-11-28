import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'
import { LoggedInService } from './loggedIn.service'
import { UserRole, UserRoleDto } from '../models/userRole'
import { Observable, map } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'

@Injectable({ providedIn: 'root' })
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

    public getRoles(): Observable<UserRole[]> {
        return this.http.get<UserRoleDto[]>(`${this.apiServerUrl}/roles`, { headers: this.headers }).pipe(
            map((roleDtos: UserRoleDto[]) => {
                return roleDtos.map((roleDto: UserRoleDto) => {
                    return {
                        role: roleDto.role,
                        self: roleDto._links.self
                    } as UserRole;
                });
            })
        );
    }

    public getRole(role: string): Observable<UserRole> {
        const roleDto$ = this.http.get<UserRoleDto>(role, { headers: this.headers })

        return roleDto$.pipe(
            map((roleDto: UserRoleDto) => {
                return {
                    role: roleDto.role,
                    self: roleDto._links.self
                } as UserRole;
            })
        );
    }
}
