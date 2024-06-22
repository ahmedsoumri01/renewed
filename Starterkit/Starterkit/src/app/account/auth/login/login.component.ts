import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '../../../../environments/environment';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm: UntypedFormGroup;
  submitted: boolean = false;
  error: string = '';
  returnUrl: string;

  year: number = new Date().getFullYear();

  constructor(
    private formBuilder: UntypedFormBuilder, 
    private route: ActivatedRoute, 
    private router: Router, 
    private http: HttpClient,
    private toastr: ToastrService
  ) { }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });

    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
  }

  get f() { return this.loginForm.controls; }

  onSubmit() {
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    } else {
      console.log(this.f.email.value, this.f.password.value); 
      this.http.post<any>(`${environment.backendHost}/auth/login`, {
        email: this.f.email.value,
        password: this.f.password.value
      }).subscribe(
        data => {
          console.log(data);
          localStorage.setItem('currentUser', JSON.stringify(data));
          this.toastr.success('Login successful');
          this.router.navigate([this.returnUrl]);
        },
        error => {
          this.error = error.error ? error.error : 'Login failed';
          console.error(this.error);
          this.toastr.error(this.error);
        });
    }
  }
}
