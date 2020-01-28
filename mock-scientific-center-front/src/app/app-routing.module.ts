import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MagazinesComponent } from './magazines/magazines.component';
import { CancelComponent } from './cancel/cancel.component';
import { ErrorComponent } from './error/error.component';
import { SuccessComponent } from './success/success.component';
import { NotFoundComponent } from './not-found/not-found.component';


const routes: Routes = [
  {
    path: '',
    component: MagazinesComponent
  },
  {
    path: 'cancel',
    component: CancelComponent
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
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
