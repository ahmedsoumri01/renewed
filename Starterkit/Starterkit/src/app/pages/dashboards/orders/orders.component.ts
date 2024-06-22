import { Component, QueryList, ViewChildren, ViewChild } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Observable, throwError} from 'rxjs';

import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';
import { ModalDirective } from 'ngx-bootstrap/modal';
import {UntypedFormBuilder, UntypedFormGroup, FormArray, Validators, FormGroup, FormBuilder} from '@angular/forms';

import Swal from 'sweetalert2';

// Date Format
import { DatePipe } from '@angular/common';

import {Account, AccountDetails, OrdersModel} from './orders.model';
import { Orders } from './data';
import { OrdersService } from './orders.service';
import { NgbdOrdersSortableHeader, SortEvent } from './orders-sortable.directive';
import {HttpClient} from "@angular/common/http";
import {Customer} from "../customers/customers.model";
import {catchError, mergeMap} from "rxjs/operators";

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss'],
  providers: [OrdersService, DecimalPipe]
})

/**
 * Ecommerce orders component
 */
export class OrdersComponent {

  modalRef?: BsModalRef;
  masterSelected!: boolean;
  // bread crumb items
  breadCrumbItems: Array<{}>;
  term: any;

  ordersForm!: UntypedFormGroup;
  submitted = false;

  transactions: OrdersModel[] = [];
  // Table data
  content?: any;
  orderes?: any;
  ordersList!: Observable<OrdersModel[]>;
  total: Observable<number>;
  accountFormGroup! : FormGroup;
  errorMessage! : object;
  @ViewChildren(NgbdOrdersSortableHeader) headers!: QueryList<NgbdOrdersSortableHeader>;
  @ViewChild('showModal', { static: false }) showModal?: ModalDirective;
  @ViewChild('showModalDebit', { static: false }) showModalDebit?: ModalDirective;
  @ViewChild('removeItemModal', { static: false }) removeItemModal?: ModalDirective;

  deletId: any;
  currentPage : number = 0;
  pageSize : number = 5;
  accountObservable! : Observable<AccountDetails>;
  accounts! : Observable<Array<Account>>;
  operationFormGroup : FormGroup;
  accountId : string;
  operation : string;
  successMessage: string = '';
  errorMessage1: string = '';
  constructor(private modalService: BsModalService, public service: OrdersService, private formBuilder: UntypedFormBuilder, private fb: FormBuilder, private http : HttpClient, private datePipe: DatePipe) {
    this.ordersList = service.countries$;
    this.total = service.total$;
  }

  ngOnInit() {
    this.breadCrumbItems = [{ label: 'Comptes' }, { label: 'Liste comptes', active: true }];
    /*this.accounts = this.service.getAccounts().pipe(
      catchError(err => {
        this.errorMessage = err
        return throwError(err)
      })
    );*/
    this.handleSearchAccounts();
    // this.handleSearchAccountsByCustomer();
    this.operationFormGroup = this.fb.group({
      operationType : this.fb.control(null),
      amount : this.fb.control(0),
      description : this.fb.control(null),
      accountDestination : this.fb.control(null)
    })
    /**
     * Form Validation
     */

    this.accountFormGroup = this.fb.group({
      accountId : this.fb.control('')
    })
    this.ordersForm = this.formBuilder.group({
      id: "#VZ2101",
      ids: [''],
      name: ['', [Validators.required]],
      date: ['', [Validators.required]],
      total: ['', [Validators.required]],
      status: ['', [Validators.required]],
      payment: ['', [Validators.required]]
    });

    /**
    * fetches data
    */
    this.ordersList.subscribe(x => {
      this.orderes = Object.assign([], x);
    });
  }

  /**
   * Open modal
   * @param content modal content
   */
  openViewModal(content: any, id :any) {
    this.modalRef = this.modalService.show(content);
    this.accountObservable = this.service.getAccount(id,this.currentPage,this.pageSize);
  }

  openCreditModal(id :any, operation : string) {
    this.accountId = id;
    this.operation = operation;
    this.showModalDebit?.show();
  }

  // The master checkbox will check/ uncheck all items
  checkUncheckAll(ev: any) {
    this.orderes.forEach((x: { state: any; }) => x.state = ev.target.checked)
  }

  checkedValGet: any[] = [];
  // Delete Data
  deleteData(id: any) {
    if (id) {
      document.getElementById('lj_' + id)?.remove();
    }
    else {
      this.checkedValGet.forEach((item: any) => {
        document.getElementById('lj_' + item)?.remove();
      });
    }
  }
  // Delete Data
  confirm(id: any) {
    this.deletId = id
    this.removeItemModal.show();
  }

  deleteOrder() {
    this.orderes.splice(this.deletId, 1);
    this.removeItemModal.hide();
  }

  /**
   * Open modal
   * @param content modal content
   */
  openModal(id: string, operation: string) {
    this.accountId = id;
    this.operation = operation;
    this.modalRef = this.modalService.show(id);
  }
  /**
   * Form data get
   */
  get form() {
    return this.ordersForm.controls;
  }

