// pagination.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class PaginationService<T> {
    private itemsSubject = new BehaviorSubject<T[]>([]);
    items$ = this.itemsSubject.asObservable();

    private totalPagesSubject = new BehaviorSubject<number>(1);
    totalPages$ = this.totalPagesSubject.asObservable();

    private currentPageSubject = new BehaviorSubject<number>(1);
    currentPage$ = this.currentPageSubject.asObservable();

    private pageSizeSubject = new BehaviorSubject<number>(10);
    pageSize$ = this.pageSizeSubject.asObservable();

    constructor() { }

    setItems(items: T[]): void {
        this.itemsSubject.next(items);
    }

    setTotalPages(totalPages: number): void {
        this.totalPagesSubject.next(totalPages);
    }

    setCurrentPage(page: number): void {
        this.currentPageSubject.next(page);
    }

    setPageSize(size: number): void {
        this.pageSizeSubject.next(size);
    }

    // Implement data fetching logic here, or abstract it for the component to implement
}
