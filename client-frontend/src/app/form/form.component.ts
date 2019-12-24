import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormService } from './form.service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Field } from '../model/field';
import { PaymentMethod } from '../model/paymentmethod';

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent implements OnInit {

  constructor(private formService: FormService,
              private router: Router) { }


  paymentMethods: PaymentMethod[] = [];
  fields: Field[] = [];


  form: FormGroup;

  ngOnInit() {

    this.getPaymentMethods();

    this.getFields();

  }

  getFields() {
    this.formService.getFields('paypal').subscribe(
      data => {
        for (let field of data) {
          this.fields.push(field);
        }

        this.form = this.createFormGroup(this.fields);
      },
      error => {
        console.log('An error ocurred.');
      }
    );
  }

  getPaymentMethods() {
    this.formService.getPaymentMethods().subscribe(
      data => {
        for (let pm of data) {
          this.paymentMethods.push(pm);
        }
      },
      error => {
        console.log('An error ocurred.');
      }
    );
  }

  createFormGroup(fields: Field[] ) {
    const group: any = {};

    fields.forEach(field => {
      group[field.name] = field.required ? new FormControl('', Validators.required) : new FormControl('');
    });

    return new FormGroup(group);
  }

  submitForm() {
    // do something
  }

}
