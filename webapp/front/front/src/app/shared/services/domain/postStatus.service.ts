import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { PostStatus } from '../../models/index';
import { PostStatusDto } from '../../dtos/app-dtos';
import { HateoasLinksService } from '../../../core/services/link.service';

@Injectable({ providedIn: 'root' })
export class DepartmentService {
    constructor(
        private http: HttpClient,
        private linkService: HateoasLinksService
    ) { }

    public getDepartment(url: string): Observable<PostStatus> {
        return this.http.get<PostStatusDto>(url).pipe(
            map(mapDepartment)
        );
    }

    public getDepartments(): Observable<PostStatus[]> {
        const url = this.linkService.getLink('neighborhood:postStatus')
        return this.http.get<PostStatusDto[]>(url).pipe(
            map((departmentDtos) => departmentDtos.map(mapDepartment))
        );
    }
}

export function mapDepartment(departmentDto: PostStatusDto): PostStatus {
    return {
        status: departmentDto.status,
        self: departmentDto._links.self
    };
}
