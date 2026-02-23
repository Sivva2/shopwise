import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/clients',
    pathMatch: 'full'
  },
  {
    path: 'clients',
    loadComponent: () => import('./components/clients/client-list/client-list.component')
      .then(m => m.ClientListComponent)
  },
  {
    path: 'clients/:id',
    loadComponent: () => import('./components/clients/client-detail/client-detail.component')
      .then(m => m.ClientDetailComponent)
  },
  {
    path: 'appointments',
    loadComponent: () => import('./components/appointments/appointment-list/appointment-list.component')
      .then(m => m.AppointmentListComponent)
  },
  {
    path: 'services',
    loadComponent: () => import('./components/services/service-list/service-list.component')
      .then(m => m.ServiceListComponent)
  },
  {
    path: 'loyalty',
    loadComponent: () => import('./components/loyalty/loyalty-dashboard/loyalty-dashboard.component')
      .then(m => m.LoyaltyDashboardComponent)
  },
  {
    path: '**',
    redirectTo: '/clients'
  }
];
