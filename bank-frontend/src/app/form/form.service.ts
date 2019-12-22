import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { PayModel } from '../model/payModel';

@Injectable({
  providedIn: 'root'
})
export class FormService {

  baseUrl = 'https://localhost:8081/api/pay/84ae202987cd6b2e783bf4a9ea6542a2808123fb920d5afdf5';

  constructor(private http: HttpClient) { }

  pay(payInfo: PayModel) {
    const httpOptions = {
      headers: new HttpHeaders({ 'Access-Control-Allow-Origin': '*', 'Content-Type': 'application/json' })
    };

    return this.http.post(this.baseUrl, payInfo, httpOptions);
  }

}
