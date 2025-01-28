// infinite-scroll-select.component.ts
import {
  Component,
  Input,
  OnInit,
  OnDestroy,
  AfterViewInit,
  ViewChild,
  ElementRef,
  forwardRef,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { Subscription, Observable } from 'rxjs';

interface FetchResponse<T> {
  items: T[];
  currentPage: number;
  totalPages: number;
}

@Component({
  selector: 'app-infinite-scroll-select',
  templateUrl: './infinite-scroll-select.component.html',
  styleUrls: ['./infinite-scroll-select.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InfiniteScrollSelectComponent),
      multi: true,
    },
  ],
})
export class InfiniteScrollSelectComponent<T>
  implements OnInit, AfterViewInit, OnDestroy, ControlValueAccessor
{
  /**
   * Theme name for styling.
   */
  @Input() theme: string = 'default';
  @Input() height: string = '300px'; // Default height
  /**
   * Enables multiple selection if true.
   */
  @Input() multiple: boolean = false;

  /**
   * Function to fetch data. Should return an Observable of FetchResponse.
   */
  @Input() fetchData!: (
    page: number,
    size: number
  ) => Observable<FetchResponse<T>>;

  /**
   * Function to display an item.
   */
  @Input() displayFn!: (item: T) => string;

  /**
   * Unique key to identify items. Defaults to 'id'.
   */
  @Input() uniqueKey: keyof T = 'id' as keyof T;

  @ViewChild('scrollContainer') scrollContainer!: ElementRef;

  items: T[] = [];
  selectedItems: T[] = [];
  selectedItem: T | null = null;

  private currentPage = 1;
  private totalPages = 1;
  private isLoading = false;
  private subscription?: Subscription;

  // ControlValueAccessor callbacks
  private onChange: (value: any) => void = () => {};
  private onTouched: () => void = () => {};

  constructor() {}

  ngOnInit(): void {
    this.loadItems();
  }

  ngAfterViewInit(): void {
    if (this.scrollContainer) {
      this.scrollContainer.nativeElement.addEventListener(
        'scroll',
        this.onScroll.bind(this)
      );
    }
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    if (this.scrollContainer) {
      this.scrollContainer.nativeElement.removeEventListener(
        'scroll',
        this.onScroll.bind(this)
      );
    }
  }

  /**
   * Load items using the provided fetchData function.
   */
  private loadItems(): void {
    if (this.isLoading || this.currentPage > this.totalPages) {
      return;
    }

    this.isLoading = true;
    this.subscription = this.fetchData(this.currentPage, 20).subscribe({
      next: (response) => {
        this.items = [...this.items, ...response.items];
        this.currentPage = response.currentPage + 1;
        this.totalPages = response.totalPages;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching data:', error);
        // Optionally handle errors (e.g., show a notification)
        this.isLoading = false;
      },
    });
  }

  /**
   * Handle scroll events to implement infinite scrolling.
   */
  onScroll(event: any): void {
    const element = event.target;
    if (element.scrollHeight - element.scrollTop - element.clientHeight < 50) {
      this.loadItems();
    }
  }

  /**
   * Handle item selection.
   */
  onSelectItem(item: T): void {
    const key = this.uniqueKey;
    if (this.multiple) {
      const index = this.selectedItems.findIndex(
        (selected) => selected[key] === item[key]
      );
      if (index !== -1) {
        this.selectedItems.splice(index, 1);
      } else {
        this.selectedItems.push(item);
      }
      this.onChange([...this.selectedItems]);
    } else {
      if (this.selectedItem && this.selectedItem[key] === item[key]) {
        this.selectedItem = null;
        this.onChange(null);
      } else {
        this.selectedItem = item;
        this.onChange(this.selectedItem);
      }
    }
    this.onTouched();
  }

  /**
   * Check if an item is selected.
   */
  isSelected(item: T): boolean {
    const key = this.uniqueKey;
    if (this.multiple) {
      return this.selectedItems.some((selected) => selected[key] === item[key]);
    } else {
      return this.selectedItem?.[key] === item[key];
    }
  }

  // ControlValueAccessor Methods

  writeValue(value: any): void {
    if (this.multiple) {
      this.selectedItems = Array.isArray(value) ? [...value] : [];
    } else {
      this.selectedItem = value || null;
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    // Optionally implement disabled state
  }
}
