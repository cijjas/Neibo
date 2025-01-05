import {
  Component,
  OnInit,
  ElementRef,
  ViewChild,
} from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HateoasLinksService } from '@core/index';
import { Tag, TagService } from '@shared/index';

@Component({
  selector: 'app-tags-filter-widget',
  templateUrl: './tags-filter-widget.component.html',
})
export class TagsFilterWidgetComponent implements OnInit {
  // Pagination state
  currentPage: number = 1;
  totalPages: number = 1;
  pageSize: number = 20; // Show 20 tags per page

  // Tags data
  tagList: Array<{ name: string; self: string }> = [];

  // Selected (applied) tags
  appliedTags: Array<{ name: string; self: string }> = []; // Store full tag objects

  constructor(
    private tagService: TagService,
    private linkService: HateoasLinksService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    // Read the 'tagsCurrent' query param for pagination
    this.route.queryParams.subscribe((params) => {
      this.currentPage = +params['tagsCurrent'] || 1;
      this.loadTagsFromApi();
      this.initializeAppliedTags();
    });
  }

  // Fetch tags for the current page
  loadTagsFromApi(): void {
    const tagsUrl = this.linkService.getLink('neighborhood:tags');
    const queryParams = {
      page: this.currentPage,
      size: this.pageSize,
    };

    this.tagService.getTags(tagsUrl, queryParams).subscribe({
      next: ({ tags, totalPages }: { tags: Tag[]; totalPages: number }) => {
        this.tagList = tags.map((tag) => ({
          name: tag.name,
          self: tag.self,
        }));
        this.totalPages = totalPages || 1;
      },
      error: (err: any) => {
        console.error('Failed to load tags:', err);
      },
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

  addTagToApply(tag: { name: string; self: string }): void {
    // Only add if it's not already selected
    if (!this.appliedTags.find((t) => t.self === tag.self)) {
      this.appliedTags.push(tag);
    }
  }

  removeTag(tag: { name: string; self: string }): void {
    this.appliedTags = this.appliedTags.filter((t) => t.self !== tag.self);
  }

  clearAllTags(): void {
    this.appliedTags = [];
    this.router.navigate([], {
      queryParams: { tags: null },
      queryParamsHandling: 'merge',
    });
  }

  applyTagsAsFilter(): void {
    if (this.appliedTags.length === 0) {
      console.warn('No tags selected.');
      return;
    }

    const queryParams = this.appliedTags.map(tag => ({ withTag: tag.self }));

    const flattenedQueryParams = queryParams.reduce((params, param) => {
      Object.keys(param).forEach(key => {
        if (!params[key]) {
          params[key] = [];
        }
        params[key].push(param[key]);
      });
      return params;
    }, {});

    this.router.navigate([], {
      queryParams: flattenedQueryParams,
      queryParamsHandling: 'merge',
    });
  }

  initializeAppliedTags(): void {
    const appliedTagsDiv = document.getElementById('applied-tags');
    if (!appliedTagsDiv) return;

    const tagsString = appliedTagsDiv.getAttribute('data-tags');
    if (tagsString) {
      try {
        const parsed = JSON.parse(tagsString);
        if (Array.isArray(parsed)) {
          this.appliedTags = parsed.map((tag: any) => ({
            name: tag.name,
            self: tag.self,
          }));
        }
      } catch (e) {
        console.error('Error parsing applied tags:', e);
      }
    }
  }
}