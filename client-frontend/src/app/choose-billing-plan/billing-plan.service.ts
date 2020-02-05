import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BillingPlan } from '../model/billingplan';
import { Observable } from 'rxjs';
import { PaymentMethod } from '../model/paymentmethod';

@Injectable({
  providedIn: 'root'
})
export class BillingPlanService {

  baseUrl = 'https://localhost:8762/api/client/';

  httpOptions = {
    headers: new HttpHeaders({ 'Access-Control-Allow-Origin': '*', 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) { }

  getBillingPlans(id: string): Observable<BillingPlan[]> {
    return this.http.get<BillingPlan[]>(this.baseUrl + 'subscriptionplan/' + id, this.httpOptions);
  }

  getSubscriptionPaymentMethods(id: string): Observable<PaymentMethod[]> {
    return this.http.get<PaymentMethod[]>(this.baseUrl + 'paymentmethod/subscription/' + id, this.httpOptions);
  }

  createSubscription(id: string, valuesList: any) {
    return this.http.put(this.baseUrl + 'subscription/complete/' + id, valuesList, this.httpOptions);
  }

}
