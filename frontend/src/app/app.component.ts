import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
    template: `
        <div class="app-container">
            <header class="header">
                <div class="header-content">
                    <a routerLink="/" class="logo">
                        <span class="logo-icon">🛒</span>
                        <span class="logo-text">ShopWise</span>
                    </a>

                    <button class="mobile-menu-toggle" (click)="toggleMobileMenu()">
                        <span class="material-icons">{{ mobileMenuOpen ? 'close' : 'menu' }}</span>
                    </button>

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

            <main class="main-content">
                <router-outlet></router-outlet>
            </main>

            <footer class="footer">
                <p>© 2025 ShopWise - Gestion de Commerce</p>
            </footer>
        </div>
    `,
    styles: [`
      .app-container {
        min-height: 100vh;
        display: flex;
        flex-direction: column;
        background: #0a0a0f;
      }

      .header {
        background: linear-gradient(135deg, #0d0d14 0%, #1a1a2e 100%);
        color: white;
        box-shadow: 0 2px 20px rgba(0, 255, 255, 0.1), 0 0 40px rgba(139, 92, 246, 0.05);
        position: sticky;
        top: 0;
        z-index: 100;
        border-bottom: 1px solid rgba(0, 255, 255, 0.1);
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
        color: #00ffff;
        font-weight: 700;
        font-size: 1.25rem;
        text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
      }

      .logo-icon {
        font-size: 1.5rem;
        filter: drop-shadow(0 0 8px rgba(0, 255, 255, 0.6));
      }

      .mobile-menu-toggle {
        display: none;
        background: none;
        border: none;
        color: #00ffff;
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
        color: rgba(255, 255, 255, 0.7);
        text-decoration: none;
        border-radius: 8px;
        transition: all 0.3s ease;
        font-weight: 500;
        border: 1px solid transparent;
      }

      .nav a:hover {
        background: rgba(139, 92, 246, 0.15);
        color: #a78bfa;
        border-color: rgba(139, 92, 246, 0.3);
        text-shadow: 0 0 8px rgba(139, 92, 246, 0.5);
      }

      .nav a.active {
        background: linear-gradient(135deg, rgba(0, 255, 255, 0.1) 0%, rgba(139, 92, 246, 0.1) 100%);
        color: #00ffff;
        border-color: rgba(0, 255, 255, 0.3);
        box-shadow: 0 0 15px rgba(0, 255, 255, 0.2);
        text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
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
        background: linear-gradient(135deg, #0d0d14 0%, #1a1a2e 100%);
        color: rgba(255, 255, 255, 0.5);
        text-align: center;
        padding: 1.5rem;
        font-size: 0.875rem;
        border-top: 1px solid rgba(0, 255, 255, 0.1);
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
          background: #0d0d14;
          flex-direction: column;
          padding: 1rem;
          gap: 0.5rem;
          transform: translateY(-100%);
          opacity: 0;
          visibility: hidden;
          transition: all 0.3s ease;
          border-bottom: 1px solid rgba(0, 255, 255, 0.1);
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