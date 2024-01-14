import { HttpClient, HttpParams } from '@angular/common/http';
import { Department } from './department'
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({providedIn: 'root'})
export class DepartmentService {
    private apiServerUrl = environment.apiBaseUrl;

    constructor(private http: HttpClient) { }

    public getDepartment(departmentId : number): Observable<Department> {
        return this.http.get<Department>(`${this.apiServerUrl}/departments/${departmentId}`)
    }

    public getDepartments(): Observable<Department[]> {
        return this.http.get<Department[]>(`${this.apiServerUrl}/departments`)
    }

}