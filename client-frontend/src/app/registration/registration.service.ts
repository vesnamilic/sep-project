import { Injectable } from '@angular/core';
import { Seller } from '../model/seller';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  baseUrl = 'https://localhost:8762/api/client/seller/';

  constructor(private http: HttpClient) { }

  register(seller: Seller) {
    const httpOptions = {
      headers: new HttpHeaders({ 'Access-Control-Allow-Origin': '*', 'Content-Type': 'application/json' })
    };

    return this.http.post(this.baseUrl, seller, httpOptions);
  }

}
