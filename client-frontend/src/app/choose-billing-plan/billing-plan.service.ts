import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BillingPlan } from '../model/billingplan';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BillingPlanService {

  baseUrl = 'https://localhost:8762/api/client/subscription/';

  httpOptions = {
    headers: new HttpHeaders({ 'Access-Control-Allow-Origin': '*', 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) { }

  getBillingPlans(id: string): Observable<BillingPlan[]> {
    return this.http.get<BillingPlan[]>(this.baseUrl + 'plans/' + id, this.httpOptions);
  }

  createSubscription(id: string, planId: number) {
    return this.http.put(this.baseUrl + 'complete/' + id + '/' + planId, this.httpOptions);
  }

}
