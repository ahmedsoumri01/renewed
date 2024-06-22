import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { first } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit {

  signupForm: UntypedFormGroup;
  submitted: boolean = false;
  error: any = '';
  successmsg: boolean = false;

  constructor(
    private formBuilder: UntypedFormBuilder, 
    private route: ActivatedRoute, 
    private router: Router, 
    private http: HttpClient,
    private toastr: ToastrService
  ) { }

  ngOnInit() {
    this.signupForm = this.formBuilder.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      telephone: ['', Validators.required],
      dateNaissance: ['', Validators.required]
    });
  }

  get f() { return this.signupForm.controls; }

  onSubmit() {
    this.submitted = true;
  
    if (this.signupForm.invalid) {
      return;
    } else {
      // Convert date to Unix timestamp format in milliseconds
      const dateOfBirth = new Date(this.f.dateNaissance.value);
      const timestamp = dateOfBirth.getTime(); // Get Unix timestamp in milliseconds
  
      this.http.post<any>(`${environment.backendHost}/auth/register`, {
        nom: this.f.nom.value,
        prenom: this.f.prenom.value,
        dateNaissance: timestamp, // Send Unix timestamp to backend
        telephone: this.f.telephone.value,
        email: this.f.email.value,
        password: this.f.password.value
      }).pipe(first())
        .subscribe(
          data => {
            this.successmsg = true;
            this.toastr.success('Registration successful. Redirecting to login...');
            setTimeout(() => {
              this.router.navigate(['/account/login']);
            }, 2000); // Redirect after 2 seconds
          },
          error => {
            this.error = error.error ? error.error : 'Registration failed';
          });
    }
  }
}
