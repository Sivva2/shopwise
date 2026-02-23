import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Appointment, Client, Service } from '../../../models/models';

@Component({
  selector: 'app-appointment-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-backdrop" (click)="close.emit()">
      <div class="modal" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3 class="modal-title">{{ appointment?.id ? 'Modifier' : 'Nouveau' }} Rendez-vous</h3>
          <button class="modal-close" (click)="close.emit()">&times;</button>
        </div>
        <form (ngSubmit)="onSubmit()">
          <div class="modal-body">
            <div class="form-group">
              <label class="form-label">Client *</label>
              <select class="form-control" [(ngModel)]="formData.clientId" name="clientId" required>
                <option [ngValue]="null" disabled>Sélectionner un client</option>
                <option *ngFor="let c of clients" [ngValue]="c.id">{{ c.firstName }} {{ c.lastName }}</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">Service *</label>
              <select class="form-control" [(ngModel)]="formData.serviceId" name="serviceId" required>
                <option [ngValue]="null" disabled>Sélectionner un service</option>
                <option *ngFor="let s of services" [ngValue]="s.id">{{ s.name }} ({{ s.durationMinutes }} min - {{ s.pointsAwarded }} pts)</option>
              </select>
            </div>
            <div class="grid grid-2">
              <div class="form-group">
                <label class="form-label">Date *</label>
                <input type="date" class="form-control" [(ngModel)]="formData.appointmentDate" name="date" required>
              </div>
              <div class="form-group">
                <label class="form-label">Heure *</label>
                <input type="time" class="form-control" [(ngModel)]="formData.appointmentTime" name="time" required>
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">Notes</label>
              <textarea class="form-control" [(ngModel)]="formData.notes" name="notes" rows="3"></textarea>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-outline" (click)="close.emit()">Annuler</button>
            <button type="submit" class="btn btn-primary">{{ appointment?.id ? 'Mettre à jour' : 'Créer' }}</button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class AppointmentFormComponent implements OnInit {
  @Input() appointment: Appointment | null = null;
  @Input() clients: Client[] = [];
  @Input() services: Service[] = [];
  @Output() save = new EventEmitter<Appointment>();
  @Output() close = new EventEmitter<void>();

  formData: Partial<Appointment> = {
    clientId: 0,
    serviceId: 0,
    appointmentDate: '',
    appointmentTime: '',
    status: 'SCHEDULED',
    notes: ''
  };

  ngOnInit(): void {
    if (this.appointment) {
      this.formData = { ...this.appointment };
    } else {
      const today = new Date().toISOString().split('T')[0];
      this.formData.appointmentDate = today;
      this.formData.appointmentTime = '09:00';
    }
  }

  onSubmit(): void {
    if (this.formData.clientId && this.formData.serviceId && this.formData.appointmentDate && this.formData.appointmentTime) {
      this.save.emit(this.formData as Appointment);
    }
  }
}
