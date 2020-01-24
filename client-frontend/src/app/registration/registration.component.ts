import { Component, OnInit } from '@angular/core';
import { Seller } from '../model/seller';
import { RegistrationService } from './registration.service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TokenStorageService } from '../authentication/token-storage.service';


@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  registrationForm: FormGroup;

  constructor(private registrationService: RegistrationService,
              private tokenStorageService: TokenStorageService,
              private router: Router) { }

  ngOnInit() {
    this.registrationForm = new FormGroup(
      {
        name: new FormControl('', Validators.required),
        email: new FormControl('', [Validators.email, Validators.required]),
        password: new FormControl('', Validators.required)
      }
    );
  }

  // submit form and register to KP
  submitForm() {
    const seller: Seller = {
      name: this.registrationForm.value.name,
      email: this.registrationForm.value.email,
      password: this.registrationForm.value.password
    };

    this.registrationService.register(seller).subscribe(
      data => {
        // save token and username
        this.tokenStorageService.saveToken(data.token);
        this.tokenStorageService.saveType(data.type);
        this.tokenStorageService.saveUsername(data.username);

        alert('Successfully registered.');
        
        this.router.navigateByUrl('/paymentmethods');
      },
      error => {
        if (error.status === 400) {
          alert(error.error);
        } else {
          alert('An error occured! Please try again.');
        }
      }
    );

  }
}
