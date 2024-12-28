import { Component, OnInit } from '@angular/core';
import { TagService, Tag } from '@shared/index'; // Adjust the path as needed

@Component({
  selector: 'app-tags-filter-widget',
  templateUrl: './tags-filter-widget.component.html',
})
export class TagsFilterWidgetComponent implements OnInit {
  tagList: Tag[] = [];
  appliedTags: string[] = [];
  currentTag: string = '';
  placeholderText: string = 'Enter a tag';

  constructor(private tagService: TagService) { }

  ngOnInit(): void {
    this.loadTags();
    this.initializeAppliedTags();
  }

  loadTags(): void {
    this.tagService.getTags('/api/tags').subscribe((tags) => {
      this.tagList = tags;
    });
  }

  addTagToApply(tag: string): void {
    if (!this.appliedTags.includes(tag)) {
      this.appliedTags.push(tag);
    }
  }

  clearAllTags(): void {
    this.appliedTags = [];
  }

  applyTagsAsFilter(): void {
    const tagsString = this.appliedTags.join(',');
    // Add logic to send the tagsString to your backend or handle filtering
    console.log('Applying tags:', tagsString);
  }

  filterTags(query: string): void {
    this.tagList = this.tagList.filter((tag) =>
      tag.name.toLowerCase().includes(query.toLowerCase())
    );
  }

  initializeAppliedTags(): void {
    const appliedTagsDiv = document.getElementById('applied-tags');
    const tagsString = appliedTagsDiv?.getAttribute('data-tags');

    if (tagsString) {
      this.appliedTags = JSON.parse(tagsString);
    }
  }
}
