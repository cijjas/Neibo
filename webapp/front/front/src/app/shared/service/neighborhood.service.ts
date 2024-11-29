import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Neighborhood } from '../model/neighborhood';
import { NeighborhoodDto } from '../dtos/app-dtos';

@Injectable({ providedIn: 'root' })
export class NeighborhoodService {
    constructor(private http: HttpClient) { }

    public getNeighborhood(url: string): Observable<Neighborhood> {
        return this.http.get<NeighborhoodDto>(url).pipe(
            map((neighborhoodDto: NeighborhoodDto) => mapNeighborhood(neighborhoodDto))
        );
    }

    public listNeighborhoods(url: string, page: number, size: number): Observable<Neighborhood[]> {
        let params = new HttpParams();
        if (page) params = params.set('page', page.toString());
        if (size) params = params.set('size', size.toString());

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

