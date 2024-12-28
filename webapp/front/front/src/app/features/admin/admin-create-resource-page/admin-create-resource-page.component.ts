import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-admin-create-resource-page',
  templateUrl: './admin-create-resource-page.component.html',
})
export class AdminCreateResourcePageComponent implements OnInit {

  resourceForm!: FormGroup;
  previewSrc: string | null = null;
  dragActive: boolean = false;

  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {
    this.resourceForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      imageFile: [null]  // or separate logic to handle the File object
    });
  }

  get titleControl() {
    return this.resourceForm.get('title');
  }
  get descControl() {
    return this.resourceForm.get('description');
  }

  onSubmit() {
    if (this.resourceForm.invalid) {
      this.resourceForm.markAllAsTouched();
      return;
    }

    console.log('Creating resource with:', this.resourceForm.value);
    // e.g. this.resourceService.createResource(this.resourceForm.value, this.file)
  }

  previewImage(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previewSrc = e.target?.result as string;
      };
      reader.readAsDataURL(file);

      // Store file in form if needed
      this.resourceForm.patchValue({ imageFile: file });
    } else {
      this.previewSrc = null;
    }
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.dragActive = true;
  }

  onDragLeave() {
    this.dragActive = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.dragActive = false;

    if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
      const file = event.dataTransfer.files[0];
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previewSrc = e.target?.result as string;
      };
      reader.readAsDataURL(file);

      this.resourceForm.patchValue({ imageFile: file });
    }
  }
}
