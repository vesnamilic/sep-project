import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';
import { RegistrationComponent } from './registration/registration.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { ChoosePaymentMethodComponent } from './choose-payment-method/choose-payment-method.component';
import { SuccessComponent } from './success/success.component';
import { CancelComponent } from './cancel/cancel.component';
import { ErrorComponent } from './error/error.component';


const routes: Routes = [
  {
    path: 'registration',
    component: RegistrationComponent
  },
  {
    path: '',
    component: HomePageComponent
  },
  {
    path: 'payment/:id',
    component: ChoosePaymentMethodComponent
  },
  {
    path: 'success',
    component: SuccessComponent
  },
  {
    path: 'cancel',
    component: CancelComponent
  },
  {
    path: 'error',
    component: ErrorComponent
  }
  ,
  {
    path: '**',
    component: NotFoundComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(
    routes,
    {useHash: true}
    )],

  exports: [RouterModule]
})
export class AppRoutingModule { }
