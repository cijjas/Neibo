import { Component, OnInit } from '@angular/core'
import { Amenity, AmenityForm } from './amenity'
import { AmenityService } from './amenity.service'
import { HttpErrorResponse } from '@angular/common/http'
import { NgForm } from '@angular/forms'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'neibo'
  public amenities: Amenity[] = []
  public editAmenity: AmenityForm = {} as AmenityForm
  public deleteAmenity: AmenityForm = {} as AmenityForm

  constructor(private amenityService: AmenityService){}

  ngOnInit() {
    this.getAmenities()
  }

  public getAmenities(): void {
    this.amenityService.getAmenities(1,1,10).subscribe(
      (response: Amenity[]) => {
        this.amenities = response
      },
      (error: HttpErrorResponse) => {
        alert(error.message)
      }
    )
  }

  public onAddAmenity(addForm: NgForm): void {
    document.getElementById('add-amenity-form')!.click()
    this.amenityService.addAmenity(addForm.value, 1).subscribe(
      (response: AmenityForm) => {
        console.log(response)
        this.getAmenities()
        addForm.reset()
      },
      (error: HttpErrorResponse) => {
        alert(error.message)
        addForm.reset()
      }
    )
  }

  public onUpdateAmenity(updateForm: NgForm): void {
    this.amenityService.updateAmenity(updateForm.value, 1).subscribe(
      (response: AmenityForm) => {
        console.log(response)
        this.getAmenities()
      },
      (error: HttpErrorResponse) => {
        alert(error.message)
      }
    )
  }

  public onDeleteAmenity(amenityId: number | undefined): void {
    if(amenityId != undefined) {
      this.amenityService.deleteAmenity(amenityId, 1).subscribe(
        (response: void) => {
          console.log(response)
          this.getAmenities()
        },
        (error: HttpErrorResponse) => {
          alert(error.message)
        }
      )
    }
  }

  public onOpenModal(amenityForm: AmenityForm | null, mode: string): void {
    const container = document.getElementById('main-container')
    const button = document.createElement('button')
    button.type = 'button'
    button.style.display = 'none'
    button.setAttribute('data-toggle', 'modal')
    if (mode === 'add') {
      button.setAttribute('data-target', '#addAmenityModal')
    }
    if(amenityForm != null) {
      if (mode === 'edit') {
        this.editAmenity = amenityForm
        button.setAttribute('data-target', '#updateEmployeeModal')
      }
      if (mode === 'delete') {
        this.deleteAmenity = amenityForm
        button.setAttribute('data-target', '#deleteEmployeeModal')
      }
    }

    //creates button in 'main-container'
    container!.appendChild(button)
    button.click()
  }
}