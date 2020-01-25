import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

baseUrl = 'https://localhost:8762/api/client/orders/';
httpOptions = {
  headers: new HttpHeaders({ 'Access-Control-Allow-Origin': '*', 'Content-Type': 'application/json' })
};

  constructor(private http: HttpClient) { }

  getPaymentMethod(id: string) {
    return this.http.get(this.baseUrl + 'paymentMethods/' + id, this.httpOptions);
  }

  sentSelectedPaymentMethod(id: string, paymentMethod: string) {
    console.log(paymentMethod);
    return this.http.put(this.baseUrl + 'complete/' + id, paymentMethod, this.httpOptions);
  }


}
