import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DefaultComponent } from './dashboards/default/default.component';
import {CustomersComponent} from "./dashboards/customers/customers.component";
import {OrdersComponent} from "./dashboards/orders/orders.component";
import {ListComponent} from "./dashboards/invoices/list/list.component";
import {FilemanagerComponent} from "./dashboards/filemanager/filemanager.component";
import {FacturesComponent} from "./dashboards/factures/orders.component";
import {MandatsComponent} from "./dashboards/mandats/orders.component";
import {AuthGuard} from "../core/guards/auth.guard";

const routes: Routes = [
  // { path: '', redirectTo: 'dashboard' },
  {
    path: "dashboard",
    component: DefaultComponent
  },
  { path: 'dashboard', component: DefaultComponent },
  { path: 'COMPTES', component:  OrdersComponent},
  { path: 'CLIENTS', component:  CustomersComponent},
  //{ path: 'FACTURES', component:  FilemanagerComponent},
  //{ path: 'MANDATS', component:  ListComponent},
  { path: 'FACTURES', component:  FacturesComponent},
  { path: 'MANDATS', component:  MandatsComponent},
  { path: 'dashboards', loadChildren: () => import('./dashboards/dashboards.module').then(m => m.DashboardsModule) },
  /*{
    path: 'admin', component: AdminDashboardComponent,
    canActivate: [AuthGuard],
    data: {
      role: 'ROLE_ADMIN'
    }
  },
  { path: 'user', component: UserDashboardComponent,
    canActivate: [AuthGuard],
    data: {
      role: 'ROLE_USER'
    }
  },*/
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PagesRoutingModule { }
