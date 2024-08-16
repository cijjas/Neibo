import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http'
import { Department, DepartmentDto } from '../models/department'
import { map, mergeMap, Observable, forkJoin } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../../../environments/environment'
import { LoggedInService } from './loggedIn.service'

@Injectable({providedIn: 'root'})
export class DepartmentService {
    private apiServerUrl = environment.apiBaseUrl
    private headers: HttpHeaders

    constructor(
        private http: HttpClient,
        private loggedInService: LoggedInService
    ) { 
        this.headers = new HttpHeaders({
            'Authorization': this.loggedInService.getAuthToken()
        })
    }

    public getDepartment(department: string): Observable<Department> {
        const departmentDto$ = this.http.get<DepartmentDto>(department, { headers: this.headers })

        return departmentDto$.pipe(
            map((departmentDto: DepartmentDto) => {
                return {
                    department: departmentDto.department,
                    self: departmentDto._links.self
                } as Department;
            })
        );
    }

    public getDepartments(): Observable<Department[]> {
        return this.http.get<DepartmentDto[]>(`${this.apiServerUrl}/departments`, { headers: this.headers }).pipe(
            map((departmentDtos: DepartmentDto[]) => {
                return departmentDtos.map((departmentDto: DepartmentDto) => {
                    return {
                        department: departmentDto.department,
                        self: departmentDto._links.self
                    } as Department;
                });
            })
        );
    }
}
