import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Field } from '../model/field';
import { Observable } from 'rxjs/internal/Observable';
import { PaymentMethod } from '../model/paymentmethod';
import { ResponseLink } from '../model/responselink';

@Injectable({
  providedIn: 'root'
})

export class FormService {

  baseUrl = 'https://localhost:8762/api/';

  httpOptions = {
    headers: new HttpHeaders({ 'Access-Control-Allow-Origin': '*', 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) { }

  getFields(service: string): Observable<Field[]> {

    const url = this.baseUrl + service + '/client/fields';

    return this.http.get<Field[]>(url, this.httpOptions);
  }

  getPaymentMethods(): Observable<PaymentMethod[]> {

    const url = this.baseUrl + 'client/paymentmethod';

    return this.http.get<PaymentMethod[]>(url, this.httpOptions);
  }

  addPaymentMethod(paymentmethod: string, valuesList: any) {

    const url = this.baseUrl + 'client/seller/paymentmethod/' + paymentmethod;

    return this.http.post(url, valuesList, this.httpOptions);
  }

  addSubscriptionPlan(valuesList: any) {

    const url = this.baseUrl + 'client/subscriptionplan';

    return this.http.post(url, valuesList, this.httpOptions);
  }


  getLink(): Observable<ResponseLink> {
    const httpOptions = {
      headers: new HttpHeaders({ 'Access-Control-Allow-Origin': '*', 'Content-Type': 'application/json' })
    };

    return this.http.get<ResponseLink>(this.baseUrl + 'client/seller/returnlink', httpOptions);
  }

}
