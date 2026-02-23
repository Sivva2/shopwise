#!/usr/bin/env python3
"""
Génère le schéma de base de données ShopWise en PDF
"""

from reportlab.lib.pagesizes import A4, landscape
from reportlab.lib.units import cm
from reportlab.lib.colors import HexColor, white, black
from reportlab.pdfgen import canvas
from reportlab.lib.styles import getSampleStyleSheet
from reportlab.platypus import Paragraph

# Couleurs
PRIMARY_COLOR = HexColor('#1E3A5F')  # Bleu foncé
SECONDARY_COLOR = HexColor('#3498DB')  # Bleu clair
HEADER_BG = HexColor('#2C3E50')  # Fond header table
ROW_BG = HexColor('#ECF0F1')  # Fond lignes
FK_COLOR = HexColor('#E74C3C')  # Rouge pour FK
PK_COLOR = HexColor('#27AE60')  # Vert pour PK

def draw_table(c, x, y, table_name, columns, width=6*cm, row_height=0.5*cm):
    """
    Dessine une table avec ses colonnes
    columns = [(nom, type, contraintes), ...]
    """
    header_height = 0.7*cm
    total_height = header_height + len(columns) * row_height
    
    # Ombre
    c.setFillColor(HexColor('#BDC3C7'))
    c.roundRect(x + 0.1*cm, y - total_height - 0.1*cm, width, total_height, 5, fill=1, stroke=0)
    
    # Fond de la table
    c.setFillColor(white)
    c.setStrokeColor(PRIMARY_COLOR)
    c.setLineWidth(2)
    c.roundRect(x, y - total_height, width, total_height, 5, fill=1, stroke=1)
    
    # Header
    c.setFillColor(HEADER_BG)
    c.roundRect(x, y - header_height, width, header_height, 5, fill=1, stroke=0)
    # Rectangle pour couvrir les coins arrondis du bas du header
    c.rect(x, y - header_height, width, header_height/2, fill=1, stroke=0)
    
    # Nom de la table
    c.setFillColor(white)
    c.setFont("Helvetica-Bold", 11)
    c.drawCentredString(x + width/2, y - header_height + 0.2*cm, table_name)
    
    # Ligne sous le header
    c.setStrokeColor(PRIMARY_COLOR)
    c.setLineWidth(1)
    c.line(x, y - header_height, x + width, y - header_height)
    
    # Colonnes
    current_y = y - header_height - row_height
    for i, (col_name, col_type, constraints) in enumerate(columns):
        # Fond alterné
        if i % 2 == 0:
            c.setFillColor(ROW_BG)
            c.rect(x, current_y, width, row_height, fill=1, stroke=0)
        
        # Icône PK/FK
        icon_x = x + 0.2*cm
        c.setFont("Helvetica-Bold", 7)
        if 'PK' in constraints:
            c.setFillColor(PK_COLOR)
            c.drawString(icon_x, current_y + 0.15*cm, "PK")
            icon_x += 0.6*cm
        if 'FK' in constraints:
            c.setFillColor(FK_COLOR)
            c.drawString(icon_x, current_y + 0.15*cm, "FK")
            icon_x += 0.6*cm
        
        # Nom de colonne
        c.setFillColor(black)
        c.setFont("Helvetica", 8)
        text_x = x + 0.3*cm if 'PK' not in constraints and 'FK' not in constraints else icon_x + 0.1*cm
        c.drawString(text_x, current_y + 0.15*cm, col_name)
        
        # Type
        c.setFillColor(HexColor('#7F8C8D'))
        c.setFont("Helvetica-Oblique", 7)
        c.drawRightString(x + width - 0.2*cm, current_y + 0.15*cm, col_type)
        
        current_y -= row_height
    
    return y - total_height

def draw_relation(c, x1, y1, x2, y2, label=""):
    """Dessine une relation entre deux tables"""
    c.setStrokeColor(FK_COLOR)
    c.setLineWidth(1.5)
    
    # Ligne avec courbure
    mid_x = (x1 + x2) / 2
    
    path = c.beginPath()
    path.moveTo(x1, y1)
    path.curveTo(mid_x, y1, mid_x, y2, x2, y2)
    c.drawPath(path, stroke=1, fill=0)
    
    # Flèche (losange pour représenter la relation)
    arrow_size = 0.15*cm
    c.setFillColor(FK_COLOR)
    path = c.beginPath()
    path.moveTo(x2, y2)
    path.lineTo(x2 - arrow_size, y2 + arrow_size)
    path.lineTo(x2 - 2*arrow_size, y2)
    path.lineTo(x2 - arrow_size, y2 - arrow_size)
    path.close()
    c.drawPath(path, fill=1, stroke=0)

