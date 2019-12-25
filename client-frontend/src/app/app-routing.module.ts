import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomePageComponent } from './home-page/home-page.component';
import { RegistrationComponent } from './registration/registration.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { PaymentMethodsComponent } from './form/form.component';


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
    path: 'paymentmethods',
    component: PaymentMethodsComponent
  },
  {
    path: '**',
    component: NotFoundComponent
  },
];

@NgModule({
  imports: [RouterModule.forRoot(
    routes,
    {useHash: true}
    )],

  exports: [RouterModule]
})
export class AppRoutingModule { }
