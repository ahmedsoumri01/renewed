import { NgModule } from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { DashboardsRoutingModule } from './dashboards-routing.module';
import { UIModule } from '../../shared/ui/ui.module';
import { WidgetModule } from '../../shared/widget/widget.module';

import { NgApexchartsModule } from 'ng-apexcharts';

import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { BsDropdownModule,BsDropdownConfig} from 'ngx-bootstrap/dropdown';
import { CarouselModule } from 'ngx-bootstrap/carousel';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { ModalModule } from 'ngx-bootstrap/modal';

import { SimplebarAngularModule } from 'simplebar-angular';

import { DefaultComponent } from './default/default.component';
import {OrdersComponent} from "./orders/orders.component";
import {CustomersComponent} from "./customers/customers.component";
import {UploadsComponent} from "./form/uploads/uploads.component";
import {ListComponent} from "./invoices/list/list.component";
import {FilemanagerComponent} from "./filemanager/filemanager.component";
import {NgxDropzoneModule} from "ngx-dropzone";

import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import {FacturesComponent} from "./factures/orders.component";
import {MandatsComponent} from "./mandats/orders.component";

@NgModule({
  declarations: [
    DefaultComponent,
    OrdersComponent,
    CustomersComponent,
    UploadsComponent,
    ListComponent,
    FilemanagerComponent,
    FacturesComponent,
    MandatsComponent
  ],
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        DashboardsRoutingModule,
        UIModule,
        BsDropdownModule.forRoot(),
        TooltipModule.forRoot(),
        TabsModule.forRoot(),
        CarouselModule.forRoot(),
        BsDatepickerModule.forRoot(),
        WidgetModule,
        NgApexchartsModule,
        SimplebarAngularModule,
        ModalModule.forRoot(),
        NgxDropzoneModule
    ],
  providers: [
    BsDropdownConfig,
    DatePipe],
})
export class DashboardsModule { }
