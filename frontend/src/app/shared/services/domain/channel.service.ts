import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Channel, ChannelDto, parseLinkHeader } from '@shared/index';

@Injectable({ providedIn: 'root' })
export class ChannelService {
  constructor(private http: HttpClient) {}

  public getChannel(url: string): Observable<Channel> {
    return this.http
      .get<ChannelDto>(url)
      .pipe(map((channelDto: ChannelDto) => mapChannel(channelDto)));
  }

  public getChannels(
    url: string,
    queryParams: {
      page?: number;
      size?: number;
    } = {},
  ): Observable<{
    channels: Channel[];
    totalPages: number;
    currentPage: number;
  }> {
    let params = new HttpParams();

    if (queryParams.page !== undefined)
      params = params.set('page', queryParams.page.toString());
    if (queryParams.size !== undefined)
      params = params.set('size', queryParams.size.toString());

    return this.http
      .get<ChannelDto[]>(url, { params, observe: 'response' })
      .pipe(
        map(response => {
          const channelsDto: ChannelDto[] = response.body || [];
          const pagination = parseLinkHeader(response.headers.get('Link'));

          const channels = channelsDto.map(mapChannel);

          return {
            channels,
            totalPages: pagination.totalPages,
            currentPage: pagination.currentPage,
          };
        }),
      );
  }
}

export function mapChannel(channelDto: ChannelDto): Channel {
  return {
    name: channelDto.name,
    self: channelDto._links.self,
  };
}