  /**
  * Save user
  */
  // saveUser() {
  //   if (this.ordersForm.valid) {
  //     if (this.ordersForm.get('ids')?.value) {
  //       this.ordersForm.controls['date'].setValue(this.datePipe.transform(this.ordersForm.get('date')?.value,"YYYY-mm-dd"));
  //       this.service.products = Orders.map((data: { id: any; }) => data.id === this.ordersForm.get('ids')?.value ? { ...data, ...this.ordersForm.value } : data)
  //       this.orderes = this.service.products
  //       //  = this.orderes
  //     } else {
  //       const status = this.ordersForm.get('status')?.value;
  //       const index = 100;
  //     }
  //   }
  //   this.showModal?.hide()
  //   setTimeout(() => {
  //     this.ordersForm.reset();
  //   }, 0);
  //   this.ordersForm.reset();
  //   this.submitted = true
  // }

  
  saveUser() {
    console.log("hydts");
    this.submitted = true;
    
      if (this.ordersForm.get('ids')?.value) {
        this.ordersForm.controls['date'].setValue(this.datePipe.transform(this.ordersForm.get('date')?.value, "YYYY-MM-dd"));
        this.service.products = this.service.products.map((data: { id: any; }) => data.id === this.ordersForm.get('ids')?.value ? { ...data, ...this.ordersForm.value } : data);
        this.orderes = this.service.products;
      } else {
        const status = this.ordersForm.get('status')?.value;
        const index = 100; // Clarify its purpose or remove if not needed
      }
  
      const requestBody = {
        compteType: this.ordersForm.get('status')?.value,
        solde: this.ordersForm.get('total')?.value
      };
      console.log('Request body', requestBody);
      this.http.post('http://localhost:8080/client/create-compte', requestBody).subscribe(
        (response: any) => {
          console.log('Account created successfully', response);
          this.successMessage = 'Account created successfully!';
          this.errorMessage1 = '';
          this.handleSearchAccounts();
          // Handle successful response here
          // You might want to update the UI or show a success message
        },
        (error) => {
          console.error('Error creating account', error);
          this.errorMessage1 = 'Error creating account. Please try again.';
          this.successMessage = '';
          // Handle error response here
          // You might want to show an error message
        }
      );
  
      this.showModal?.hide();
      setTimeout(() => {
        this.ordersForm.reset();
        this.submitted = false;
      }, 0);
    
  }
  
  /**
   * Open Edit modal
   * @param content modal content
   */
  editModal(id: any) {
    this.submitted = false;
    // this.modalRef = this.modalService.show(content, { class: 'modal-md'});
    var modelTitle = document.querySelector('.modal-title') as HTMLAreaElement;
    modelTitle.innerHTML = 'Edit Order';
    var updateBtn = document.getElementById('addNewOrder-btn') as HTMLAreaElement;
    updateBtn.innerHTML = "Update";
    this.showModal?.show()
    var listData = this.orderes[id];
    this.ordersForm.controls['name'].setValue(listData.name);
    this.ordersForm.controls['date'].setValue(new Date(listData.date));
    this.ordersForm.controls['total'].setValue(listData.total);
    this.ordersForm.controls['status'].setValue(listData.status);
    this.ordersForm.controls['payment'].setValue(listData.payment);
    this.ordersForm.controls['ids'].setValue(listData.id);
  }

  handleSearchAccounts() {
    this.accounts = this.service.getAccounts().pipe(
      catchError(err => {
        this.errorMessage = err.message;
        return throwError(err);
      })
    );
  }

  handleSearchAccountsByCustomer() {
    let customer= {};
    this.accounts = this.service.getAccountsByCustomer(customer).pipe(
      catchError(err => {
        this.errorMessage = err.message;
        return throwError(err);
      })
    );
  }

  handleSearchAccount(accountId : any) {
    this.accountObservable = this.service.getAccount(accountId,this.currentPage,this.pageSize);
  }

  handleSearchBankAccount() {
    let accountId : string = this.accountFormGroup.value.accountId;
    let account : Observable<Account> = this.service.getAccountById(accountId);
    // add account observable to array Observable<Account>
  }

  goToPage(page: number, id : any) {
    this.currentPage = page;
    this.handleSearchAccount(id);
  }

  handleAccountOperation() {
    let op = this.operation;
    let id = this.accountId;
    let amount : number =  this.operationFormGroup.value.amount;
    console.log("amount: "+amount);
    let description : string =  this.operationFormGroup.value.description;
    let accountDestination : string =  this.operationFormGroup.value.accountDestination;
    console.log("op: " +op,"id: "+id)
    if(op == 'Retrait'){
      this.service.debit(id, amount, description).subscribe({
        next : (data) => {
          alert("Débit avec succés");
          this.handleSearchAccounts();
        },
        error : (err) => { console.log(err)}
      })
    } else if(op == 'CREDIT') {
      this.service.credit(id, amount, description).subscribe({
        next : (data) => {
          alert("Crédit avec succés");
          this.handleSearchAccounts();
        },
        error : (err) => { console.log(err)}
      })
    } else if(op == 'TRANSFER'){
      this.service.transfer(id, accountDestination, amount, description).subscribe({
        next : (data) => {
          alert("Transfert avec succés");
          this.handleSearchAccounts();
        },
        error : (err) => { console.log(err)}
      })
    }


    this.operationFormGroup.reset();
    this.showModalDebit?.hide();

  }
}
