import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { ClientFormComponent } from './client-form.component';
import { Client } from '../../../models/models';

describe('ClientFormComponent', () => {
  let component: ClientFormComponent;
  let fixture: ComponentFixture<ClientFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormsModule, ClientFormComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ClientFormComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should initialize with empty form for new client', () => {
    component.client = null;
    fixture.detectChanges();
    
    expect(component.formData.firstName).toBe('');
    expect(component.formData.lastName).toBe('');
    expect(component.formData.email).toBe('');
  });

  it('should initialize with client data for edit', () => {
    const client: Client = {
      id: 1,
      firstName: 'Marie',
      lastName: 'Dupont',
      email: 'marie@email.com',
      phone: '0612345678'
    };
    component.client = client;
    fixture.detectChanges();
    component.ngOnInit();
    
    expect(component.formData.firstName).toBe('Marie');
    expect(component.formData.lastName).toBe('Dupont');
    expect(component.formData.email).toBe('marie@email.com');
  });

  it('should emit save event on valid submit', () => {
    fixture.detectChanges();
    spyOn(component.save, 'emit');
    
    component.formData = {
      firstName: 'Sophie',
      lastName: 'Bernard',
      email: 'sophie@email.com'
    };
    
    component.onSubmit();
    
    expect(component.save.emit).toHaveBeenCalledWith(component.formData);
  });

  it('should not emit save event on invalid submit', () => {
    fixture.detectChanges();
    spyOn(component.save, 'emit');
    
    component.formData = {
      firstName: '',
      lastName: 'Test',
      email: 'test@email.com'
    };
    
    component.onSubmit();
    
    expect(component.save.emit).not.toHaveBeenCalled();
  });

  it('should emit close event', () => {
    fixture.detectChanges();
    spyOn(component.close, 'emit');
    
    component.close.emit();
    
    expect(component.close.emit).toHaveBeenCalled();
  });
});
