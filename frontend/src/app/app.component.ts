import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="app-container">
      <!-- Header -->
      <header class="header">
        <div class="header-content">
          <a routerLink="/" class="logo">
            <span class="logo-icon">🛒</span>
            <span class="logo-text">ShopWise</span>
          </a>
          
          <!-- Mobile Menu Toggle -->
          <button class="mobile-menu-toggle" (click)="toggleMobileMenu()">
            <span class="material-icons">{{ mobileMenuOpen ? 'close' : 'menu' }}</span>
          </button>
          
          <!-- Navigation -->
          <nav class="nav" [class.nav-open]="mobileMenuOpen">
            <a routerLink="/clients" routerLinkActive="active" (click)="closeMobileMenu()">
              <span class="material-icons">people</span>
              <span>Clients</span>
            </a>
            <a routerLink="/appointments" routerLinkActive="active" (click)="closeMobileMenu()">
              <span class="material-icons">calendar_today</span>
              <span>Rendez-vous</span>
            </a>
            <a routerLink="/services" routerLinkActive="active" (click)="closeMobileMenu()">
              <span class="material-icons">miscellaneous_services</span>
              <span>Services</span>
            </a>
            <a routerLink="/loyalty" routerLinkActive="active" (click)="closeMobileMenu()">
              <span class="material-icons">star</span>
              <span>Fidélité</span>
            </a>
          </nav>
        </div>
      </header>

      <!-- Main Content -->
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>

      <!-- Footer -->
      <footer class="footer">
        <p>&copy; 2025 ShopWise - Gestion de Commerce</p>
      </footer>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }

    .header {
      background: linear-gradient(135deg, #1E3A5F 0%, #2C3E50 100%);
      color: white;
      padding: 0 1rem;
      position: sticky;
      top: 0;
      z-index: 100;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 64px;
    }

    .logo {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      text-decoration: none;
      color: white;
      font-weight: 700;
      font-size: 1.25rem;
    }

    .logo-icon {
      font-size: 1.5rem;
    }

    .mobile-menu-toggle {
      display: none;
      background: none;
      border: none;
      color: white;
      cursor: pointer;
      padding: 0.5rem;

      @media (max-width: 768px) {
        display: block;
      }
    }

    .nav {
      display: flex;
      gap: 0.5rem;

      a {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem 1rem;
        color: rgba(255, 255, 255, 0.8);
        text-decoration: none;
        border-radius: 8px;
        transition: all 0.2s ease;
        font-size: 0.875rem;

        &:hover {
          background: rgba(255, 255, 255, 0.1);
          color: white;
        }

        &.active {
          background: rgba(255, 255, 255, 0.2);
          color: white;
        }

        .material-icons {
          font-size: 1.25rem;
        }
      }

      @media (max-width: 768px) {
        display: none;
        position: absolute;
        top: 64px;
        left: 0;
        right: 0;
        flex-direction: column;
        background: #1E3A5F;
        padding: 1rem;
        gap: 0;

        &.nav-open {
          display: flex;
        }

        a {
          padding: 1rem;
          border-radius: 0;
          border-bottom: 1px solid rgba(255, 255, 255, 0.1);

          &:last-child {
            border-bottom: none;
          }
        }
      }
    }

    .main-content {
      flex: 1;
      padding: 2rem 1rem;
      max-width: 1200px;
      margin: 0 auto;
      width: 100%;

      @media (max-width: 768px) {
        padding: 1rem;
      }
    }

    .footer {
      background: #2C3E50;
      color: rgba(255, 255, 255, 0.7);
      text-align: center;
      padding: 1.5rem;
      font-size: 0.875rem;
    }
  `]
})
export class AppComponent {
  mobileMenuOpen = false;

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  closeMobileMenu(): void {
    this.mobileMenuOpen = false;
  }
}
            </a>
            <a routerLink="/loyalty" routerLinkActive="active" (click)="closeMobileMenu()">
              <span class="material-icons">stars</span>
              <span>Fidélité</span>
            </a>
          </nav>
        </div>
      </header>

      <!-- Main Content -->
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>

      <!-- Footer -->
      <footer class="footer">
        <p>&copy; 2025 ShopWise - Gestion de Commerce</p>
      </footer>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }

    .header {
      background: linear-gradient(135deg, #1E3A5F 0%, #2C5282 100%);
      color: white;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
      position: sticky;
      top: 0;
      z-index: 100;
    }

    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 1rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 64px;
    }

    .logo {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      text-decoration: none;
      color: white;
      font-weight: 700;
      font-size: 1.25rem;
    }

    .logo-icon {
      font-size: 1.5rem;
    }

    .mobile-menu-toggle {
      display: none;
      background: none;
      border: none;
      color: white;
      cursor: pointer;
      padding: 0.5rem;
    }

    .nav {
      display: flex;
      gap: 0.5rem;
    }

    .nav a {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.5rem 1rem;
      color: rgba(255, 255, 255, 0.8);
      text-decoration: none;
      border-radius: 8px;
      transition: all 0.2s ease;
      font-weight: 500;
    }

    .nav a:hover {
      background: rgba(255, 255, 255, 0.1);
      color: white;
    }

    .nav a.active {
      background: rgba(255, 255, 255, 0.2);
      color: white;
    }

    .nav a .material-icons {
      font-size: 1.25rem;
    }

    .main-content {
      flex: 1;
      padding: 2rem 1rem;
      max-width: 1200px;
      margin: 0 auto;
      width: 100%;
    }

    .footer {
      background: #2C3E50;
      color: rgba(255, 255, 255, 0.7);
      text-align: center;
      padding: 1.5rem;
      font-size: 0.875rem;
    }

    @media (max-width: 768px) {
      .mobile-menu-toggle {
        display: block;
      }

      .nav {
        position: fixed;
        top: 64px;
        left: 0;
        right: 0;
        background: #1E3A5F;
        flex-direction: column;
        padding: 1rem;
        gap: 0.5rem;
        transform: translateY(-100%);
        opacity: 0;
        visibility: hidden;
        transition: all 0.3s ease;
      }

      .nav.nav-open {
        transform: translateY(0);
        opacity: 1;
        visibility: visible;
      }

      .nav a {
        padding: 1rem;
      }
    }
  `]
})
export class AppComponent {
  mobileMenuOpen = false;

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  closeMobileMenu(): void {
    this.mobileMenuOpen = false;
  }
}
