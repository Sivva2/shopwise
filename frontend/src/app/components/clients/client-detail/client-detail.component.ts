import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Client, Appointment, PointTransaction, LoyaltyBalance } from '../../../models/models';
import { ClientService } from '../../../services/client.service';
import { AppointmentService } from '../../../services/appointment.service';
import { LoyaltyService } from '../../../services/loyalty.service';

@Component({
  selector: 'app-client-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="page-header">
      <div>
        <a routerLink="/clients" class="btn btn-outline btn-sm mb-2">← Retour</a>
        <h1 class="page-title">{{ client?.firstName }} {{ client?.lastName }}</h1>
      </div>
    </div>

    <div *ngIf="loading" class="loading"><div class="loading-spinner"></div></div>

    <div *ngIf="!loading && client" class="grid grid-2">
      <!-- Client Info -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Informations</h3>
        </div>
        <div class="info-list">
          <div class="info-item"><span class="info-label">Email</span><span>{{ client.email }}</span></div>
          <div class="info-item"><span class="info-label">Téléphone</span><span>{{ client.phone || '-' }}</span></div>
          <div class="info-item"><span class="info-label">Inscrit le</span><span>{{ client.createdAt | date:'dd/MM/yyyy' }}</span></div>
        </div>
      </div>

      <!-- Loyalty Info -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Fidélité</h3>
        </div>
        <div class="loyalty-stats">
          <div class="loyalty-balance">
            <span class="loyalty-value">{{ balance?.balance || 0 }}</span>
            <span class="loyalty-label">Points actuels</span>
          </div>
          <div class="loyalty-details">
            <div><span class="text-success">+{{ balance?.totalEarned || 0 }}</span> gagnés</div>
            <div><span class="text-danger">-{{ balance?.totalRedeemed || 0 }}</span> utilisés</div>
          </div>
        </div>
      </div>

      <!-- Appointments -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Rendez-vous ({{ appointments.length }})</h3>
        </div>
        <div *ngIf="appointments.length === 0" class="empty-state">
          <p>Aucun rendez-vous</p>
        </div>
        <div *ngFor="let apt of appointments.slice(0, 5)" class="appointment-item">
          <div>
            <strong>{{ apt.serviceName }}</strong>
            <div class="text-muted">{{ apt.appointmentDate | date:'dd/MM/yyyy' }} à {{ apt.appointmentTime }}</div>
          </div>
          <span class="badge" [ngClass]="'badge-' + apt.status.toLowerCase()">{{ getStatusLabel(apt.status) }}</span>
        </div>
      </div>

      <!-- Transactions -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Historique Points</h3>
        </div>
        <div *ngIf="transactions.length === 0" class="empty-state">
          <p>Aucune transaction</p>
        </div>
        <div *ngFor="let tx of transactions.slice(0, 5)" class="transaction-item">
          <div>
            <strong [ngClass]="tx.points > 0 ? 'text-success' : 'text-danger'">
              {{ tx.points > 0 ? '+' : '' }}{{ tx.points }} pts
            </strong>
            <div class="text-muted">{{ tx.description }}</div>
          </div>
          <span class="text-muted">{{ tx.createdAt | date:'dd/MM/yyyy' }}</span>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .info-list { display: flex; flex-direction: column; gap: 1rem; }
    .info-item { display: flex; justify-content: space-between; padding: 0.5rem 0; border-bottom: 1px solid #eee; }
    .info-label { color: #666; }
    .loyalty-stats { text-align: center; }
    .loyalty-balance { margin-bottom: 1rem; }
    .loyalty-value { font-size: 3rem; font-weight: 700; color: #F39C12; display: block; }
    .loyalty-label { color: #666; }
    .loyalty-details { display: flex; justify-content: space-around; }
    .appointment-item, .transaction-item { display: flex; justify-content: space-between; align-items: center; padding: 0.75rem 0; border-bottom: 1px solid #eee; }
  `]
})
export class ClientDetailComponent implements OnInit {
  client: Client | null = null;
  appointments: Appointment[] = [];
  transactions: PointTransaction[] = [];
  balance: LoyaltyBalance | null = null;
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private clientService: ClientService,
    private appointmentService: AppointmentService,
    private loyaltyService: LoyaltyService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) this.loadData(id);
  }

  loadData(id: number): void {
    this.clientService.getClientById(id).subscribe(c => { this.client = c; this.loading = false; });
    this.appointmentService.getAppointmentsByClient(id).subscribe(a => this.appointments = a);
    this.loyaltyService.getClientTransactions(id).subscribe(t => this.transactions = t);
    this.loyaltyService.getClientBalance(id).subscribe(b => this.balance = b);
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = { SCHEDULED: 'Planifié', COMPLETED: 'Honoré', CANCELLED: 'Annulé' };
    return labels[status] || status;
  }
}
