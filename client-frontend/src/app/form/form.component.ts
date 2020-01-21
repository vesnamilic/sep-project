import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormService } from './form.service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Field } from '../model/field';
import { PaymentMethod } from '../model/paymentmethod';
import { MatStepper } from '@angular/material/stepper';

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class PaymentMethodsComponent implements OnInit {

  constructor(private formService: FormService,
              private router: Router) { }


  paymentMethods: PaymentMethod[] = [];
  fields: any = {};

  forms: any = {};

  ngOnInit() {
    this.createForms();

    console.log(this.fields);
    console.log(this.forms);
  }

  createForms() {
    // get all available payment methods
    this.formService.getPaymentMethods().subscribe(
      data => {
        for (let pm of data) {
          this.paymentMethods.push(pm);
        }

        // create a form for every payment method
        for (let pm of this.paymentMethods) {
          this.getFields(pm.name.toLowerCase());
        }

      },
      error => {
        console.log('An error ocurred.');
      }
    );
 
  }

  getFields(paymentMethodName: string) {

    let list: Field[] = [];
    let form: FormGroup;

    // get fields for a payment method
    this.formService.getFields(paymentMethodName).subscribe(
      data => {
        for (let field of data) {
          list.push(field);
        }

        this.fields[paymentMethodName] = list;

        // create a form group from the list of fields
        form = this.createFormGroup(list);
        this.forms[paymentMethodName] = form;
      },
      error => {
        console.log('An error ocurred.');
      }
    );
  }

  // create a form group from the list of fields
  createFormGroup(fields: Field[]) {
    const group: any = {};

    fields.forEach(field => {
      console.log(field.name + " " + field.required);
      group[field.name] = field.required ? new FormControl('', Validators.required) : new FormControl('');
    });

    return new FormGroup(group);
  }

  submitForm(stepper: MatStepper, paymentMethod: string) {

    // TODO: finish submit method

    stepper.next();
  }

}
