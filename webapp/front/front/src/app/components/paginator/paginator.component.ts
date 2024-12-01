import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-paginator',
  templateUrl: './paginator.component.html'
})
export class PaginatorComponent implements OnChanges {
  @Input() totalPages: number = 1;
  @Input() currentPage: number = 1;
  @Input() pageSize: number = 10;
  @Input() isMarketplace: boolean = false;

  @Output() pageChange: EventEmitter<number> = new EventEmitter<number>();
  @Output() pageSizeChange: EventEmitter<number> = new EventEmitter<number>();

  pageNumbers: number[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['totalPages'] || changes['currentPage']) {
      this.updatePageNumbers();
    }
  }
  private updatePageNumbers(): void {
    const maxVisiblePages = 5; // Adjust this number to reduce the number of visible buttons
    const pages: number[] = [];

    if (this.totalPages <= maxVisiblePages) {
      // Show all pages if the total number is within the limit
      for (let i = 1; i <= this.totalPages; i++) {
        pages.push(i);
      }
    } else {
      const startPage = Math.max(2, this.currentPage - 1);
      const endPage = Math.min(this.totalPages - 1, this.currentPage + 1);

      // Always show the first page
      pages.push(1);

      // Add ellipsis if needed
      if (startPage > 2) {
        pages.push(-1); // Use -1 to represent "..."
      }

      // Add the range of pages around the current page
      for (let i = startPage; i <= endPage; i++) {
        pages.push(i);
      }

      // Add ellipsis if needed
      if (endPage < this.totalPages - 1) {
        pages.push(-1); // Use -1 to represent "..."
      }

      // Always show the last page
      pages.push(this.totalPages);
    }

    this.pageNumbers = pages;
  }




  goToPage(pageNumber: number): void {
    if (pageNumber !== this.currentPage) {
      this.currentPage = pageNumber;
      this.pageChange.emit(this.currentPage);
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.pageChange.emit(this.currentPage);
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.pageChange.emit(this.currentPage);
    }
  }

  onPageSizeChange(event: Event): void {
    const newSize = parseInt((event.target as HTMLSelectElement).value, 10);
    this.pageSize = newSize;
    this.pageSizeChange.emit(this.pageSize);
  }
}
