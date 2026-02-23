import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ClientService } from './client.service';
import { Client } from '../models/models';
import { environment } from '../../environments/environment';

describe('ClientService', () => {
  let service: ClientService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/clients`;

  const mockClient: Client = {
    id: 1,
    firstName: 'Marie',
    lastName: 'Dupont',
    email: 'marie@email.com',
    phone: '0612345678',
    loyaltyPoints: 100
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ClientService]
    });
    service = TestBed.inject(ClientService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAllClients', () => {
    it('should return all clients', () => {
      const mockClients: Client[] = [mockClient];

      service.getAllClients().subscribe(clients => {
        expect(clients.length).toBe(1);
        expect(clients[0].firstName).toBe('Marie');
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockClients);
    });
  });

  describe('getClientById', () => {
    it('should return a client by id', () => {
      service.getClientById(1).subscribe(client => {
        expect(client.id).toBe(1);
        expect(client.email).toBe('marie@email.com');
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockClient);
    });
  });

  describe('getClientByEmail', () => {
    it('should return a client by email', () => {
      service.getClientByEmail('marie@email.com').subscribe(client => {
        expect(client.email).toBe('marie@email.com');
      });

      const req = httpMock.expectOne(`${apiUrl}/email/marie@email.com`);
      expect(req.request.method).toBe('GET');
      req.flush(mockClient);
    });
  });

  describe('searchClients', () => {
    it('should search clients by query', () => {
      service.searchClients('dup').subscribe(clients => {
        expect(clients.length).toBe(1);
      });

      const req = httpMock.expectOne(`${apiUrl}/search?query=dup`);
      expect(req.request.method).toBe('GET');
      req.flush([mockClient]);
    });
  });

  describe('createClient', () => {
    it('should create a new client', () => {
      const newClient: Client = {
        firstName: 'Sophie',
        lastName: 'Bernard',
        email: 'sophie@email.com'
      };

      service.createClient(newClient).subscribe(client => {
        expect(client.id).toBe(2);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newClient);
      req.flush({ ...newClient, id: 2 });
    });
  });

  describe('updateClient', () => {
    it('should update an existing client', () => {
      const updatedClient = { ...mockClient, firstName: 'Marie-Claire' };

      service.updateClient(1, updatedClient).subscribe(client => {
        expect(client.firstName).toBe('Marie-Claire');
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      expect(req.request.method).toBe('PUT');
      req.flush(updatedClient);
    });
  });

  describe('deleteClient', () => {
    it('should delete a client', () => {
      service.deleteClient(1).subscribe(() => {
        expect(true).toBeTruthy();
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });
});
