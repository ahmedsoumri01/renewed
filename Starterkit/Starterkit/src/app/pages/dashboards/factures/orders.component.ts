import { Component, QueryList, ViewChildren, ViewChild } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Observable } from 'rxjs';

import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { UntypedFormBuilder, UntypedFormGroup, FormArray, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import Swal from 'sweetalert2';
import { FormBuilder, FormGroup } from '@angular/forms';

// Date Format
import { DatePipe } from '@angular/common';

import { OrdersModel } from './orders.model';
import { Orders } from './data';
import { OrdersService } from './orders.service';
import { NgbdOrdersSortableHeader, SortEvent } from './orders-sortable.directive';

@Component({
  selector: 'app-factures',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss'],
  providers: [OrdersService, DecimalPipe]
})

/**
 * Ecommerce orders component
 */
export class FacturesComponent {
    paymentForm!: FormGroup; // Declare paymentForm property

  selectedStatus: string = '';
  modalRef?: BsModalRef;
  masterSelected!: boolean;
  // bread crumb items
  
  selected: boolean = false;
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
  facid:number;
  @ViewChildren(NgbdOrdersSortableHeader) headers!: QueryList<NgbdOrdersSortableHeader>;
  @ViewChild('showModal', { static: false }) showModal?: ModalDirective;
  @ViewChild('removeItemModal', { static: false }) removeItemModal?: ModalDirective;
  deletId: any;

  constructor(private modalService: BsModalService,private http: HttpClient,
    public service: OrdersService, private formBuilder: UntypedFormBuilder, private datePipe: DatePipe) {
    this.ordersList = service.countries$;
    this.total = service.total$;
  }

  ngOnInit() {
    this.breadCrumbItems = [{ label: 'Factures' }, { label: 'Liste factures', active: true }];

    /**
     * Form Validation
     */
    this.ordersForm = this.formBuilder.group({
      id: "#VZ2101",
      ids: [''],
      name: ['', [Validators.required]],
      date: ['', [Validators.required]],
      total: ['', [Validators.required]],
      status: ['', [Validators.required]],
      payment: ['', [Validators.required]]
    });
    this.paymentForm = this.formBuilder.group({
      amount: ['', Validators.required] // Initialize with required validator
    });
    /**
    * fetches data
    */

  }
  onStatusChange() {
    // Check if the selectedStatus matches a specific value where you want to set boolean to true
    this.fetchAndFilterOrders();

    if (this.selectedStatus !== '') {
      this.selected = true;
    } else {
      this.selected = false;
    }
  }
  fetchAndFilterOrders() {
    this.http.get<OrdersModel[]>('http://localhost:8080/factures/all')
      .pipe(
        map(factures => factures.filter(facture => facture.libelle === this.selectedStatus))
      )
      .subscribe(filteredFactures => {
        this.orderes = filteredFactures;
        console.log('Filtered factures:', this.orderes);
      });
  }
  chooseAnotherFacture() {
    this.selected = false;
  }

  /**
   * Open modal
   * @param content modal content
   */
  openViewModal(content: any,id:number) {
    this.modalRef = this.modalService.show(content);
    this.facid = id;
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
  openModal(content: any) {
    this.submitted = false;
    this.modalRef = this.modalService.show(content, { class: 'modal-md' });
  }
  payAmount(accountId: string) {
    const factureId = this.facid; // Assuming this.facid contains the ID of the facture
    console.log('Facture ID:', factureId);
    console.log('Compte ID:', accountId);
    // Ensure that accountId is a valid number
        // Make the HTTP POST request to your backend endpoint
        this.http.post<any>(`http://localhost:8080/factures/pay/${factureId}?compteId=${accountId}`, {}).subscribe(
            (response) => {
                console.log('Payment successful:', response);
                this.modalRef?.hide(); // Hide the modal after making the payment request
                this.fetchAndFilterOrders(); // Fetch and filter orders after payment
                // Do something with the successful response, if needed
            },
            (error) => {
                console.error('Error making payment:', error);
                // Handle errors here, if needed
            }
        );

        this.modalRef?.hide(); // Hide the modal after making the payment request
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
  saveUser() {
    if (this.ordersForm.valid) {
      if (this.ordersForm.get('ids')?.value) {
        this.ordersForm.controls['date'].setValue(this.datePipe.transform(this.ordersForm.get('date')?.value,"YYYY-mm-dd"));
        this.service.products = Orders.map((data: { id: any; }) => data.id === this.ordersForm.get('ids')?.value ? { ...data, ...this.ordersForm.value } : data)
        this.orderes = this.service.products
        //  = this.orderes
      } else {
        const name = this.ordersForm.get('name')?.value;
        const date = this.datePipe.transform(this.ordersForm.get('date')?.value,"YYYY-mm-dd");
        const total = this.ordersForm.get('total')?.value;
        const status = this.ordersForm.get('status')?.value;
        const payment = this.ordersForm.get('payment')?.value;
        const index = 100;
        Orders.push({
          id: this.orderes.length + 1,
          name,
          date,
          total,
          status,
          payment_icon: '',
          payment,
          index
        });
      }
    }
    this.showModal?.hide()
    setTimeout(() => {
      this.ordersForm.reset();
    }, 0);
    this.ordersForm.reset();
    this.submitted = true
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
}
