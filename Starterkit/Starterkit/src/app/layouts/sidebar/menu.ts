import { MenuItem } from './menu.model';
export const MENU: MenuItem[] = [
  {
    id: 1,
    label: 'DASHBOARD',
    icon: 'bx-home-circle',
    link: '/dashboard'
  },
  {
    id: 2,
    label: 'CLIENTS',
    icon: 'bx-user-circle',
    link: '/dashboard/CLIENTS'
  },
  {
    id: 3,
    label: 'COMPTES',
    icon: 'bx-list-ul',
    link: '/dashboard/COMPTES'
  },
  {
    id: 4,
    label: 'MANDATS',
    icon: 'bx-receipt',
    link: '/dashboard/MANDATS'
  },
  {
    id: 5,
    label: 'FACTURES',
    icon: 'bx-file',
    link: '/dashboard/FACTURES'
  }
]