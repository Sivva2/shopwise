import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Service } from '../../../models/models';
import { ServiceService } from '../../../services/service.service';
import { ServiceFormComponent } from '../service-form/service-form.component';

@Component({
  selector: 'app-service-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ServiceFormComponent],
  template: `
    <div class="page-header">
      <h1 class="page-title">Gestion des Services</h1>
      <button class="btn btn-primary" (click)="openCreateModal()">
        <span class="material-icons">add</span> Nouveau Service
      </button>
    </div>

    <div *ngIf="loading" class="loading"><div class="loading-spinner"></div></div>

    <div class="grid grid-3" *ngIf="!loading">
      <div *ngFor="let service of services" class="card service-card" [class.inactive]="!service.active">
        <div class="service-header">
          <h3>{{ service.name }}</h3>
          <span class="badge" [ngClass]="service.active ? 'badge-completed' : 'badge-cancelled'">
            {{ service.active ? 'Actif' : 'Inactif' }}
          </span>
        </div>
        <p class="service-description">{{ service.description || 'Aucune description' }}</p>
        <div class="service-details">
          <div class="service-detail">
            <span class="material-icons">schedule</span>
            <span>{{ service.durationMinutes }} minutes</span>
          </div>
          <div class="service-detail">
            <span class="material-icons">star</span>
            <span>{{ service.pointsAwarded }} points</span>
          </div>
        </div>
        <div class="service-actions">
          <button class="btn btn-sm btn-secondary" (click)="openEditModal(service)">Modifier</button>
          <button class="btn btn-sm btn-danger" (click)="confirmDelete(service)">Supprimer</button>
        </div>
      </div>

      <div *ngIf="services.length === 0" class="empty-state" style="grid-column: 1 / -1;">
        <div class="empty-state-icon">⚙️</div>
        <h3>Aucun service</h3>
        <button class="btn btn-primary" (click)="openCreateModal()">Créer un service</button>
      </div>
    </div>

    <app-service-form *ngIf="showModal" [service]="selectedService" (save)="onSave($event)" (close)="closeModal()"></app-service-form>

    <div class="modal-backdrop" *ngIf="showDeleteConfirm" (click)="showDeleteConfirm = false">
      <div class="modal" (click)="$event.stopPropagation()">
        <div class="modal-header"><h3>Confirmer la suppression</h3><button class="modal-close" (click)="showDeleteConfirm = false">&times;</button></div>
        <div class="modal-body"><p>Supprimer le service <strong>{{ serviceToDelete?.name }}</strong> ?</p></div>
        <div class="modal-footer">
          <button class="btn btn-outline" (click)="showDeleteConfirm = false">Annuler</button>
          <button class="btn btn-danger" (click)="deleteService()">Supprimer</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .service-card { display: flex; flex-direction: column; }
    .service-card.inactive { opacity: 0.6; }
    .service-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.5rem; }
    .service-header h3 { margin: 0; font-size: 1.1rem; }
    .service-description { color: #666; font-size: 0.875rem; flex: 1; margin-bottom: 1rem; }
    .service-details { display: flex; gap: 1.5rem; margin-bottom: 1rem; padding: 0.75rem; background: #f8f9fa; border-radius: 8px; }
    .service-detail { display: flex; align-items: center; gap: 0.5rem; font-size: 0.875rem; }
    .service-detail .material-icons { font-size: 1rem; color: #666; }
    .service-actions { display: flex; gap: 0.5rem; }
  `]
})
export class ServiceListComponent implements OnInit {
  services: Service[] = [];
  loading = false;
  showModal = false;
  showDeleteConfirm = false;
  selectedService: Service | null = null;
  serviceToDelete: Service | null = null;

  constructor(private serviceService: ServiceService) {}

  ngOnInit(): void { this.loadServices(); }

  loadServices(): void {
    this.loading = true;
    this.serviceService.getAllServices().subscribe({
      next: (s) => { this.services = s; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  openCreateModal(): void { this.selectedService = null; this.showModal = true; }
  openEditModal(s: Service): void { this.selectedService = { ...s }; this.showModal = true; }
  closeModal(): void { this.showModal = false; this.selectedService = null; }

  onSave(service: Service): void {
    const obs = service.id ? this.serviceService.updateService(service.id, service) : this.serviceService.createService(service);
    obs.subscribe({ next: () => { this.loadServices(); this.closeModal(); }, error: () => alert('Erreur') });
  }

  confirmDelete(s: Service): void { this.serviceToDelete = s; this.showDeleteConfirm = true; }
  deleteService(): void {
    if (this.serviceToDelete?.id) {
      this.serviceService.deleteService(this.serviceToDelete.id).subscribe({
        next: () => { this.loadServices(); this.showDeleteConfirm = false; },
        error: () => alert('Erreur')
      });
    }
  }
}
