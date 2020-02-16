import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { FormComponent } from './form/form.component';
import { FailedComponent } from './failed/failed.component';
import { ErrorComponent } from './error/error.component';
import { SuccessComponent } from './success/success.component';
import { NotFoundComponent } from './not-found/not-found.component';


const routes: Routes = [
  {
    path: '',
    component: FormComponent
  },
  {
    path: 'form/:token',
    component: FormComponent
  },
  {
    path: 'failed',
    component: FailedComponent
  },
  {
    path: 'error',
    component: ErrorComponent
  },
  {
    path: 'success',
    component: SuccessComponent
  },
  {
    path: '**',
    component: NotFoundComponent
  }

];

@NgModule({
  imports: [RouterModule.forRoot(
    routes
  )],

  exports: [RouterModule]
})

export class AppRoutingModule { }
