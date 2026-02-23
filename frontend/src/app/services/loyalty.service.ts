import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PointTransaction, LoyaltyBalance } from '../models/models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LoyaltyService {
  private apiUrl = `${environment.apiUrl}/loyalty`;

  constructor(private http: HttpClient) {}

  getClientTransactions(clientId: number): Observable<PointTransaction[]> {
    return this.http.get<PointTransaction[]>(`${this.apiUrl}/client/${clientId}/transactions`);
  }

  getClientBalance(clientId: number): Observable<LoyaltyBalance> {
    return this.http.get<LoyaltyBalance>(`${this.apiUrl}/client/${clientId}/balance`);
  }

  getTransactionById(id: number): Observable<PointTransaction> {
    return this.http.get<PointTransaction>(`${this.apiUrl}/transactions/${id}`);
  }

  redeemPoints(clientId: number, points: number, description?: string): Observable<PointTransaction> {
    let params = new HttpParams().set('points', points.toString());
    if (description) {
      params = params.set('description', description);
    }
    return this.http.post<PointTransaction>(`${this.apiUrl}/client/${clientId}/redeem`, null, { params });
  }

  adjustPoints(clientId: number, points: number, description?: string): Observable<PointTransaction> {
    let params = new HttpParams().set('points', points.toString());
    if (description) {
      params = params.set('description', description);
    }
    return this.http.post<PointTransaction>(`${this.apiUrl}/client/${clientId}/adjust`, null, { params });
  }
}
