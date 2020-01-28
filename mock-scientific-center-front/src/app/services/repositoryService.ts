import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Http, Response } from '@angular/http';


import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class RepositoryService {

  constructor(private httpClient: HttpClient, private http: Http) {

  }

  getAllMagazines() {
    return this.httpClient.get('https://localhost:9897/magazines') as Observable<any>
  }

  payForMagazines(orderDTO) {
    return this.httpClient.post("http://localhost:9897/pay", orderDTO) as Observable<any>;
  }

  subscriptionForMagazines(subscriptionDTO) {
    return this.httpClient.post("http://localhost:9897/subscription", subscriptionDTO) as Observable<any>;
  }

}