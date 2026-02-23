import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Client } from '../../../models/models';
import { ClientService } from '../../../services/client.service';
import { ClientFormComponent } from '../client-form/client-form.component';

@Component({
  selector: 'app-client-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, ClientFormComponent],
  template: `
    <div class="page-header">
      <h1 class="page-title">Gestion des Clients</h1>
      <button class="btn btn-primary" (click)="openCreateModal()">
        <span class="material-icons">add</span>
        Nouveau Client
      </button>
    </div>

    <div class="grid grid-4 mb-3">
      <div class="stat-card">
        <div class="stat-card-icon primary"><span class="material-icons">people</span></div>
        <div class="stat-card-value">{{ clients.length }}</div>
        <div class="stat-card-label">Total Clients</div>
      </div>
      <div class="stat-card">
        <div class="stat-card-icon success"><span class="material-icons">star</span></div>
        <div class="stat-card-value">{{ getTotalPoints() }}</div>
        <div class="stat-card-label">Points Distribués</div>
      </div>
      <div class="stat-card">
        <div class="stat-card-icon warning"><span class="material-icons">emoji_events</span></div>
        <div class="stat-card-value">{{ getTopClient()?.firstName || '-' }}</div>
        <div class="stat-card-label">Meilleur Client</div>
      </div>
      <div class="stat-card">
        <div class="stat-card-icon info"><span class="material-icons">trending_up</span></div>
        <div class="stat-card-value">{{ getAveragePoints() }}</div>
        <div class="stat-card-label">Moyenne Points</div>
      </div>
    </div>

    <div class="filters">
      <div class="form-group">
        <input type="text" class="form-control" placeholder="Rechercher un client..." [(ngModel)]="searchQuery" (input)="filterClients()">
      </div>
    </div>

    <div *ngIf="loading" class="loading"><div class="loading-spinner"></div></div>
    <div *ngIf="error" class="alert alert-danger">{{ error }}</div>

    <div class="card" *ngIf="!loading">
      <div class="table-container">
        <table class="table">
          <thead>
            <tr><th>Client</th><th>Email</th><th>Téléphone</th><th>Points</th><th>Actions</th></tr>
          </thead>
          <tbody>
            <tr *ngFor="let client of filteredClients">
              <td data-label="Client">
                <div class="client-info">
                  <div class="client-avatar">{{ getInitials(client) }}</div>
                  <strong>{{ client.firstName }} {{ client.lastName }}</strong>
                </div>
              </td>
              <td data-label="Email">{{ client.email }}</td>
              <td data-label="Téléphone">{{ client.phone || '-' }}</td>
              <td data-label="Points"><span class="badge badge-points">⭐ {{ client.loyaltyPoints }}</span></td>
              <td data-label="Actions">
                <div class="actions">
                  <a [routerLink]="['/clients', client.id]" class="btn btn-sm btn-outline">👁</a>
                  <button class="btn btn-sm btn-secondary" (click)="openEditModal(client)">✏️</button>
                  <button class="btn btn-sm btn-danger" (click)="confirmDelete(client)">🗑️</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <div *ngIf="filteredClients.length === 0" class="empty-state">
          <div class="empty-state-icon">👥</div>
          <h3 class="empty-state-title">Aucun client trouvé</h3>
          <p class="empty-state-text">Commencez par ajouter votre premier client</p>
          <button class="btn btn-primary" (click)="openCreateModal()">Ajouter un client</button>
        </div>
      </div>
    </div>

    <app-client-form *ngIf="showModal" [client]="selectedClient" (save)="onSave($event)" (close)="closeModal()"></app-client-form>

    <div class="modal-backdrop" *ngIf="showDeleteConfirm" (click)="showDeleteConfirm = false">
      <div class="modal" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3 class="modal-title">Confirmer la suppression</h3>
          <button class="modal-close" (click)="showDeleteConfirm = false">&times;</button>
        </div>
        <div class="modal-body">
          <p>Supprimer <strong>{{ clientToDelete?.firstName }} {{ clientToDelete?.lastName }}</strong> ?</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-outline" (click)="showDeleteConfirm = false">Annuler</button>
          <button class="btn btn-danger" (click)="deleteClient()">Supprimer</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .client-info { display: flex; align-items: center; gap: 0.75rem; }
    .client-avatar { width: 40px; height: 40px; border-radius: 50%; background: linear-gradient(135deg, #3498DB, #2980B9); color: white; display: flex; align-items: center; justify-content: center; font-weight: 600; }
  `]
})
export class ClientListComponent implements OnInit {
  clients: Client[] = [];
  filteredClients: Client[] = [];
  searchQuery = '';
  loading = false;
  error = '';
  showModal = false;
  showDeleteConfirm = false;
  selectedClient: Client | null = null;
  clientToDelete: Client | null = null;

  constructor(private clientService: ClientService) {}

  ngOnInit(): void { this.loadClients(); }

  loadClients(): void {
    this.loading = true;
    this.clientService.getAllClients().subscribe({
      next: (clients) => { this.clients = clients; this.filteredClients = clients; this.loading = false; },
      error: () => { this.error = 'Erreur lors du chargement'; this.loading = false; }
    });
  }

  filterClients(): void {
    const q = this.searchQuery.toLowerCase();
    this.filteredClients = this.clients.filter(c => c.firstName.toLowerCase().includes(q) || c.lastName.toLowerCase().includes(q) || c.email.toLowerCase().includes(q));
  }

  getInitials(c: Client): string { return `${c.firstName.charAt(0)}${c.lastName.charAt(0)}`.toUpperCase(); }
  getTotalPoints(): number { return this.clients.reduce((s, c) => s + (c.loyaltyPoints || 0), 0); }
  getAveragePoints(): number { return this.clients.length ? Math.round(this.getTotalPoints() / this.clients.length) : 0; }
  getTopClient(): Client | null { return this.clients.length ? this.clients.reduce((m, c) => (c.loyaltyPoints || 0) > (m.loyaltyPoints || 0) ? c : m) : null; }
  openCreateModal(): void { this.selectedClient = null; this.showModal = true; }
  openEditModal(c: Client): void { this.selectedClient = { ...c }; this.showModal = true; }
  closeModal(): void { this.showModal = false; this.selectedClient = null; }

  onSave(client: Client): void {
    const obs = client.id ? this.clientService.updateClient(client.id, client) : this.clientService.createClient(client);
    obs.subscribe({ next: () => { this.loadClients(); this.closeModal(); }, error: () => alert('Erreur') });
  }

  confirmDelete(c: Client): void { this.clientToDelete = c; this.showDeleteConfirm = true; }
  deleteClient(): void {
    if (this.clientToDelete?.id) {
      this.clientService.deleteClient(this.clientToDelete.id).subscribe({
        next: () => { this.loadClients(); this.showDeleteConfirm = false; },
        error: () => alert('Erreur')
      });
    }
  }
}
