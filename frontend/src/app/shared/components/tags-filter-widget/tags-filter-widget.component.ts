import { Component, OnInit, Input } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HateoasLinksService } from '@core/index';
import {
  TagService,
  ProfessionService,
  Tag,
  Profession,
  LinkKey,
} from '@shared/index';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-tags-filter-widget',
  templateUrl: './tags-filter-widget.component.html',
})
export class TagsFilterWidgetComponent implements OnInit {
  @Input() mode: 'tags' | 'professions' = 'tags'; // 'tags' or 'professions'

  // Pagination state (only used for tags)
  currentPage: number = 1;
  totalPages: number = 1;
  pageSize: number = 20;

  // Data for tags or professions
  itemList: Array<{ name: string; self: string }> = [];

  // Selected (applied) items
  appliedItems: Array<{ name: string; self: string }> = [];

  constructor(
    private tagService: TagService,
    private professionService: ProfessionService,
    private linkService: HateoasLinksService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (this.mode === 'tags') {
        this.currentPage = +params['tagsCurrent'] || 1;

        // Handle tags from query params
        const tagUrls = params['withTag'];

        if (tagUrls) {
          // If there's a 'withTag' param, fetch those tags
          const tagUrlArray = Array.isArray(tagUrls) ? tagUrls : [tagUrls];
          this.fetchAndApplyTags(tagUrlArray);
        } else {
          // If there's no 'withTag' param, clear the applied items
          this.appliedItems = [];
        }

        this.loadTagsFromApi();
      } else if (this.mode === 'professions') {
        // Handle professions from query params
        const professionUrls = params['withProfession'];

        if (professionUrls) {
          const professionUrlArray = Array.isArray(professionUrls)
            ? professionUrls
            : [professionUrls];
          this.fetchAndApplyProfessions(professionUrlArray);
        } else {
          // If there's no 'withProfession' param, clear the applied items
          this.appliedItems = [];
        }

        this.loadProfessions();
      }
    });
  }

  private fetchAndApplyTags(tagUrls: string[]): void {
    const tagObservables = tagUrls.map(url => this.tagService.getTag(url));
    forkJoin(tagObservables).subscribe({
      next: tags => {
        this.appliedItems = tags.map(tag => ({
          name: tag.name,
          self: tag.self,
        }));
      },
      error: err => console.error('Failed to fetch tags:', err),
    });
  }

  private fetchAndApplyProfessions(professionUrls: string[]): void {
    const professionObservables = professionUrls.map(url =>
      this.professionService.getProfession(url),
    );

    forkJoin(professionObservables).subscribe({
      next: professions => {
        this.appliedItems = professions.map(profession => ({
          name: profession.displayName,
          self: profession.self,
        }));
      },
      error: err => console.error('Failed to fetch professions:', err),
    });
  }

  private loadTagsFromApi(): void {
    const tagsUrl = this.linkService.getLink(LinkKey.NEIGHBORHOOD_TAGS);
    const queryParams = {
      page: this.currentPage,
      size: this.pageSize,
    };

    this.tagService.getTags(tagsUrl, queryParams).subscribe({
      next: ({ tags, totalPages }: { tags: Tag[]; totalPages: number }) => {
        this.itemList = tags.map(tag => ({
          name: tag.name,
          self: tag.self,
        }));
        this.totalPages = totalPages || 1;
      },
      error: (err: any) => console.error('Failed to load tags:', err),
    });
  }

  private loadProfessions(): void {
    this.professionService.getProfessions().subscribe({
      next: (professions: Profession[]) => {
        this.itemList = professions.map(profession => ({
          displayName: profession.displayName,
          name: profession.name,
          self: profession.self,
        }));
      },
      error: (err: any) => console.error('Failed to load professions:', err),
    });
  }

  addItem(item: { name: string; self: string }): void {
    // Only add if it's not already in the appliedItems array
    if (!this.appliedItems.find(t => t.self === item.self)) {
      this.appliedItems.push(item);

      const queryParams = { ...this.route.snapshot.queryParams };
      const key = this.mode === 'tags' ? 'withTag' : 'withProfession';

      if (!queryParams[key]) {
        queryParams[key] = [];
      }

      // Ensure queryParams[key] is an array, then push the new self link
      queryParams[key] = Array.isArray(queryParams[key])
        ? [...queryParams[key], item.self]
        : [queryParams[key], item.self];

      this.router.navigate([], {
        queryParams,
        queryParamsHandling: 'merge',
      });
    }
  }

  removeItem(item: { name: string; self: string }): void {
    this.appliedItems = this.appliedItems.filter(t => t.self !== item.self);

    const queryParams = { ...this.route.snapshot.queryParams };
    const key = this.mode === 'tags' ? 'withTag' : 'withProfession';

    if (Array.isArray(queryParams[key])) {
      // Remove the item from the array
      queryParams[key] = queryParams[key].filter(
        (value: string) => value !== item.self,
      );
      // If array is now empty, remove the param altogether
      if (queryParams[key].length === 0) {
        delete queryParams[key];
      }
    } else if (queryParams[key] === item.self) {
      // Single string case
      delete queryParams[key];
    }

    this.router.navigate([], {
      queryParams,
      queryParamsHandling: '',
    });
  }

  // Pagination methods
  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.goToPage(this.currentPage + 1);
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.goToPage(this.currentPage - 1);
    }
  }

  goToPage(page: number): void {
    if (page < 1 || page > this.totalPages) return;

    this.currentPage = page;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { tagsCurrent: this.currentPage },
      queryParamsHandling: 'merge',
    });
  }
}
