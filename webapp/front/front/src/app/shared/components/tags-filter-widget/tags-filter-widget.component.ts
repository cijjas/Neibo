import {
  Component,
  OnInit,
  AfterViewInit,
  ElementRef,
  ViewChild,
} from '@angular/core';
import { Router } from '@angular/router'; // Import Angular Router
import { HateoasLinksService } from '@core/index';
import { TagService } from '@shared/index'; // Adjust import path based on your project structure

@Component({
  selector: 'app-tags-filter-widget',
  templateUrl: './tags-filter-widget.component.html',
})
export class TagsFilterWidgetComponent implements OnInit, AfterViewInit {
  @ViewChild('tagInput2', { static: true }) tagInput2Ref!: ElementRef;

  placeholderText: string = 'Enter a tag';
  appliedTags: string[] = [];
  tagList: Array<{ tag: string }> = [];
  private tagInput2: any;

  constructor(
    private tagService: TagService,
    private linkService: HateoasLinksService,
    private router: Router // Inject Angular Router
  ) {}

  ngOnInit(): void {
    this.loadTagsFromApi(this.linkService.getLink('neighborhood:tags')); // Replace with your actual API endpoint
  }

  ngAfterViewInit(): void {
    // Initialize the TagsInput plugin after the DOM is ready
    this.tagInput2 = new (window as any).TagsInput({
      selector: 'tag-input2',
      wrapperClass: 'tags-input-wrapper',
      duplicate: false,
      max: 5,
    });

    // Initialize applied tags if any
    this.initializeAppliedTags();
  }

  // Fetch tags from the API
  loadTagsFromApi(tagsUrl: string): void {
    const queryParams = {
      page: 1,
      size: 20,
    };
    this.tagService.getTags(tagsUrl, queryParams).subscribe({
      next: (tags: any[]) => {
        console.log(tags);
        // Map the API response to extract the `name` property for each tag
        this.tagList = tags.map((tag) => ({ tag: tag.name }));
      },
      error: (err) => {
        console.error('Failed to load tags:', err);
      },
    });
  }

  addTagToApply(tagText: string): void {
    this.tagInput2?.addTag(tagText);
  }

  clearAllTags(): void {
    this.tagInput2?.clearAllTags();

    // Also remove the 'tags' param from the URL
    this.router.navigate([], {
      queryParams: { tags: null },
      queryParamsHandling: 'merge',
    });
  }

  applyTagsAsFilter(): void {
    const tagsArray = this.tagInput2?.arr || [];
    if (tagsArray.length === 0) {
      console.warn('No tags selected.');
      return;
    }

    // Create query parameters
    const queryParams = { tags: tagsArray.join(',') };

    // Navigate with query parameters
    this.router.navigate([], {
      queryParams,
      queryParamsHandling: 'merge', // Merge with existing query parameters
    });

    console.log('Tags applied:', queryParams);
  }

  initializeAppliedTags(): void {
    const appliedTagsDiv = document.getElementById('applied-tags');
    const tagsString = appliedTagsDiv?.getAttribute('data-tags');
    if (tagsString) {
      this.appliedTags = JSON.parse(tagsString);
      this.appliedTags.forEach((tagText) => {
        this.tagInput2?.addTag(tagText);
      });
    }
  }
}
