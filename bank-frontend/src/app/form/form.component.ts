import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { FormService } from './form.service';
import { Router } from '@angular/router';
import { PayModel } from '../model/payModel';


@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent implements OnInit {

  payForm: FormGroup;

  constructor(private formService: FormService,
    private router: Router) { }

  ngOnInit() {
    this.payForm = new FormGroup(
      {
        name: new FormControl('', Validators.required),
        lastName: new FormControl('', Validators.required),
        cvv: new FormControl('', Validators.required),
        pan: new FormControl('', Validators.required),
        month: new FormControl('', Validators.required),
        year: new FormControl('', Validators.required)
      }
    );
  }

  submitForm() {
    console.log(this.payForm.value);
    const payInfo: PayModel = {
      name: this.payForm.value.name,
      lastName: this.payForm.value.lastName,
      year: this.payForm.value.year,
      month: this.payForm.value.month,
      cvv: this.payForm.value.cvv,
      pan: this.payForm.value.pan
    };
    console.log(payInfo);
    this.formService.pay(payInfo).subscribe(
      data => {
        alert('Payed successfully');
        this.router.navigateByUrl('/');
      },
      error => {
        alert('An error ocurred.');
      }
    );

  }
}
