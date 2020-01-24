import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { PaymentService } from './payment.service';

@Component({
  selector: 'app-choose-payment-method',
  templateUrl: './choose-payment-method.component.html',
  styleUrls: ['./choose-payment-method.component.css']
})
export class ChoosePaymentMethodComponent implements OnInit {

  paymentForm: FormGroup;

  paymentMethods: string[];

  orderId: string;

  constructor(private paymentService: PaymentService, private router: Router, private activeRoute: ActivatedRoute) { }

  ngOnInit() {
    this.paymentForm = new FormGroup(
      {
        paymentMethod: new FormControl('')
      }
    );

    this.activeRoute.paramMap.subscribe(
      params => {
        this.orderId = params.get('id');
      });

    this.getPaymentMethods();
  }

  getPaymentMethods() {
    this.paymentService.getPaymentMethod(this.orderId).subscribe(
      data => {
        this.paymentMethods = data as string[];
      },
      error => {
        alert(error);
      }
    );
  }

  submitForm() {
    console.log(this.paymentForm.value);
    this.paymentService.sentSelectedPaymentMethod(this.orderId, this.paymentForm.value.paymentMethod).subscribe(
      data => {
        const response = data as any;
        document.location.href  = response.url;
      },
      error => {
        alert(error);
      }
    );

  }

}
