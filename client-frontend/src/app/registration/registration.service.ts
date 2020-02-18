import { Injectable } from '@angular/core';
import { Seller } from '../model/seller';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { JwtResponse } from '../model/jwtresponse';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  baseUrl = 'https://localhost:8762/api/client/seller/';

  constructor(private http: HttpClient) { }

  register(seller: any): Observable<JwtResponse> {
    const httpOptions = {
      headers: new HttpHeaders({ 'Access-Control-Allow-Origin': '*', 'Content-Type': 'application/json' })
    };

    return this.http.post<JwtResponse>(this.baseUrl + 'login', seller, httpOptions);
  }
}
