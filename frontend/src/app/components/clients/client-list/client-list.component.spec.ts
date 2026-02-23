import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { ClientListComponent } from './client-list.component';
import { ClientService } from '../../../services/client.service';
import { Client } from '../../../models/models';

describe('ClientListComponent', () => {
  let component: ClientListComponent;
  let fixture: ComponentFixture<ClientListComponent>;
  let clientService: jasmine.SpyObj<ClientService>;

  const mockClients: Client[] = [
    { id: 1, firstName: 'Marie', lastName: 'Dupont', email: 'marie@email.com', loyaltyPoints: 100 },
    { id: 2, firstName: 'Jean', lastName: 'Martin', email: 'jean@email.com', loyaltyPoints: 50 }
  ];

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('ClientService', [
      'getAllClients', 'createClient', 'updateClient', 'deleteClient'
    ]);

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule, ClientListComponent],
      providers: [{ provide: ClientService, useValue: spy }]
    }).compileComponents();

    clientService = TestBed.inject(ClientService) as jasmine.SpyObj<ClientService>;
    clientService.getAllClients.and.returnValue(of(mockClients));

    fixture = TestBed.createComponent(ClientListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load clients on init', () => {
    expect(component.clients.length).toBe(2);
    expect(component.filteredClients.length).toBe(2);
  });

  it('should filter clients by search query', () => {
    component.searchQuery = 'marie';
    component.filterClients();
    expect(component.filteredClients.length).toBe(1);
    expect(component.filteredClients[0].firstName).toBe('Marie');
  });

  it('should filter clients by last name', () => {
    component.searchQuery = 'martin';
    component.filterClients();
    expect(component.filteredClients.length).toBe(1);
    expect(component.filteredClients[0].lastName).toBe('Martin');
  });

  it('should filter clients by email', () => {
    component.searchQuery = 'jean@';
    component.filterClients();
    expect(component.filteredClients.length).toBe(1);
  });

  it('should return correct initials', () => {
    const client = mockClients[0];
    expect(component.getInitials(client)).toBe('MD');
  });

  it('should calculate total points', () => {
    expect(component.getTotalPoints()).toBe(150);
  });

  it('should calculate average points', () => {
    expect(component.getAveragePoints()).toBe(75);
  });

  it('should return zero average when no clients', () => {
    component.clients = [];
    expect(component.getAveragePoints()).toBe(0);
  });

  it('should get top client', () => {
    const topClient = component.getTopClient();
    expect(topClient?.firstName).toBe('Marie');
    expect(topClient?.loyaltyPoints).toBe(100);
  });

  it('should return null for top client when empty', () => {
    component.clients = [];
    expect(component.getTopClient()).toBeNull();
  });

  it('should open create modal', () => {
    component.openCreateModal();
    expect(component.showModal).toBeTrue();
    expect(component.selectedClient).toBeNull();
  });

  it('should open edit modal with client data', () => {
    const client = mockClients[0];
    component.openEditModal(client);
    expect(component.showModal).toBeTrue();
    expect(component.selectedClient).toEqual(client);
  });

  it('should close modal', () => {
    component.showModal = true;
    component.selectedClient = mockClients[0];
    component.closeModal();
    expect(component.showModal).toBeFalse();
    expect(component.selectedClient).toBeNull();
  });

  it('should create client on save', fakeAsync(() => {
    const newClient: Client = { firstName: 'Sophie', lastName: 'Bernard', email: 'sophie@email.com' };
    clientService.createClient.and.returnValue(of({ ...newClient, id: 3 }));
    clientService.getAllClients.and.returnValue(of([...mockClients, { ...newClient, id: 3 }]));

    component.onSave(newClient);
    tick();

    expect(clientService.createClient).toHaveBeenCalledWith(newClient);
  }));

  it('should update client on save', fakeAsync(() => {
    const updatedClient = { ...mockClients[0], firstName: 'Marie-Claire' };
    clientService.updateClient.and.returnValue(of(updatedClient));
    clientService.getAllClients.and.returnValue(of([updatedClient, mockClients[1]]));

    component.onSave(updatedClient);
    tick();

    expect(clientService.updateClient).toHaveBeenCalledWith(1, updatedClient);
  }));

  it('should confirm delete', () => {
    const client = mockClients[0];
    component.confirmDelete(client);
    expect(component.showDeleteConfirm).toBeTrue();
    expect(component.clientToDelete).toEqual(client);
  });

  it('should delete client', fakeAsync(() => {
    component.clientToDelete = mockClients[0];
    clientService.deleteClient.and.returnValue(of(void 0));
    clientService.getAllClients.and.returnValue(of([mockClients[1]]));

    component.deleteClient();
    tick();

    expect(clientService.deleteClient).toHaveBeenCalledWith(1);
    expect(component.showDeleteConfirm).toBeFalse();
  }));

  it('should handle load error', fakeAsync(() => {
    clientService.getAllClients.and.returnValue(throwError(() => new Error('Error')));
    
    component.loadClients();
    tick();

    expect(component.error).toBe('Erreur lors du chargement');
    expect(component.loading).toBeFalse();
  }));
});
