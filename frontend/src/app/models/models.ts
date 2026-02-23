// Client
export interface Client {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  loyaltyPoints?: number;
  createdAt?: string;
  updatedAt?: string;
}

// Service
export interface Service {
  id?: number;
  name: string;
  description?: string;
  durationMinutes: number;
  pointsAwarded: number;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
}

// Appointment Status
export type AppointmentStatus = 'SCHEDULED' | 'COMPLETED' | 'CANCELLED';

// Appointment
export interface Appointment {
  id?: number;
  clientId: number;
  clientName?: string;
  clientEmail?: string;
  serviceId: number;
  serviceName?: string;
  serviceDuration?: number;
  servicePoints?: number;
  appointmentDate: string;
  appointmentTime: string;
  status: AppointmentStatus;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

// Transaction Type
export type TransactionType = 'EARNED' | 'REDEEMED' | 'ADJUSTMENT';

// Point Transaction
export interface PointTransaction {
  id?: number;
  clientId: number;
  clientName?: string;
  appointmentId?: number;
  points: number;
  transactionType: TransactionType;
  description?: string;
  createdAt?: string;
}

// Loyalty Balance
export interface LoyaltyBalance {
  clientId: number;
  balance: number;
  totalEarned: number;
  totalRedeemed: number;
}

// API Response Error
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  errors?: { [key: string]: string };
}
