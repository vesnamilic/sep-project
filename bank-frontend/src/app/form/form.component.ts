import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { FormService } from './form.service';
import { Router } from '@angular/router';
import { PayModel } from '../model/payModel';
import { ActivatedRoute } from '@angular/router';


@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent implements OnInit {

  payForm: FormGroup;
  token: string;

  constructor(private formService: FormService,
    private router: Router, private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
    
    //this.token = window.location.href.substring(30); //ako je https 30, ako je http 29
    //console.log(this.token);
    
    this.activatedRoute.paramMap.subscribe(
      
      params => {
        console.log(params);
          this.token = params.get('token');
          console.log(this.token);  
        });
    this.payForm = new FormGroup(
      {
        name: new FormControl('', Validators.required),
        lastName: new FormControl('', Validators.required),
        cvv: new FormControl('', Validators.required),
        pan: new FormControl('', Validators.required),
        month: new FormControl('', Validators.required),
        year: new FormControl('', Validators.required)
      }
    );
  }

  submitForm() {
    console.log(this.payForm.value);
    const payInfo: PayModel = {
      name: this.payForm.value.name,
      lastName: this.payForm.value.lastName,
      year: this.payForm.value.year,
      month: this.payForm.value.month,
      cvv: this.payForm.value.cvv,
      pan: this.payForm.value.pan
    };
    console.log(payInfo);
    console.log(this.token);
    this.formService.pay(payInfo, this.token).subscribe(
      data => {
        alert('Payed successfully');
        this.router.navigateByUrl('/');
      },
      error => {
        alert('An error ocurred.');
      }
    );

  }
}
