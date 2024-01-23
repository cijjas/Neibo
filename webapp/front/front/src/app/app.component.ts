// app.component.ts

import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Amenity, AmenityForm } from './shared/models/amenity';
import { AmenityService } from './shared/services/amenity.service';
import { HttpErrorResponse } from '@angular/common/http';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'] // Corrected property name
})
export class AppComponent implements OnInit {
  title = 'neibo';
  public amenities: Amenity[] = [];
  public editAmenity: AmenityForm = {} as AmenityForm;
  public deleteAmenity: AmenityForm = {} as AmenityForm;

  constructor(private amenityService: AmenityService) {}

  ngOnInit() {
    this.getAmenities();
  }

  public getAmenities(): void {
    this.amenityService.getAmenities(1, 1, 10).subscribe(
      (response: Amenity[]) => {
        this.amenities = response;
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  public onAddAmenity(addForm: NgForm): void {
    document.getElementById('add-amenity-form')!.click();
    this.amenityService.addAmenity(addForm.value, 1).subscribe(
      (response: AmenityForm) => {
        console.log(response);
        this.getAmenities();
        addForm.reset();
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
        addForm.reset();
      }
    );
  }

  public onUpdateAmenity(updateForm: NgForm): void {
    this.amenityService.updateAmenity(updateForm.value, 1).subscribe(
      (response: AmenityForm) => {
        console.log(response);
        this.getAmenities();
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
      }
    );
  }

  public onDeleteAmenity(amenityId: number | undefined): void {
    if (amenityId !== undefined) {
      this.amenityService.deleteAmenity(amenityId, 1).subscribe(
        (response: void) => {
          console.log(response);
          this.getAmenities();
        },
        (error: HttpErrorResponse) => {
          alert(error.message);
        }
      );
    }
  }

  public onOpenModal(amenity: Amenity | null, mode: string): void {
    const container = document.getElementById('main-container');
    const button = document.createElement('button');
    button.type = 'button';
    button.style.display = 'none';
    button.setAttribute('data-toggle', 'modal');
  
    if (mode === 'add') {
      // For adding, use an empty AmenityForm
      this.editAmenity = {
        amenityId: 0, // Set to an appropriate default value or null
        name: '',
        description: '',
        selectedShifts: [],
        self: '',
      };
      button.setAttribute('data-target', '#updateAmenityModal');
    }
  
    if (amenity != null) {
      if (mode === 'edit') {
        // For editing, use AmenityForm and populate the selectedShifts property
        this.editAmenity = {
          amenityId: amenity.amenityId,
          name: amenity.name,
          description: amenity.description,
          selectedShifts: amenity.availability.map(avail => avail.shift), // Modify this according to your actual structure
          self: amenity.self,
        };
        button.setAttribute('data-target', '#updateAmenityModal');
      }
      if (mode === 'delete') {
        // For deletion, use AmenityForm as well
        this.deleteAmenity = {
          amenityId: amenity.amenityId,
          name: amenity.name,
          description: amenity.description,
          selectedShifts: amenity.availability.map(avail => avail.shift), // Modify this according to your actual structure
          self: amenity.self,
        };
        button.setAttribute('data-target', '#deleteAmenityModal');
      }
    }
  
    // Creates button in 'main-container'
    container!.appendChild(button);
    button.click();
  }

}
