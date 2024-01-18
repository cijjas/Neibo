import { Component, OnInit } from '@angular/core'
import { Amenity } from './amenity'
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
  public editAmenity: Amenity = {} as Amenity
  public deleteAmenity: Amenity = {} as Amenity

  constructor(private amenityService: AmenityService){}

  ngOnInit() {
    this.getAmenities()
  }

  public getAmenities(): void {
    this.amenityService.getAmenities().subscribe(
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
    this.amenityService.addAmenity(addForm.value).subscribe(
      (response: Amenity) => {
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

  public onUpdateAmenity(amenity: Amenity): void {
    this.amenityService.updateAmenity(amenity).subscribe(
      (response: Amenity) => {
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
      this.amenityService.deleteAmenity(amenityId).subscribe(
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

  public onOpenModal(amenity: Amenity | null, mode: string): void {
    const container = document.getElementById('main-container')
    const button = document.createElement('button')
    button.type = 'button'
    button.style.display = 'none'
    button.setAttribute('data-toggle', 'modal')
    if (mode === 'add') {
      button.setAttribute('data-target', '#addAmenityModal')
    }
    if(amenity != null) {
      if (mode === 'edit') {
        this.editAmenity = amenity
        button.setAttribute('data-target', '#updateEmployeeModal')
      }
      if (mode === 'delete') {
        this.deleteAmenity = amenity
        button.setAttribute('data-target', '#deleteEmployeeModal')
      }
    }

    //creates button in 'main-container'
    container!.appendChild(button)
    button.click()
  }
}
