export interface OrdersModel {
    id: any;
    name: string;
    date: string;
    total: string;
    status: string;
    payment_icon: string;
    payment: any;
    index: any;
  }


export interface Account {
  type:          string;
  id:            string;
  balance:       number;
  createdAt:     Date;
  status:        null;
  customerDTO:   CustomerDTO;
  interestRate?: number;
  overDraft?:    number;
}

export interface CustomerDTO {
  id:          number;
  name:        string;
  email:       string;
  phoneNumber: string;
  address:     string;
  birthDate:   null;
  isBlocked:   boolean;
}


export interface AccountOperation {
  id:            number;
  operationDate: Date;
  amount:        number;
  description:   string;
  type:          string;
}

export interface AccountDetails {
  accountId:            string;
  balance:              number;
  currentPage:          number;
  totalPages:           number;
  pageSize:             number;
  accountOperationDTOS: AccountOperation[];
}
