import { HttpClient, HttpParams } from '@angular/common/http'
import { Channel } from './channel'
import { Observable } from 'rxjs'
import { Injectable } from '@angular/core'
import { environment } from '../environments/environment'

@Injectable({providedIn: 'root'})
export class ChannelService {
    private apiServerUrl = environment.apiBaseUrl

    constructor(private http: HttpClient) { }

    public getChannel(channelId : number, neighborhoodId : number): Observable<Channel> {
        return this.http.get<Channel>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/channels/${channelId}`)
    }

    public getChannels(neighborhoodId : number): Observable<Channel[]> {
        return this.http.get<Channel[]>(`${this.apiServerUrl}/neighborhoods/${neighborhoodId}/channels`)
    }

}