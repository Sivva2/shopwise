import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Client } from '../../../models/models';

@Component({
  selector: 'app-client-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-backdrop" (click)="close.emit()">
      <div class="modal" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3 class="modal-title">{{ client?.id ? 'Modifier' : 'Nouveau' }} Client</h3>
          <button class="modal-close" (click)="close.emit()">&times;</button>
        </div>
        <form (ngSubmit)="onSubmit()">
          <div class="modal-body">
            <div class="grid grid-2">
              <div class="form-group">
                <label class="form-label">Prénom *</label>
                <input type="text" class="form-control" [(ngModel)]="formData.firstName" name="firstName" required>
              </div>
              <div class="form-group">
                <label class="form-label">Nom *</label>
                <input type="text" class="form-control" [(ngModel)]="formData.lastName" name="lastName" required>
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">Email *</label>
              <input type="email" class="form-control" [(ngModel)]="formData.email" name="email" required>
            </div>
            <div class="form-group">
              <label class="form-label">Téléphone</label>
              <input type="tel" class="form-control" [(ngModel)]="formData.phone" name="phone">
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-outline" (click)="close.emit()">Annuler</button>
            <button type="submit" class="btn btn-primary">{{ client?.id ? 'Mettre à jour' : 'Créer' }}</button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class ClientFormComponent implements OnInit {
  @Input() client: Client | null = null;
  @Output() save = new EventEmitter<Client>();
  @Output() close = new EventEmitter<void>();

  formData: Client = { firstName: '', lastName: '', email: '', phone: '' };

  ngOnInit(): void {
    if (this.client) {
      this.formData = { ...this.client };
    }
  }

  onSubmit(): void {
    if (this.formData.firstName && this.formData.lastName && this.formData.email) {
      this.save.emit(this.formData);
    }
  }
}
