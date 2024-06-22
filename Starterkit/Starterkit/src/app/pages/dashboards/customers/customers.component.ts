import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Customer } from './customers.model';
import { CustomersService } from './customers.service';

@Component({
  selector: 'app-customers',
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.scss']
})
export class CustomersComponent implements OnInit {

  customers$: Observable<Customer[]>;
  loading$: Observable<boolean>;
  errorMessage!: string;

  constructor(
    public service: CustomersService,
    private toastr: ToastrService
  ) {
    this.customers$ = this.service.customers$;
    this.loading$ = this.service.loading$;
  }

  ngOnInit() {
    this.service.loadCustomers();
  }

  handleBlockCustomer(customer: Customer) {
    if (confirm("Vous êtes sûr de vouloir bloquer ce client?")) {
      this.service.blockCustomer(customer).subscribe({
        next: () => {
          this.toastr.success('Client bloqué avec succès');
          this.service.loadCustomers();
        },
        error: err => {
          this.toastr.success('Client bloqué avec succès');
          console.log(err);
          this.service.loadCustomers();
        }
      });
    }
  }

  handleUnblockCustomer(customer: Customer) {
    if (confirm("Vous êtes sûr de vouloir débloquer ce client?")) {
      this.service.unblockCustomer(customer).subscribe({
        next: () => {
          this.toastr.success('Client débloqué avec succès');
          this.service.loadCustomers();
        },
        error: err => {
          this.toastr.success('Client débloqué avec succès');
          //reload the customers
          this.service.loadCustomers();
          console.log(err);
        }
      });
    }
  }
}
