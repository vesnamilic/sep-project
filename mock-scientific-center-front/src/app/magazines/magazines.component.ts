import { Component, OnInit } from '@angular/core';
import { RepositoryService } from '../services/repositoryService';

@Component({
  selector: 'app-magazines',
  templateUrl: './magazines.component.html',
  styleUrls: ['./magazines.component.css']
})
export class MagazinesComponent implements OnInit {

  private magazines = [];

  constructor(private repositoryService: RepositoryService) {

    let x = this.repositoryService.getAllMagazines();

    x.subscribe(
      res => {
        this.magazines = res;
        console.log(this.magazines);
      },
      err => {
        console.log("Error occured");
      }
    );
  }

  ngOnInit() {

  }

  buy(magazine: any) {
    let orderDTO=new OrderDTO(magazine.email, magazine.price, "USD");
    let x = this.repositoryService.payForMagazines(orderDTO);
    x.subscribe(
      res => {
        //redirekcija na front kp
        document.location.href  = res.url;
      },
      err => {
        console.log("Error occured");
      }
    );
  }

  subscription(magazine: any) {
    let subscriptionDTO=new SubscriptionDTO(magazine.email);
    let x = this.repositoryService.subscriptionForMagazines(subscriptionDTO);
    x.subscribe(
      res => {
        alert("subscription is success");
      },
      err => {
        console.log("Error occured");
      }
    ); 
   }
}

export class OrderDTO {
  constructor(private email: string, private paymentAmount: number, private paymentCurrency: string) { }
}

export class SubscriptionDTO {
  constructor(private email: string) { }
}
