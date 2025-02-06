import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { LinkKey, Resource, ResourceDto, parseLinkHeader } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Injectable({ providedIn: 'root' })
export class ResourceService {
  constructor(
    private http: HttpClient,
    private linkService: HateoasLinksService,
  ) {}

  public getResource(url: string): Observable<Resource> {
    return this.http
      .get<ResourceDto>(url)
      .pipe(map((resourceDto: ResourceDto) => mapResource(resourceDto)));
  }

  public getResources(
    queryParams: {
      page?: number;
      size?: number;
    } = {},
  ): Observable<{
    resources: Resource[];
    totalPages: number;
    currentPage: number;
  }> {
    let resourcesUrl: string = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_RESOURCES,
    );

    let params = new HttpParams();

    if (queryParams.page !== undefined)
      params = params.set('page', queryParams.page.toString());
    if (queryParams.size !== undefined)
      params = params.set('size', queryParams.size.toString());

    return this.http
      .get<ResourceDto[]>(resourcesUrl, { params, observe: 'response' })
      .pipe(
        map(response => {
          const resourcesDto: ResourceDto[] = response.body || [];
          const pagination = parseLinkHeader(response.headers.get('Link'));

          const resources = resourcesDto.map(mapResource);

          return {
            resources,
            totalPages: pagination.totalPages,
            currentPage: pagination.currentPage,
          };
        }),
      );
  }

  public createResource(
    title: string,
    description: string,
    image: string,
  ): Observable<string | null> {
    const body: ResourceDto = {
      title: title,
      description: description,
      image: image,
    };

    let resourcesUrl: string = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_RESOURCES,
    );

    return this.http.post(resourcesUrl, body, { observe: 'response' }).pipe(
      map(response => {
        const locationHeader = response.headers.get('Location');
        if (locationHeader) {
          return locationHeader;
        } else {
          console.error('Location header not found:');
          return null;
        }
      }),
      catchError(error => {
        console.error('Error creating resource', title, error);
        return of(null);
      }),
    );
  }

  public deleteResource(resourceUrl: string): Observable<void> {
    return this.http.delete<void>(resourceUrl);
  }
}

export function mapResource(resourceDto: ResourceDto): Resource {
  return {
    title: resourceDto.title,
    description: resourceDto.description,
    image: resourceDto._links.resourceImage,
    self: resourceDto._links.self,
  };
}
