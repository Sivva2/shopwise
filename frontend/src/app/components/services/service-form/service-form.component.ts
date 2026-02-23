import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Service } from '../../../models/models';

@Component({
  selector: 'app-service-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-backdrop" (click)="close.emit()">
      <div class="modal" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3 class="modal-title">{{ service?.id ? 'Modifier' : 'Nouveau' }} Service</h3>
          <button class="modal-close" (click)="close.emit()">&times;</button>
        </div>
        <form (ngSubmit)="onSubmit()">
          <div class="modal-body">
            <div class="form-group">
              <label class="form-label">Nom du service *</label>
              <input type="text" class="form-control" [(ngModel)]="formData.name" name="name" required>
            </div>
            <div class="form-group">
              <label class="form-label">Description</label>
              <textarea class="form-control" [(ngModel)]="formData.description" name="description" rows="3"></textarea>
            </div>
            <div class="grid grid-2">
              <div class="form-group">
                <label class="form-label">Durée (minutes) *</label>
                <input type="number" class="form-control" [(ngModel)]="formData.durationMinutes" name="duration" min="1" required>
              </div>
              <div class="form-group">
                <label class="form-label">Points attribués *</label>
                <input type="number" class="form-control" [(ngModel)]="formData.pointsAwarded" name="points" min="0" required>
              </div>
            </div>
            <div class="form-group">
              <label class="form-check">
                <input type="checkbox" [(ngModel)]="formData.active" name="active">
                <span>Service actif</span>
              </label>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-outline" (click)="close.emit()">Annuler</button>
            <button type="submit" class="btn btn-primary">{{ service?.id ? 'Mettre à jour' : 'Créer' }}</button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .form-check { display: flex; align-items: center; gap: 0.5rem; cursor: pointer; }
    .form-check input { width: 18px; height: 18px; }
  `]
})
export class ServiceFormComponent implements OnInit {
  @Input() service: Service | null = null;
  @Output() save = new EventEmitter<Service>();
  @Output() close = new EventEmitter<void>();

  formData: Service = { name: '', description: '', durationMinutes: 30, pointsAwarded: 10, active: true };

  ngOnInit(): void {
    if (this.service) {
      this.formData = { ...this.service };
    }
  }

  onSubmit(): void {
    if (this.formData.name && this.formData.durationMinutes && this.formData.pointsAwarded !== undefined) {
      this.save.emit(this.formData);
    }
  }
}
