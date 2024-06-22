import { Component, QueryList, ViewChildren, ViewChild } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Observable } from 'rxjs';

import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';
import { ModalDirective } from 'ngx-bootstrap/modal';
import { UntypedFormBuilder, UntypedFormGroup, FormArray, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

import Swal from 'sweetalert2';

// Date Format
import { DatePipe } from '@angular/common';

import { OrdersModel } from './orders.model';
import { Orders } from './data';
import { OrdersService } from './orders.service';
import { NgbdOrdersSortableHeader, SortEvent } from './orders-sortable.directive';

@Component({
  selector: 'app-mandats',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss'],
  providers: [OrdersService, DecimalPipe]
})

/**
 * Ecommerce orders component
 */
export class MandatsComponent {

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

  @ViewChildren(NgbdOrdersSortableHeader) headers!: QueryList<NgbdOrdersSortableHeader>;
  @ViewChild('showModal', { static: false }) showModal?: ModalDirective;
  @ViewChild('removeItemModal', { static: false }) removeItemModal?: ModalDirective;
  deletId: any;

  constructor(private modalService: BsModalService,private http: HttpClient, public service: OrdersService, private formBuilder: UntypedFormBuilder, private datePipe: DatePipe) {
    this.ordersList = service.countries$;
    this.total = service.total$;
  }

  ngOnInit() {
    this.breadCrumbItems = [{ label: 'Mandats' }, { label: 'Liste mandats', active: true }];

    /**
     * Form Validation
     */
    this.ordersForm = this.formBuilder.group({
      id: "#VZ2101",
      ids: [''],
      nomRecepteur: ['', [Validators.required]],
      date: ['', [Validators.required]],
      total: ['', [Validators.required]],
      status: ['', [Validators.required]],
      payment: ['', [Validators.required]],
      cinEmetteur: ['', [Validators.required]],
      cinRecepteur: ['', [Validators.required]],
      emailRecepteur: ['', [Validators.required]],
      prenomRecepteur: ['', [Validators.required]],
      numCompte: ['', [Validators.required]],
      montant: ['', [Validators.required]] // Add 'montant' form control here

    });

    /**
    * fetches data
    */
    this.http.get<any[]>('http://localhost:8080/client/mandats').subscribe(data => {
      this.orderes = data;
    });
  }

  /**
   * Open modal
   * @param content modal content
   */
  openViewModal(content: any) {
    this.modalRef = this.modalService.show(content);
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
  /**
   * Form data get
   */
  get form() {
    return this.ordersForm.controls;
  }

  /**
  * Save user
  */
  saveMandat() {
    // Assuming ordersForm is defined in your component
      const mandatData = {
        numCompte: this.ordersForm.get('numCompte').value,
        montant: this.ordersForm.get('montant').value,
        cinEmetteur: this.ordersForm.get('cinEmetteur').value, // This value should be obtained from the authenticated user's information
        cinRecepteur: this.ordersForm.get('cinRecepteur').value,
        nomRecepteur: this.ordersForm.get('nomRecepteur').value,
        prenomRecepteur: this.ordersForm.get('prenomRecepteur').value,
        emailRecepteur: this.ordersForm.get('emailRecepteur').value,
      };
  
      // Modify this part to get the authenticated user's CIN and assign it to cinEmetteur
  
      this.http.post<any>('http://localhost:8080/client/create-mandat', mandatData).subscribe(
        (response) => {
          // Handle success, maybe show a success message
          console.log('Mandat saved successfully:', response);
          //show success message in alert
          Swal.fire('Success', 'Mandat saved successfully', 'success');
      // Reload the page
      this.http.get<any[]>('http://localhost:8080/client/mandats').subscribe(data => {
        this.orderes = data;
      }
      //
      );
        },
        (error) => {
          // Handle error, maybe show an error message
          console.error('Error saving mandat:', error);
        }
      );
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
