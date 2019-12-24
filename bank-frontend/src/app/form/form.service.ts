import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { PayModel } from '../model/payModel';

@Injectable({
  providedIn: 'root'
})
export class FormService {

  constructor(private http: HttpClient) { }

  pay(payInfo: PayModel, token: string) {
    const httpOptions = {
      headers: new HttpHeaders({ 'Access-Control-Allow-Origin': '*', 'Content-Type': 'application/json' })
    };

    const baseUrl = 'https://localhost:8081/pay/'+token;

    return this.http.post(baseUrl, payInfo, httpOptions);
  }

}
