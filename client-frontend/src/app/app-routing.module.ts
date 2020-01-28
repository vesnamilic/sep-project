import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';
import { RegistrationComponent } from './registration/registration.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { PaymentMethodsComponent } from './form/form.component';
import { ChoosePaymentMethodComponent } from './choose-payment-method/choose-payment-method.component';
import { SuccessComponent } from './success/success.component';
import { CancelComponent } from './cancel/cancel.component';
import { ErrorComponent } from './error/error.component';
import { NonAuthGuardService } from './authentication/non-auth-guard.service';
import { AuthGuardService } from './authentication/auth-guard.service';
import { ChooseBillingPlanComponent } from './choose-billing-plan/choose-billing-plan.component';


const routes: Routes = [
  {
    path: 'registration',
    component: RegistrationComponent
    //canActivate: [NonAuthGuardService]
  },
  {
    path: '',
    component: HomePageComponent
  },
  {
    path: 'paymentmethods',
    component: PaymentMethodsComponent,
    canActivate: [AuthGuardService]
  },
  {
    path: 'payment/:id',
    component: ChoosePaymentMethodComponent
  },
  {
    path: 'subscription/:id',
    component: ChooseBillingPlanComponent
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
