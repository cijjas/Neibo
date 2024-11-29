import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class ApiService {
    private links: any = {};

    // Set links dynamically from API responses
    setLinks(newLinks: any): void {
        this.links = { ...this.links, ...newLinks };
    }

    // Get a specific link
    getLink(key: string): string | null {
        return this.links[key] || null;
    }
}

// EXAMPLE USAGE
// import { Component, OnInit } from '@angular/core';
// import { ApiService } from '../services/api.service';

// @Component({
//   selector: 'app-feed',
//   templateUrl: './feed.component.html',
// })
// export class FeedComponent implements OnInit {
//   posts: any[] = [];

//   constructor(private apiService: ApiService) {}

//   ngOnInit(): void {
//     this.apiService.getData('/feed').subscribe((response: any) => {
//       this.posts = response.data; // Set posts data
//       this.apiService.setLinks(response.links); // Store HATEOAS links
//     });
//   }

//   getHotLink(): string | null {
//     return this.apiService.getLink('hot'); // Dynamically fetch "hot" link
//   }
// }