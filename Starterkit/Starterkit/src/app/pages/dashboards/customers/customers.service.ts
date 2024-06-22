import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from "@angular/common/http";
import { environment } from "../../../../environments/environment";
import { Customer } from './customers.model';
import { tap, catchError } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class CustomersService {
  private _customers$ = new BehaviorSubject<Customer[]>([]);
  private _loading$ = new BehaviorSubject<boolean>(true);

  constructor(private http: HttpClient) {
    this.loadCustomers();
  }

  loadCustomers() {
    this.http.get<Customer[]>(`${environment.backendHost}/admin/comptes`).pipe(
      tap(customers => this._customers$.next(customers)),
      tap(() => this._loading$.next(false)),
      catchError(error => {
        console.error('Error loading customers', error);
        this._loading$.next(false);
        return [];
      })
    ).subscribe();
  }

  get customers$(): Observable<Customer[]> {
    return this._customers$.asObservable();
  }

  blockCustomer(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(`${environment.backendHost}/compte/${customer.numCompte}/desactivate`, {}).pipe(
      tap(() => this.loadCustomers()),
      catchError(error => {
        console.error('Error blocking customer', error);
        throw error;
      })
    );
  }

  unblockCustomer(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(`${environment.backendHost}/compte/${customer.numCompte}/activate`, {}).pipe(
      tap(() => this.loadCustomers()),
      catchError(error => {
        console.error('Error unblocking customer', error);
        throw error;
      })
    );
  }

  get loading$(): Observable<boolean> {
    return this._loading$.asObservable();
  }
}
