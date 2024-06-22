import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { first } from 'rxjs/operators';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class ProfileComponent implements OnInit {

  userForm: FormGroup;
  submitted: boolean = false;
  error: any = '';
  successmsg: boolean = false;
  userData: any = {}; // Object to hold user data
  modifiedData: any = {}; // Object to hold modified user data

  constructor(
    private formBuilder: FormBuilder, 
    private http: HttpClient
  ) { }

  ngOnInit() {
    this.userForm = this.formBuilder.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      telephone: ['', Validators.required],
      dateNaissance: ['', Validators.required]
    });
  
    // Fetch user profile information when the component is initialized
    this.getUserProfile();
  
    // Listen to value changes of form controls and update modifiedData
    this.userForm.valueChanges.subscribe(data => {
      this.modifiedData = { ...data };
    });
  }
  

  // Convenience getter for easy access to form fields
  get f() { return this.userForm.controls; }

  getUserProfile() {
    this.http.get<any>(`${environment.backendHost}/client/profile`)
      .subscribe(
        data => {
          this.userData = data; // Store user data
          this.modifiedData = { ...data }; // Initialize modifiedData with user data
          this.userForm.patchValue({
            nom: this.userData.nom,
            prenom: this.userData.prenom,
            email: this.userData.email,
            telephone: this.userData.telephone,
          });
          console.log('User profile:', this.userForm.value);
        },
        error => {
          console.error('Error fetching user profile:', error);
          // Handle error here
        }
      );
  }

  onSubmit() {
    this.submitted = true;

    // Make the API call to update the profile
    this.http.put<any>(`${environment.backendHost}/client/update-profile`, {
      nom: this.f.nom.value,
      prenom: this.f.prenom.value,
      telephone: this.f.telephone.value,
      email: this.f.email.value,
      password: this.f.password.value
    }).pipe(first())
      .subscribe(
        data => {
          // Handle success, show success message or redirect
          console.log('Profile updated successfully:', data);
          this.successmsg = true;
        },
        error => {
          console.error('Error updating profile:', error);
          this.error = error.message; // Display error message to user
        }
      );
  }
}
