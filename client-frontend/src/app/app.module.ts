import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomePageComponent } from './home-page/home-page.component';
import { RegistrationComponent } from './registration/registration.component';
import { NavigationComponent } from './navigation/navigation.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule} from '@angular/common/http';
import { NotFoundComponent } from './not-found/not-found.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { PaymentMethodsComponent } from './form/form.component';
import { MatStepperModule } from '@angular/material/stepper';
import { ChoosePaymentMethodComponent } from './choose-payment-method/choose-payment-method.component';
import { SuccessComponent } from './success/success.component';
import { CancelComponent } from './cancel/cancel.component';
import { ErrorComponent } from './error/error.component';

@NgModule({
   declarations: [
      AppComponent,
      HomePageComponent,
      RegistrationComponent,
      NavigationComponent,
      NotFoundComponent,
      ChoosePaymentMethodComponent,
      PaymentMethodsComponent,
      SuccessComponent,
      CancelComponent,
      ErrorComponent
   ],
   imports: [
      BrowserModule,
      AppRoutingModule,
      ReactiveFormsModule,
      HttpClientModule,
      MatStepperModule,
      BrowserAnimationsModule
   ],
   providers: [],
   bootstrap: [
      AppComponent
   ]

})


export class AppModule { }
