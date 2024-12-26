import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Department } from '../../models/index';
import { DepartmentDto } from '../../dtos/app-dtos';
import { HateoasLinksService } from '../index.service';
import { formatName } from './utils';

@Injectable({ providedIn: 'root' })
export class DepartmentService {
    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService
    ) { }

    public getDepartment(url: string): Observable<Department> {
        return this.http.get<DepartmentDto>(url).pipe(
            map(mapDepartment)
        );
    }

    public getDepartments(): Observable<Department[]> {
        const url = this.linkService.getLink('neighborhood:departments')

        return this.http.get<DepartmentDto[]>(url).pipe(
            map((departmentDtos) => departmentDtos.map(mapDepartment))
        );
    }
}

export function mapDepartment(departmentDto: DepartmentDto): Department {
    return {
        name: departmentDto.name,
        displayName: formatName(departmentDto.name), // Add formatted name
        self: departmentDto._links.self
    };
}
