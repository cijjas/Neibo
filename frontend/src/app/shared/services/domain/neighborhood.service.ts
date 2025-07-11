import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  LinkKey,
  Neighborhood,
  NeighborhoodDto,
  parseLinkHeader,
} from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Injectable({ providedIn: 'root' })
export class NeighborhoodService {
  constructor(
    private http: HttpClient,
    private linkService: HateoasLinksService,
  ) {}

  public getNeighborhood(url: string): Observable<Neighborhood> {
    return this.http
      .get<NeighborhoodDto>(url)
      .pipe(
        map((neighborhoodDto: NeighborhoodDto) =>
          mapNeighborhood(neighborhoodDto),
        ),
      );
  }

  public getNeighborhoods(
    queryParams: {
      page?: number;
      size?: number;
      withWorker?: string;
      withoutWorker?: string;
    } = {},
  ): Observable<{
    neighborhoods: Neighborhood[];
    totalPages: number;
    currentPage: number;
  }> {
    let neighborhoodsUrl: string = this.linkService.getLink(
      LinkKey.NEIGHBORHOODS,
    );

    let params = new HttpParams();

    if (queryParams.page !== undefined)
      params = params.set('page', queryParams.page.toString());
    if (queryParams.size !== undefined)
      params = params.set('size', queryParams.size.toString());
    if (queryParams.withWorker)
      params = params.set('withWorker', queryParams.withWorker);
    if (queryParams.withoutWorker)
      params = params.set('withoutWorker', queryParams.withoutWorker);

    // This line is impl. specific and can be changed if it should return base channels like SuperAdmin and Rejected too.
    // therefore it is here so that getNeighborhoods is only for neighbor usage cases.
    params = params.set('isBase', false);

    return this.http
      .get<NeighborhoodDto[]>(neighborhoodsUrl, { params, observe: 'response' })
      .pipe(
        map(response => {
          const neighborhoodsDto: NeighborhoodDto[] = response.body || [];
          const pagination = parseLinkHeader(response.headers.get('Link'));

          const neighborhoods = neighborhoodsDto.map(mapNeighborhood);
          return {
            neighborhoods,
            totalPages: pagination.totalPages,
            currentPage: pagination.currentPage,
          };
        }),
      );
  }
}

export function mapNeighborhood(
  neighborhoodDto: NeighborhoodDto,
): Neighborhood {
  return {
    name: neighborhoodDto.name,
    self: neighborhoodDto._links.self,
  };
}
