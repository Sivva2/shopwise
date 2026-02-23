import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AppointmentService } from './appointment.service';
import { Appointment } from '../models/models';
import { environment } from '../../environments/environment';

describe('AppointmentService', () => {
  let service: AppointmentService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/appointments`;

  const mockAppointment: Appointment = {
    id: 1,
    clientId: 1,
    clientName: 'Marie Dupont',
    serviceId: 1,
    serviceName: 'Consultation',
    appointmentDate: '2025-03-01',
    appointmentTime: '10:00',
    status: 'SCHEDULED'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AppointmentService]
    });
    service = TestBed.inject(AppointmentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAllAppointments', () => {
    it('should return all appointments without filters', () => {
      service.getAllAppointments().subscribe(appointments => {
        expect(appointments.length).toBe(1);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush([mockAppointment]);
    });

    it('should return filtered appointments', () => {
      service.getAllAppointments({ clientId: 1, status: 'SCHEDULED' }).subscribe(appointments => {
        expect(appointments.length).toBe(1);
      });

      const req = httpMock.expectOne(`${apiUrl}?clientId=1&status=SCHEDULED`);
      expect(req.request.method).toBe('GET');
      req.flush([mockAppointment]);
    });
  });

  describe('getAppointmentById', () => {
    it('should return an appointment by id', () => {
      service.getAppointmentById(1).subscribe(appointment => {
        expect(appointment.id).toBe(1);
        expect(appointment.status).toBe('SCHEDULED');
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockAppointment);
    });
  });

  describe('getAppointmentsByClient', () => {
    it('should return appointments for a client', () => {
      service.getAppointmentsByClient(1).subscribe(appointments => {
        expect(appointments.length).toBe(1);
      });

      const req = httpMock.expectOne(`${apiUrl}/client/1`);
      expect(req.request.method).toBe('GET');
      req.flush([mockAppointment]);
    });
  });

  describe('createAppointment', () => {
    it('should create a new appointment', () => {
      const newAppointment: Appointment = {
        clientId: 1,
        serviceId: 1,
        appointmentDate: '2025-03-02',
        appointmentTime: '14:00',
        status: 'SCHEDULED'
      };

      service.createAppointment(newAppointment).subscribe(appointment => {
        expect(appointment.id).toBe(2);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      req.flush({ ...newAppointment, id: 2 });
    });
  });

  describe('updateAppointment', () => {
    it('should update an appointment', () => {
      const updatedAppointment = { ...mockAppointment, notes: 'Updated' };

      service.updateAppointment(1, updatedAppointment).subscribe(appointment => {
        expect(appointment.notes).toBe('Updated');
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      expect(req.request.method).toBe('PUT');
      req.flush(updatedAppointment);
    });
  });

  describe('updateStatus', () => {
    it('should update appointment status', () => {
      service.updateStatus(1, 'COMPLETED').subscribe(appointment => {
        expect(appointment.status).toBe('COMPLETED');
      });

      const req = httpMock.expectOne(`${apiUrl}/1/status`);
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual({ status: 'COMPLETED' });
      req.flush({ ...mockAppointment, status: 'COMPLETED' });
    });
  });

  describe('deleteAppointment', () => {
    it('should delete an appointment', () => {
      service.deleteAppointment(1).subscribe(() => {
        expect(true).toBeTruthy();
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
