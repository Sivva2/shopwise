import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Client, PointTransaction, LoyaltyBalance } from '../../../models/models';
import { ClientService } from '../../../services/client.service';
import { LoyaltyService } from '../../../services/loyalty.service';

@Component({
  selector: 'app-loyalty-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-header">
      <h1 class="page-title">Programme de Fidélité</h1>
    </div>

    <!-- Client Selection -->
    <div class="card mb-3">
      <div class="form-group" style="margin-bottom: 0;">
        <label class="form-label">Sélectionner un client</label>
        <select class="form-control" [(ngModel)]="selectedClientId" (change)="onClientChange()">
          <option [ngValue]="null">-- Choisir un client --</option>
          <option *ngFor="let c of clients" [ngValue]="c.id">{{ c.firstName }} {{ c.lastName }} ({{ c.loyaltyPoints }} pts)</option>
        </select>
      </div>
    </div>

    <!-- Client Loyalty Info -->
    <div *ngIf="selectedClient && balance" class="grid grid-2 mb-3">
      <div class="card">
        <h3 class="card-title">{{ selectedClient.firstName }} {{ selectedClient.lastName }}</h3>
        <div class="loyalty-display">
          <div class="loyalty-balance">
            <span class="balance-value">{{ balance.balance }}</span>
            <span class="balance-label">Points disponibles</span>
          </div>
          <div class="loyalty-stats">
            <div class="stat">
              <span class="stat-value text-success">+{{ balance.totalEarned }}</span>
              <span class="stat-label">Gagnés</span>
            </div>
            <div class="stat">
              <span class="stat-value text-danger">-{{ balance.totalRedeemed }}</span>
              <span class="stat-label">Utilisés</span>
            </div>
          </div>
        </div>
      </div>

      <div class="card">
        <h3 class="card-title">Actions</h3>
        <div class="action-buttons">
          <button class="btn btn-success btn-lg" (click)="showAdjustModal = true; adjustType = 'add'">
            <span class="material-icons">add</span> Ajouter des points
          </button>
          <button class="btn btn-warning btn-lg" (click)="showRedeemModal = true" [disabled]="balance.balance === 0">
            <span class="material-icons">redeem</span> Utiliser des points
          </button>
        </div>
      </div>
    </div>

    <!-- Transaction History -->
    <div class="card" *ngIf="selectedClient">
      <div class="card-header">
        <h3 class="card-title">Historique des transactions</h3>
      </div>
      <div class="table-container">
        <table class="table">
          <thead>
          <tr><th>Date</th><th>Type</th><th>Points</th><th>Description</th></tr>
          </thead>
          <tbody>
          <tr *ngFor="let tx of transactions">
            <td data-label="Date">{{ tx.createdAt | date:'dd/MM/yyyy HH:mm' }}</td>
            <td data-label="Type">
              <span class="badge" [ngClass]="getTypeBadgeClass(tx.transactionType)">{{ getTypeLabel(tx.transactionType) }}</span>
            </td>
            <td data-label="Points">
              <strong [ngClass]="tx.points > 0 ? 'text-success' : 'text-danger'">
                {{ tx.points > 0 ? '+' : '' }}{{ tx.points }}
              </strong>
            </td>
            <td data-label="Description">{{ tx.description }}</td>
          </tr>
          </tbody>
        </table>
        <div *ngIf="transactions.length === 0" class="empty-state">
          <p>Aucune transaction</p>
        </div>
      </div>
    </div>

    <!-- Top Clients Leaderboard -->
    <div class="card" *ngIf="!selectedClient">
      <div class="card-header">
        <h3 class="card-title">🏆 Classement Fidélité</h3>
      </div>
      <div class="leaderboard">
        <div *ngFor="let client of getTopClients(); let i = index" class="leaderboard-item">
          <div class="rank" [ngClass]="'rank-' + (i + 1)">{{ i + 1 }}</div>
          <div class="client-info">
            <strong>{{ client.firstName }} {{ client.lastName }}</strong>
            <span class="text-muted">{{ client.email }}</span>
          </div>
          <div class="points">
            <span class="badge badge-points">⭐ {{ client.loyaltyPoints }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Adjust Points Modal -->
    <div class="modal-backdrop" *ngIf="showAdjustModal" (click)="showAdjustModal = false">
      <div class="modal" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3>Ajouter des points</h3>
          <button class="modal-close" (click)="showAdjustModal = false">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">Nombre de points</label>
            <input type="number" class="form-control" [(ngModel)]="adjustPoints" min="1">
          </div>
          <div class="form-group">
            <label class="form-label">Raison</label>
            <input type="text" class="form-control" [(ngModel)]="adjustDescription" placeholder="Ex: Bonus promotionnel">
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-outline" (click)="showAdjustModal = false">Annuler</button>
          <button class="btn btn-success" (click)="doAdjust()">Ajouter</button>
        </div>
      </div>
    </div>

    <!-- Redeem Points Modal -->
    <div class="modal-backdrop" *ngIf="showRedeemModal" (click)="showRedeemModal = false">
      <div class="modal" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3>Utiliser des points</h3>
          <button class="modal-close" (click)="showRedeemModal = false">&times;</button>
        </div>
        <div class="modal-body">
          <p>Solde actuel: <strong>{{ balance?.balance }} points</strong></p>
          <div class="form-group">
            <label class="form-label">Points à utiliser</label>
            <input type="number" class="form-control" [(ngModel)]="redeemPoints" min="1" [max]="balance?.balance || 0">
          </div>
          <div class="form-group">
            <label class="form-label">Description</label>
            <input type="text" class="form-control" [(ngModel)]="redeemDescription" placeholder="Ex: Réduction 10%">
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-outline" (click)="showRedeemModal = false">Annuler</button>
          <button class="btn btn-warning" (click)="doRedeem()">Utiliser</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .loyalty-display { text-align: center; }
    .loyalty-balance { margin-bottom: 1.5rem; }
    .balance-value { font-size: 4rem; font-weight: 700; color: #F39C12; display: block; line-height: 1; }
    .balance-label { color: #666; }
    .loyalty-stats { display: flex; justify-content: center; gap: 3rem; }
    .stat { text-align: center; }
    .stat-value { font-size: 1.5rem; font-weight: 600; display: block; }
    .stat-label { color: #666; font-size: 0.875rem; }
    .action-buttons { display: flex; flex-direction: column; gap: 1rem; }
    .leaderboard-item { display: flex; align-items: center; gap: 1rem; padding: 1rem; border-bottom: 1px solid #eee; }
    .rank { width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; background: #eee; }
    .rank-1 { background: gold; color: #333; }
    .rank-2 { background: silver; color: #333; }
    .rank-3 { background: #CD7F32; color: white; }
    .client-info { flex: 1; display: flex; flex-direction: column; }
  `]
})
export class LoyaltyDashboardComponent implements OnInit {
  clients: Client[] = [];
  selectedClientId: number | null = null;
  selectedClient: Client | null = null;
  balance: LoyaltyBalance | null = null;
  transactions: PointTransaction[] = [];

  showAdjustModal = false;
  showRedeemModal = false;
  adjustType: 'add' | 'subtract' = 'add';
  adjustPoints = 0;
  adjustDescription = '';
  redeemPoints = 0;
  redeemDescription = '';

  constructor(private clientService: ClientService, private loyaltyService: LoyaltyService) {}

  ngOnInit(): void {
    this.loadClients();
  }

  loadClients(): void {
    this.clientService.getAllClients().subscribe(c => this.clients = c);
  }

  onClientChange(): void {
    if (this.selectedClientId) {
      this.selectedClient = this.clients.find(c => c.id === this.selectedClientId) || null;
      this.loadClientData();
    } else {
      this.selectedClient = null;
      this.balance = null;
      this.transactions = [];
    }
  }

  loadClientData(): void {
    if (this.selectedClientId) {
      this.loyaltyService.getClientBalance(this.selectedClientId).subscribe(b => this.balance = b);
      this.loyaltyService.getClientTransactions(this.selectedClientId).subscribe(t => this.transactions = t);
    }
  }

  getTopClients(): Client[] {
    return [...this.clients].sort((a, b) => (b.loyaltyPoints || 0) - (a.loyaltyPoints || 0)).slice(0, 10);
  }

  getTypeLabel(type: string): string {
    const labels: Record<string, string> = { EARNED: 'Gagné', REDEEMED: 'Utilisé', ADJUSTMENT: 'Ajustement' };
    return labels[type] || type;
  }

  getTypeBadgeClass(type: string): string {
    const classes: Record<string, string> = { EARNED: 'badge-completed', REDEEMED: 'badge-cancelled', ADJUSTMENT: 'badge-scheduled' };
    return classes[type] || '';
  }

  doAdjust(): void {
    if (this.selectedClientId && this.adjustPoints > 0) {
      this.loyaltyService.adjustPoints(this.selectedClientId, this.adjustPoints, this.adjustDescription || 'Ajustement manuel').subscribe({
        next: () => {
          this.loadClientData();
          this.loadClients();
          this.showAdjustModal = false;
          this.adjustPoints = 0;
          this.adjustDescription = '';
        },
        error: () => alert('error')
      });
    }
  }

  doRedeem(): void {
    if (this.selectedClientId && this.redeemPoints > 0) {
      this.loyaltyService.redeemPoints(this.selectedClientId, this.redeemPoints, this.redeemDescription || 'Utilisation de points').subscribe({
        next: () => {
          this.loadClientData();
          this.loadClients();
          this.showRedeemModal = false;
          this.redeemPoints = 0;
          this.redeemDescription = '';
        },
        error: () => alert('error')
      });
    }
  }
}