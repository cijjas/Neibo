import { HttpClientModule } from "@angular/common/http"
import { AppComponent } from "./app.component"
import { BrowserModule } from "@angular/platform-browser"
import { NgModule } from "@angular/core"
import { AmenityService } from "./shared/services/amenity.service"
import { FormsModule } from '@angular/forms'

@NgModule({
    declarations: [
        AppComponent
    ],
    imports: [
        BrowserModule,
        HttpClientModule,
        FormsModule
    ],
    providers: [AmenityService],
    bootstrap: [AppComponent]
})
export class AppModule {}
