import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { BillingPlan } from '../model/billingplan';
import { Router, ActivatedRoute } from '@angular/router';
import { BillingPlanService } from './billing-plan.service';

@Component({
  selector: 'app-choose-billing-plan',
  templateUrl: './choose-billing-plan.component.html',
  styleUrls: ['./choose-billing-plan.component.css']
})
export class ChooseBillingPlanComponent implements OnInit {

  billingPlanForm: FormGroup;
  billingPlans: BillingPlan[];
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
        billingPlan: new FormControl('')
      }
    );

    // get available billing plans
    this.getBillingPlans();
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

  // submit form and create the subscription
  submitForm() {
    console.log(this.billingPlanForm.value.billingPlan);
    this.billingPlanService.createSubscription(this.subscriptionId, this.billingPlanForm.value.billingPlan.id).subscribe(
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
