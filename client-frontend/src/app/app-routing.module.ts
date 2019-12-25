import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';
import { RegistrationComponent } from './registration/registration.component';
import { NotFoundComponent } from './not-found/not-found.component';
<<<<<<< HEAD
import { PaymentMethodsComponent } from './form/form.component';
=======
import { ChoosePaymentMethodComponent } from './choose-payment-method/choose-payment-method.component';
import { SuccessComponent } from './success/success.component';
import { CancelComponent } from './cancel/cancel.component';
>>>>>>> 581f5d2f99e7278b3a1a97d6e1cc34d627380999


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
<<<<<<< HEAD
    path: 'paymentmethods',
    component: PaymentMethodsComponent
  },
  {
=======
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
  }
  ,
  {
>>>>>>> 581f5d2f99e7278b3a1a97d6e1cc34d627380999
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
