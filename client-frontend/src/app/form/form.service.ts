import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Field } from '../model/field';
import { Observable } from 'rxjs/internal/Observable';
import { PaymentMethod } from '../model/paymentmethod';

@Injectable({
  providedIn: 'root'
})

export class FormService {

  baseUrl = 'https://localhost:8762/api/';

  constructor(private http: HttpClient) { }

  getFields(service: string): Observable<Field[]> {
    const httpOptions = {
      headers: new HttpHeaders({ 'Access-Control-Allow-Origin': '*', 'Content-Type': 'application/json' })
    };

    const url = this.baseUrl + service + '/client/fields';

    return this.http.get<Field[]>(url, httpOptions);
  }

  getPaymentMethods(): Observable<PaymentMethod[]> {
    const httpOptions = {
      headers: new HttpHeaders({ 'Access-Control-Allow-Origin': '*', 'Content-Type': 'application/json' })
    };

    const url = this.baseUrl + 'client/paymentmethod';

    return this.http.get<PaymentMethod[]>(url, httpOptions);
  }

}
