import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Appointment, AppointmentStatus, Client, Service } from '../../../models/models';
import { AppointmentService } from '../../../services/appointment.service';
import { ClientService } from '../../../services/client.service';
import { ServiceService } from '../../../services/service.service';
import { AppointmentFormComponent } from '../appointment-form/appointment-form.component';

@Component({
  selector: 'app-appointment-list',
  standalone: true,
  imports: [CommonModule, FormsModule, AppointmentFormComponent],
  template: `
    <div class="page-header">
      <h1 class="page-title">Gestion des Rendez-vous</h1>
      <button class="btn btn-primary" (click)="openCreateModal()">
        <span class="material-icons">add</span> Nouveau RDV
      </button>
    </div>

    <div class="grid grid-4 mb-3">
      <div class="stat-card">
        <div class="stat-card-icon primary"><span class="material-icons">calendar_today</span></div>
        <div class="stat-card-value">{{ appointments.length }}</div>
        <div class="stat-card-label">Total RDV</div>
      </div>
      <div class="stat-card">
        <div class="stat-card-icon info"><span class="material-icons">schedule</span></div>
        <div class="stat-card-value">{{ getCountByStatus('SCHEDULED') }}</div>
        <div class="stat-card-label">Planifiés</div>
      </div>
      <div class="stat-card">
        <div class="stat-card-icon success"><span class="material-icons">check_circle</span></div>
        <div class="stat-card-value">{{ getCountByStatus('COMPLETED') }}</div>
        <div class="stat-card-label">Honorés</div>
      </div>
      <div class="stat-card">
        <div class="stat-card-icon warning"><span class="material-icons">cancel</span></div>
        <div class="stat-card-value">{{ getCountByStatus('CANCELLED') }}</div>
        <div class="stat-card-label">Annulés</div>
      </div>
    </div>

    <div class="filters">
      <div class="form-group">
        <label class="form-label">Client</label>
        <select class="form-control" [(ngModel)]="filterClientId" (change)="applyFilters()">
          <option [ngValue]="null">Tous les clients</option>
          <option *ngFor="let c of clients" [ngValue]="c.id">{{ c.firstName }} {{ c.lastName }}</option>
        </select>
      </div>
      <div class="form-group">
        <label class="form-label">Statut</label>
        <select class="form-control" [(ngModel)]="filterStatus" (change)="applyFilters()">
          <option [ngValue]="null">Tous les statuts</option>
          <option value="SCHEDULED">Planifié</option>
          <option value="COMPLETED">Honoré</option>
          <option value="CANCELLED">Annulé</option>
        </select>
      </div>
      <div class="form-group">
        <label class="form-label">Date</label>
        <input type="date" class="form-control" [(ngModel)]="filterDate" (change)="applyFilters()">
      </div>
    </div>

    <div *ngIf="loading" class="loading"><div class="loading-spinner"></div></div>

    <div class="card" *ngIf="!loading">
      <div class="table-container">
        <table class="table">
          <thead>
            <tr><th>Date/Heure</th><th>Client</th><th>Service</th><th>Statut</th><th>Actions</th></tr>
          </thead>
          <tbody>
            <tr *ngFor="let apt of filteredAppointments">
              <td data-label="Date/Heure">
                <strong>{{ apt.appointmentDate | date:'dd/MM/yyyy' }}</strong><br>
                <span class="text-muted">{{ apt.appointmentTime }}</span>
              </td>
              <td data-label="Client">{{ apt.clientName }}</td>
              <td data-label="Service">
                {{ apt.serviceName }}<br>
                <span class="text-muted">{{ apt.serviceDuration }} min • {{ apt.servicePoints }} pts</span>
              </td>
              <td data-label="Statut">
                <span class="badge" [ngClass]="'badge-' + apt.status.toLowerCase()">{{ getStatusLabel(apt.status) }}</span>
              </td>
              <td data-label="Actions">
                <div class="actions">
                  <button *ngIf="apt.status === 'SCHEDULED'" class="btn btn-sm btn-success" (click)="updateStatus(apt, 'COMPLETED')" title="Marquer honoré">✓</button>
                  <button *ngIf="apt.status === 'SCHEDULED'" class="btn btn-sm btn-warning" (click)="updateStatus(apt, 'CANCELLED')" title="Annuler">✗</button>
                  <button *ngIf="apt.status === 'SCHEDULED'" class="btn btn-sm btn-secondary" (click)="openEditModal(apt)">✏️</button>
                  <button *ngIf="apt.status !== 'COMPLETED'" class="btn btn-sm btn-danger" (click)="confirmDelete(apt)">🗑️</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <div *ngIf="filteredAppointments.length === 0" class="empty-state">
          <div class="empty-state-icon">📅</div>
          <h3>Aucun rendez-vous</h3>
          <button class="btn btn-primary" (click)="openCreateModal()">Créer un rendez-vous</button>
        </div>
      </div>
    </div>

    <app-appointment-form *ngIf="showModal" [appointment]="selectedAppointment" [clients]="clients" [services]="services" (save)="onSave($event)" (close)="closeModal()"></app-appointment-form>

    <div class="modal-backdrop" *ngIf="showDeleteConfirm" (click)="showDeleteConfirm = false">
      <div class="modal" (click)="$event.stopPropagation()">
        <div class="modal-header"><h3>Confirmer la suppression</h3><button class="modal-close" (click)="showDeleteConfirm = false">&times;</button></div>
        <div class="modal-body"><p>Supprimer ce rendez-vous ?</p></div>
        <div class="modal-footer">
          <button class="btn btn-outline" (click)="showDeleteConfirm = false">Annuler</button>
          <button class="btn btn-danger" (click)="deleteAppointment()">Supprimer</button>
        </div>
      </div>
    </div>
  `
})
export class AppointmentListComponent implements OnInit {
  appointments: Appointment[] = [];
  filteredAppointments: Appointment[] = [];
  clients: Client[] = [];
  services: Service[] = [];
  loading = false;
  showModal = false;
  showDeleteConfirm = false;
  selectedAppointment: Appointment | null = null;
  appointmentToDelete: Appointment | null = null;
  filterClientId: number | null = null;
  filterStatus: AppointmentStatus | null = null;
  filterDate: string | null = null;

