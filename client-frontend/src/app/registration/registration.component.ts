import { Component, OnInit } from '@angular/core';
import { Seller } from '../model/seller';
import { RegistrationService } from './registration.service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';


@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  registrationForm: FormGroup;

  constructor(private registrationService: RegistrationService,
              private router: Router) { }

  ngOnInit() {
    this.registrationForm = new FormGroup(
      {
        name: new FormControl('', Validators.required),
        email: new FormControl('', [Validators.email, Validators.required])
      }
    );
  }

  submitForm() {
    const seller: Seller = {
      id: null,
      name: this.registrationForm.value.name,
      email: this.registrationForm.value.email
    };

    this.registrationService.register(seller).subscribe(
      data => {
        alert('Successfully registered.');
        this.router.navigateByUrl('/paymentmethods');
      },
      error => {
        alert('An error ocurred.');
      }
    );

  }
}
