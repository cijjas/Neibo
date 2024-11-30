import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Neighborhood } from '../../models/index';
import { NeighborhoodDto } from '../../dtos/app-dtos';

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
    ): Observable<Neighborhood[]> {
        let params = new HttpParams();

        if (queryParams.page !== undefined) params = params.set('page', queryParams.page.toString());
        if (queryParams.size !== undefined) params = params.set('size', queryParams.size.toString());
        if (queryParams.withWorker) params = params.set('withWorker', queryParams.withWorker);

        return this.http.get<NeighborhoodDto[]>(url, { params }).pipe(
            map((neighborhoodsDto: NeighborhoodDto[]) => neighborhoodsDto.map(mapNeighborhood))
        );
    }

}

export function mapNeighborhood(neighborhoodDto: NeighborhoodDto): Neighborhood {
    return {
        name: neighborhoodDto.name,
        self: neighborhoodDto._links.self
    };
}
