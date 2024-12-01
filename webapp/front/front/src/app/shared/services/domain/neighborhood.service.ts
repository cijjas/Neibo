import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { Neighborhood } from '../../models/index';
import { NeighborhoodDto } from '../../dtos/app-dtos';
import { parseLinkHeader } from './utils';

@Injectable({ providedIn: 'root' })
export class NeighborhoodService {
    constructor(private http: HttpClient) { }

    public getNeighborhood(url: string): Observable<Neighborhood> {
        return this.http.get<NeighborhoodDto>(url).pipe(
            map((neighborhoodDto: NeighborhoodDto) => mapNeighborhood(neighborhoodDto))
        );
    }

    public getNeighborhoods(
        url: string,
        queryParams: {
            page?: number;
            size?: number;
            withWorker?: string;
        } = {}
    ): Observable<{ neighborhoods: Neighborhood[]; totalPages: number; currentPage: number }> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());
        if (queryParams.withWorker) params = params.set('withWorker', queryParams.withWorker);

        return this.http.get<NeighborhoodDto[]>(url, { params, observe: 'response' }).pipe(
            map((response) => {
                const neighborhoodsDto: NeighborhoodDto[] = response.body || [];
                const pagination = parseLinkHeader(response.headers.get('Link'));

                const neighborhoods = neighborhoodsDto.map(mapNeighborhood);
                return {
                    neighborhoods,
                    totalPages: pagination.totalPages,
                    currentPage: pagination.currentPage
                };
            })
        );
    }
}

export function mapNeighborhood(neighborhoodDto: NeighborhoodDto): Neighborhood {
    return {
        name: neighborhoodDto.name,
        self: neighborhoodDto._links.self
    };
}