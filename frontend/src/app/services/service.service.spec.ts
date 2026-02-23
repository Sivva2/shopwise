import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ServiceService } from './service.service';
import { Service } from '../models/models';
import { environment } from '../../environments/environment';

describe('ServiceService', () => {
  let service: ServiceService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/services`;

  const mockService: Service = {
    id: 1,
    name: 'Consultation',
    description: 'Consultation standard',
    durationMinutes: 30,
    pointsAwarded: 10,
    active: true
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ServiceService]
    });
    service = TestBed.inject(ServiceService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAllServices', () => {
    it('should return all services', () => {
      service.getAllServices().subscribe(services => {
        expect(services.length).toBe(1);
        expect(services[0].name).toBe('Consultation');
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush([mockService]);
    });
  });

  describe('getActiveServices', () => {
    it('should return only active services', () => {
      service.getActiveServices().subscribe(services => {
        expect(services.length).toBe(1);
        expect(services[0].active).toBe(true);
      });

      const req = httpMock.expectOne(`${apiUrl}/active`);
      expect(req.request.method).toBe('GET');
      req.flush([mockService]);
    });
  });

  describe('getServiceById', () => {
    it('should return a service by id', () => {
      service.getServiceById(1).subscribe(svc => {
        expect(svc.id).toBe(1);
        expect(svc.durationMinutes).toBe(30);
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockService);
    });
  });

  describe('createService', () => {
    it('should create a new service', () => {
      const newService: Service = {
        name: 'Premium',
        durationMinutes: 60,
        pointsAwarded: 25,
        active: true
      };

      service.createService(newService).subscribe(svc => {
        expect(svc.id).toBe(2);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      req.flush({ ...newService, id: 2 });
    });
  });

  describe('updateService', () => {
    it('should update a service', () => {
      const updated = { ...mockService, pointsAwarded: 15 };

      service.updateService(1, updated).subscribe(svc => {
        expect(svc.pointsAwarded).toBe(15);
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      expect(req.request.method).toBe('PUT');
      req.flush(updated);
    });
  });

  describe('deleteService', () => {
    it('should delete a service', () => {
      service.deleteService(1).subscribe(() => {
        expect(true).toBeTruthy();
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
