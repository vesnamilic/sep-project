import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavigationComponent } from './navigation/navigation.component';
import { MagazinesComponent } from './magazines/magazines.component';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';
import {ShoppingCartModule} from 'ng-shopping-cart'; // <-- Import the module class
import { MDBBootstrapModule } from 'angular-bootstrap-md';
import { ErrorComponent } from './error/error.component';
import { CancelComponent } from './cancel/cancel.component';
import { SuccessComponent } from './success/success.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { AngularFontAwesomeModule } from 'angular-font-awesome';

@NgModule({
  declarations: [
    AppComponent,
    NavigationComponent,
    MagazinesComponent,
    ErrorComponent,
    CancelComponent,
    SuccessComponent,
    NotFoundComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    HttpModule,
    AngularFontAwesomeModule,
    MDBBootstrapModule.forRoot()
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