def create_schema_pdf(filename):
    """Crée le PDF du schéma de base de données"""
    c = canvas.Canvas(filename, pagesize=landscape(A4))
    width, height = landscape(A4)
    
    # Titre
    c.setFont("Helvetica-Bold", 24)
    c.setFillColor(PRIMARY_COLOR)
    c.drawCentredString(width/2, height - 1.5*cm, "ShopWise - Schéma de Base de Données")
    
    # Sous-titre
    c.setFont("Helvetica", 12)
    c.setFillColor(HexColor('#7F8C8D'))
    c.drawCentredString(width/2, height - 2.2*cm, "Modules : Clients, Fidélisation, Rendez-vous")
    
    # Ligne décorative
    c.setStrokeColor(SECONDARY_COLOR)
    c.setLineWidth(2)
    c.line(2*cm, height - 2.6*cm, width - 2*cm, height - 2.6*cm)
    
    # === TABLE CLIENT ===
    client_columns = [
        ("id", "BIGINT", "PK"),
        ("first_name", "VARCHAR(100)", "NOT NULL"),
        ("last_name", "VARCHAR(100)", "NOT NULL"),
        ("email", "VARCHAR(255)", "UNIQUE"),
        ("phone", "VARCHAR(20)", ""),
        ("loyalty_points", "INT", "DEFAULT 0"),
        ("created_at", "TIMESTAMP", ""),
        ("updated_at", "TIMESTAMP", ""),
    ]
    client_x = 2*cm
    client_y = height - 4*cm
    client_bottom = draw_table(c, client_x, client_y, "CLIENT", client_columns, width=6.5*cm)
    
    # === TABLE SERVICE ===
    service_columns = [
        ("id", "BIGINT", "PK"),
        ("name", "VARCHAR(150)", "NOT NULL"),
        ("description", "TEXT", ""),
        ("duration_minutes", "INT", "NOT NULL"),
        ("points_awarded", "INT", "NOT NULL"),
        ("active", "BOOLEAN", "DEFAULT TRUE"),
        ("created_at", "TIMESTAMP", ""),
        ("updated_at", "TIMESTAMP", ""),
    ]
    service_x = width - 8.5*cm
    service_y = height - 4*cm
    service_bottom = draw_table(c, service_x, service_y, "SERVICE", service_columns, width=6.5*cm)
    
    # === TABLE APPOINTMENT ===
    appointment_columns = [
        ("id", "BIGINT", "PK"),
        ("client_id", "BIGINT", "FK"),
        ("service_id", "BIGINT", "FK"),
        ("appointment_date", "DATE", "NOT NULL"),
        ("appointment_time", "TIME", "NOT NULL"),
        ("status", "ENUM", "SCHEDULED/..."),
        ("notes", "TEXT", ""),
        ("created_at", "TIMESTAMP", ""),
        ("updated_at", "TIMESTAMP", ""),
    ]
    appointment_x = (width - 7*cm) / 2
    appointment_y = height - 4*cm
    appointment_bottom = draw_table(c, appointment_x, appointment_y, "APPOINTMENT", appointment_columns, width=7*cm)
    
    # === TABLE POINT_TRANSACTION ===
    transaction_columns = [
        ("id", "BIGINT", "PK"),
        ("client_id", "BIGINT", "FK"),
        ("appointment_id", "BIGINT", "FK"),
        ("points", "INT", "NOT NULL"),
        ("transaction_type", "ENUM", "EARNED/..."),
        ("description", "VARCHAR(255)", ""),
        ("created_at", "TIMESTAMP", ""),
    ]
    transaction_x = 2*cm
    transaction_y = client_bottom - 1.5*cm
    transaction_bottom = draw_table(c, transaction_x, transaction_y, "POINT_TRANSACTION", transaction_columns, width=6.5*cm)
    
    # === RELATIONS ===
    # Client -> Appointment
    draw_relation(c, 
                  client_x + 6.5*cm, client_y - 1.5*cm,
                  appointment_x, appointment_y - 1*cm)
    
    # Service -> Appointment
    draw_relation(c,
                  service_x, service_y - 1.5*cm,
                  appointment_x + 7*cm, appointment_y - 1.5*cm)
    
    # Client -> Point_Transaction
    draw_relation(c,
                  client_x + 3*cm, client_bottom,
                  transaction_x + 3*cm, transaction_y)
    
    # Appointment -> Point_Transaction
    draw_relation(c,
                  appointment_x + 2*cm, appointment_bottom,
                  transaction_x + 6.5*cm, transaction_y - 1.5*cm)
    
    # === LÉGENDE ===
    legend_x = width - 8*cm
    legend_y = 4*cm
    
    c.setFont("Helvetica-Bold", 10)
    c.setFillColor(PRIMARY_COLOR)
    c.drawString(legend_x, legend_y, "Légende")
    
    c.setLineWidth(1)
    c.setStrokeColor(PRIMARY_COLOR)
    c.line(legend_x, legend_y - 0.3*cm, legend_x + 3*cm, legend_y - 0.3*cm)
    
    # PK
    c.setFont("Helvetica-Bold", 8)
    c.setFillColor(PK_COLOR)
    c.drawString(legend_x, legend_y - 0.8*cm, "PK")
    c.setFillColor(black)
    c.setFont("Helvetica", 8)
    c.drawString(legend_x + 0.8*cm, legend_y - 0.8*cm, "= Clé Primaire")
    
    # FK
    c.setFont("Helvetica-Bold", 8)
    c.setFillColor(FK_COLOR)
    c.drawString(legend_x, legend_y - 1.4*cm, "FK")
    c.setFillColor(black)
    c.setFont("Helvetica", 8)
    c.drawString(legend_x + 0.8*cm, legend_y - 1.4*cm, "= Clé Étrangère")
    
    # Relation
    c.setStrokeColor(FK_COLOR)
    c.setLineWidth(1.5)
    c.line(legend_x, legend_y - 2*cm, legend_x + 0.6*cm, legend_y - 2*cm)
    c.setFillColor(black)
    c.drawString(legend_x + 0.8*cm, legend_y - 2.1*cm, "= Relation")
    
    # === DESCRIPTION DES TABLES ===
    desc_y = 3.5*cm
    c.setFont("Helvetica-Bold", 10)
    c.setFillColor(PRIMARY_COLOR)
    c.drawString(2*cm, desc_y, "Description des entités :")
    
    c.setFont("Helvetica", 8)
    c.setFillColor(black)
    descriptions = [
        "• CLIENT : Fiche client avec informations personnelles et solde de points fidélité",
        "• SERVICE : Services proposés par le commerce (durée, points attribués)",
        "• APPOINTMENT : Rendez-vous liant un client à un service avec statut (SCHEDULED, COMPLETED, CANCELLED)",
        "• POINT_TRANSACTION : Historique des mouvements de points (EARNED, REDEEMED, ADJUSTMENT)",
    ]
    
    for i, desc in enumerate(descriptions):
        c.drawString(2*cm, desc_y - (i+1)*0.5*cm, desc)
    
    # === CARDINALITÉS ===
    card_x = 12*cm
    c.setFont("Helvetica-Bold", 10)
    c.setFillColor(PRIMARY_COLOR)
    c.drawString(card_x, desc_y, "Cardinalités :")
    
    c.setFont("Helvetica", 8)
    c.setFillColor(black)
    cardinalities = [
        "• CLIENT (1) --- (0,N) APPOINTMENT : Un client peut avoir plusieurs RDV",
        "• SERVICE (1) --- (0,N) APPOINTMENT : Un service peut être utilisé dans plusieurs RDV",
        "• CLIENT (1) --- (0,N) POINT_TRANSACTION : Un client a un historique de transactions",
        "• APPOINTMENT (1) --- (0,1) POINT_TRANSACTION : Un RDV peut générer une transaction",
    ]
    
    for i, card in enumerate(cardinalities):
        c.drawString(card_x, desc_y - (i+1)*0.5*cm, card)
    
    # Footer
    c.setFont("Helvetica-Oblique", 8)
    c.setFillColor(HexColor('#95A5A6'))
    c.drawCentredString(width/2, 0.8*cm, "ShopWise - Projet de digitalisation des commerces de proximité - RNCP Niveau 7")
    
    c.save()
    print(f"PDF créé : {filename}")

if __name__ == "__main__":
    create_schema_pdf("/home/claude/shopwise/bdd/schema_base_de_donnees.pdf")
