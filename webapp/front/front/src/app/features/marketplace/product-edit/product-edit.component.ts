import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { DepartmentService, ProductService, Department, Product } from '@shared/index';
import { ImageService, ToastService, HateoasLinksService } from '@core/index';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, map, switchMap } from 'rxjs';

@Component({
  selector: 'app-product-edit',
  templateUrl: './product-edit.component.html',
})
export class ProductEditComponent implements OnInit {
  darkMode = false; // Could be set based on user preferences
  channel: string = 'Edit';
  departmentList: Department[] = [];
  product: Product | null = null;

  // For demonstration, we assume these are populated after loading product data
  selectedDepartmentSelf: string | null = null;
  selectedQuantity: number = 1;

  quantities: number[] = Array.from({ length: 100 }, (_, i) => i + 1);

  listingForm = this.fb.group({
    title: ['', Validators.required],
    price: ['', [Validators.required, Validators.pattern(/^\$?\d+(,\d{3})*(\.\d{2})?$/)]],
    description: ['', Validators.required],
    departmentId: ['', Validators.required],
    used: [false, Validators.required],
    quantity: [1, Validators.required]
  });

  formErrors: string | null = null;
  images: { file: File; preview: string }[] = [];

  showImageUpload: boolean = true; // Toggle if needed

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private productService: ProductService,
    private departmentService: DepartmentService,
    private imageService: ImageService,
    private toastService: ToastService,
    private linkService: HateoasLinksService
  ) { }

  ngOnInit(): void {
    this.departmentService.getDepartments().subscribe({
      next: (departments: Department[]) => {
        this.departmentList = departments;
      },
      error: (err: any) => console.error(err)
    });

    const productUrl = this.route.snapshot.paramMap.get('id')!;
    if (productUrl) {
      this.productService.getProduct(productUrl).subscribe({
        next: (prod) => {
          this.product = prod;
          this.populateForm(prod);
        },
        error: (err) => console.error(err)
      });
    }
  }

  populateForm(prod: Product): void {
    // Set form values
    this.listingForm.patchValue({
      title: prod.name,
      price: '$' + Number(prod.price).toFixed(2),
      description: prod.description,
      departmentId: prod.department.self,
      used: prod.used,
      quantity: prod.stock
    });

    // Prefill department
    this.selectedDepartmentSelf = prod.department.self;

    // Prefill quantity
    this.selectedQuantity = prod.stock;

    // Prefill images if URLs exist
    this.images = [];

    const imageUrls = [prod.firstImage, prod.secondImage, prod.thirdImage].filter(url => url);

    imageUrls.forEach(url => {
      this.images.push({ file: null as unknown as File, preview: url });
    });
  }


  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;

    // Calculate the number of remaining slots for images
    const remainingSlots = 3 - this.images.length;

    // Only allow uploading up to the remaining number of slots
    const filesToAdd = Array.from(input.files).slice(0, remainingSlots);

    for (let i = 0; i < filesToAdd.length; i++) {
      const file = filesToAdd[i];
      if (!file.type.startsWith('image/')) continue;

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.images.push({ file, preview: e.target.result });
        this.clearImageError(); // Clear error if images are selected
      };
      reader.readAsDataURL(file);
    }

    // Clear the file input to allow selecting the same file again if needed
    input.value = '';
  }



  triggerImageInput(el: HTMLInputElement): void {
    el.click();
  }

  removeImage(index: number): void {
    this.images.splice(index, 1);
    this.clearImageError(); // Clear error if there are still images left
  }


  clearImageError(): void {
    if (this.images.length > 0 && this.formErrors === 'Please upload at least one image for the product.') {
      this.formErrors = null;
    }
  }

  formatCurrency(blur?: boolean): void {
    let val = this.listingForm.get('price')?.value || '';
    if (!val) return;

    // Remove all non-digit and non-dot characters except leading $
    val = val.replace(/[^0-9.]/g, '');

    let floatVal = parseFloat(val);
    if (isNaN(floatVal)) {
      floatVal = 0.00;
    }

    let formatted = '$' + floatVal.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    this.listingForm.patchValue({ price: formatted }, { emitEvent: false });
  }

  onSubmit(): void {
    if (this.listingForm.invalid) {
      this.formErrors = 'Please fill in all required fields correctly.';
      return;
    }

    if (!this.product) {
      this.formErrors = 'No product loaded.';
      return;
    }

    // Check if at least one image exists
    if (this.images.length === 0) {
      this.formErrors = 'Please upload at least one image for the product.';
      return;
    }

    // Extract form values
    const rawValue = this.listingForm.value;
    const userSelf = this.linkService.getLink('user:self');

    const productData: any = {
      name: rawValue.title,
      description: rawValue.description,
      price: rawValue.price.replace('$', ''), // Remove the dollar sign before sending
      used: rawValue.used,
      department: rawValue.departmentId,
      remainingUnits: rawValue.quantity,
      user: userSelf,
      images: [] // Collect image URLs here
    };

    // Separate new images (files) from preloaded images (URLs)
    const newImageFiles = this.images.filter(img => img.file);
    const existingImageUrls = this.images.filter(img => !img.file).map(img => img.preview);

    const imageUploadObservables = newImageFiles.map(img => this.imageService.createImage(img.file));

    combineLatest(imageUploadObservables)
      .pipe(
        map(newImageUrls => {
          productData.images = [...existingImageUrls, ...newImageUrls.filter(url => url !== null)];
          return productData;
        }),
        switchMap(updatedProductData =>
          this.productService.updateProduct(this.product!.self, updatedProductData)
        )
      )
      .subscribe({
        next: () => {
          this.toastService.showToast('Listing updated successfully', 'success');
          this.router.navigate(['/marketplace/products', this.product!.self]);
        },
        error: err => {
          this.formErrors = 'There was a problem updating the listing.';
          console.error(err);
        }
      });
  }





  goBack(): void {
    // Navigate back one step in the history or to a known route
    this.router.navigate(['/marketplace/products', this.product?.self]);
  }
}
