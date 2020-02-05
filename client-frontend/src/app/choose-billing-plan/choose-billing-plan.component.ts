import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { BillingPlan } from '../model/billingplan';
import { Router, ActivatedRoute } from '@angular/router';
import { BillingPlanService } from './billing-plan.service';
import { PaymentMethod } from '../model/paymentmethod';

@Component({
  selector: 'app-choose-billing-plan',
  templateUrl: './choose-billing-plan.component.html',
  styleUrls: ['./choose-billing-plan.component.css']
})
export class ChooseBillingPlanComponent implements OnInit {

  billingPlanForm: FormGroup;
  billingPlans: BillingPlan[];
  paymentMethods: PaymentMethod[];
  subscriptionId: string;

  constructor(private billingPlanService: BillingPlanService,
              private router: Router,
              private activeRoute: ActivatedRoute) { }

  ngOnInit() {
    // get subcription id from the route
    this.activeRoute.paramMap.subscribe(
      params => {
        this.subscriptionId = params.get('id');
      });

    // initialize the form
    this.billingPlanForm = new FormGroup(
      {
        paymentMethod: new FormControl('', [Validators.required]),
        billingPlan: new FormControl('', [Validators.required])
      }
    );

    // get available billing plans
    this.getBillingPlans();

    // get available payment methods
    this.getSubscriptionPaymentMethods();
  }


  // get billing plans for the subscription
  getBillingPlans() {
    this.billingPlanService.getBillingPlans(this.subscriptionId).subscribe(
      data => {
        this.billingPlans = data;
      },
      error => {
        alert('An error occurred. Please try again!');
      }
    );
  }

  // get payment methods for the subscription
  getSubscriptionPaymentMethods() {
    this.billingPlanService.getSubscriptionPaymentMethods(this.subscriptionId).subscribe(
      data => {
        this.paymentMethods = data;
        console.log(this.paymentMethods);
      },
      error => {
        alert('An error occurred. Please try again!');
      }
    );
  }

  // submit form and create the subscription
  submitForm() {

    const valuesList: any = {
      paymentMethod: this.billingPlanForm.value.paymentMethod.name,
      subscriptionPlanId: this.billingPlanForm.value.billingPlan.id
    };

    console.log(valuesList);
    this.billingPlanService.createSubscription(this.subscriptionId, valuesList).subscribe(
      data => {
        const response = data as any;
        document.location.href  = response.url;
      },
      error => {
        alert('An error occurred. Please try again!');
      }
    );

  }

}
