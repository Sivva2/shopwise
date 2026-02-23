import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LoyaltyService } from './loyalty.service';
import { PointTransaction, LoyaltyBalance } from '../models/models';
import { environment } from '../../environments/environment';

describe('LoyaltyService', () => {
  let service: LoyaltyService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/loyalty`;

  const mockTransaction: PointTransaction = {
    id: 1,
    clientId: 1,
    clientName: 'Marie Dupont',
    points: 10,
    transactionType: 'EARNED',
    description: 'Points gagnés'
  };

  const mockBalance: LoyaltyBalance = {
    clientId: 1,
    balance: 100,
    totalEarned: 150,
    totalRedeemed: 50
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [LoyaltyService]
    });
    service = TestBed.inject(LoyaltyService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getClientTransactions', () => {
    it('should return client transactions', () => {
      service.getClientTransactions(1).subscribe(transactions => {
        expect(transactions.length).toBe(1);
        expect(transactions[0].points).toBe(10);
      });

      const req = httpMock.expectOne(`${apiUrl}/client/1/transactions`);
      expect(req.request.method).toBe('GET');
      req.flush([mockTransaction]);
    });
  });

  describe('getClientBalance', () => {
    it('should return client balance', () => {
      service.getClientBalance(1).subscribe(balance => {
        expect(balance.balance).toBe(100);
        expect(balance.totalEarned).toBe(150);
      });

      const req = httpMock.expectOne(`${apiUrl}/client/1/balance`);
      expect(req.request.method).toBe('GET');
      req.flush(mockBalance);
    });
  });

  describe('getTransactionById', () => {
    it('should return a transaction by id', () => {
      service.getTransactionById(1).subscribe(tx => {
        expect(tx.id).toBe(1);
        expect(tx.transactionType).toBe('EARNED');
      });

      const req = httpMock.expectOne(`${apiUrl}/transactions/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockTransaction);
    });
  });

  describe('redeemPoints', () => {
    it('should redeem points without description', () => {
      service.redeemPoints(1, 50).subscribe(tx => {
        expect(tx.points).toBe(-50);
      });

      const req = httpMock.expectOne(`${apiUrl}/client/1/redeem?points=50`);
      expect(req.request.method).toBe('POST');
      req.flush({ ...mockTransaction, points: -50, transactionType: 'REDEEMED' });
    });

    it('should redeem points with description', () => {
      service.redeemPoints(1, 50, 'Réduction').subscribe(tx => {
        expect(tx.description).toBe('Réduction');
      });

      const req = httpMock.expectOne(`${apiUrl}/client/1/redeem?points=50&description=R%C3%A9duction`);
      expect(req.request.method).toBe('POST');
      req.flush({ ...mockTransaction, points: -50, description: 'Réduction' });
    });
  });

  describe('adjustPoints', () => {
    it('should adjust points positively', () => {
      service.adjustPoints(1, 25, 'Bonus').subscribe(tx => {
        expect(tx.points).toBe(25);
      });

      const req = httpMock.expectOne(`${apiUrl}/client/1/adjust?points=25&description=Bonus`);
      expect(req.request.method).toBe('POST');
      req.flush({ ...mockTransaction, points: 25, transactionType: 'ADJUSTMENT' });
    });

    it('should adjust points negatively', () => {
      service.adjustPoints(1, -10).subscribe(tx => {
        expect(tx.points).toBe(-10);
      });

      const req = httpMock.expectOne(`${apiUrl}/client/1/adjust?points=-10`);
      expect(req.request.method).toBe('POST');
      req.flush({ ...mockTransaction, points: -10, transactionType: 'ADJUSTMENT' });
    });
  });
});