  constructor(
    private appointmentService: AppointmentService,
    private clientService: ClientService,
    private serviceService: ServiceService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.clientService.getAllClients().subscribe(c => this.clients = c);
    this.serviceService.getActiveServices().subscribe(s => this.services = s);
    this.appointmentService.getAllAppointments().subscribe({
      next: (a) => { this.appointments = a; this.filteredAppointments = a; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  applyFilters(): void {
    this.filteredAppointments = this.appointments.filter(a => {
      if (this.filterClientId && a.clientId !== this.filterClientId) return false;
      if (this.filterStatus && a.status !== this.filterStatus) return false;
      if (this.filterDate && a.appointmentDate !== this.filterDate) return false;
      return true;
    });
  }

  getCountByStatus(status: AppointmentStatus): number {
    return this.appointments.filter(a => a.status === status).length;
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = { SCHEDULED: 'Planifié', COMPLETED: 'Honoré', CANCELLED: 'Annulé' };
    return labels[status] || status;
  }

  openCreateModal(): void { this.selectedAppointment = null; this.showModal = true; }
  openEditModal(apt: Appointment): void { this.selectedAppointment = { ...apt }; this.showModal = true; }
  closeModal(): void { this.showModal = false; this.selectedAppointment = null; }

  onSave(apt: Appointment): void {
    const obs = apt.id ? this.appointmentService.updateAppointment(apt.id, apt) : this.appointmentService.createAppointment(apt);
    obs.subscribe({ next: () => { this.loadData(); this.closeModal(); }, error: () => alert('Erreur') });
  }

  updateStatus(apt: Appointment, status: AppointmentStatus): void {
    if (apt.id) {
      this.appointmentService.updateStatus(apt.id, status).subscribe({
        next: () => this.loadData(),
        error: () => alert('Erreur')
      });
    }
  }

  confirmDelete(apt: Appointment): void { this.appointmentToDelete = apt; this.showDeleteConfirm = true; }
  deleteAppointment(): void {
    if (this.appointmentToDelete?.id) {
      this.appointmentService.deleteAppointment(this.appointmentToDelete.id).subscribe({
        next: () => { this.loadData(); this.showDeleteConfirm = false; },
        error: () => alert('Erreur')
      });
    }
  }
}
