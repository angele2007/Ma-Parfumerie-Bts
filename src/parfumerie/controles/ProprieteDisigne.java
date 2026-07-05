/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package parfumerie.controles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 *
 * @author bouka
 */
public class ProprieteDisigne extends javax.swing.JPanel {

    /**
     * Creates new form ProprieteDisigne
     */
    public ProprieteDisigne() {
        initComponents();
    }
    public static void appliquerCartePointillee(JPanel panel, Color fond) {

        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(220, 210));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Override du paint
        panel.setUI(new javax.swing.plaf.PanelUI() {
            @Override
            public void update(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond
                g2.setColor(fond);
                g2.fillRoundRect(0, 0, c.getWidth()-1, c.getHeight()-1, 14, 14);

                // Bordure pointillée FIXE
                g2.setColor(new Color(13, 107, 63, 60));
                float[] dash = {6f, 4f};
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND, 1f, dash, 0f));

                g2.drawRoundRect(1, 1, c.getWidth()-3, c.getHeight()-3, 14, 14);

                g2.dispose();
            }
        });
    }
    public static void ajouterHover(JPanel panel, Color hoverColor) {

        panel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(hoverColor);
                panel.setOpaque(true);
            }

            public void mouseExited(MouseEvent e) {
                panel.setOpaque(false);
            }
        });
    }
    public static void arrondirPanel(javax.swing.JPanel panel, java.awt.Color fond, int radius) {
        panel.setOpaque(false);
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        panel.setUI(new javax.swing.plaf.PanelUI() {});

        javax.swing.plaf.PanelUI ui = new javax.swing.plaf.basic.BasicPanelUI() {
            @Override
            public void paint(java.awt.Graphics g, javax.swing.JComponent c) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fond);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), radius, radius);
                g2.dispose();
                super.paint(g, c);
            }
        };

        panel.setUI(ui);
    }

    // Dans ProprieteDisigne.java
    public static void styleUserIcon(JButton btn, Color couleurFond) {
    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setOpaque(false);
    btn.setMargin(new Insets(0, 0, 0, 0));

    btn.setUI(new BasicButtonUI() {
        @Override 
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = c.getWidth();
            int h = c.getHeight();

            // 1. Dessiner le fond rond
            g2.setColor(couleurFond);
            g2.fillRoundRect(0, 0, w, h, Math.min(w, h), Math.min(w, h));

            // 2. Dessiner l'ICÔNE (si elle existe)
            Icon icon = b.getIcon();
            if (icon != null) {
                int x = (w - icon.getIconWidth()) / 2;
                int y = (h - icon.getIconHeight()) / 2;
                icon.paintIcon(c, g2, x, y);
            }

            // 3. Dessiner le TEXTE (si il y en a)
            String text = b.getText();
            if (text != null && !text.isEmpty()) {
                g2.setColor(b.getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int x = (w - fm.stringWidth(text)) / 2;
                int y = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, x, y);
            }

            g2.dispose();
        }
    });
}
    // Dans ProprieteDisigne.java — remplace panelArrondi par ça
    public static void panelArrondi(JPanel panel, Color fond, Color bordure, int radius) {
    panel.setOpaque(false);
    panel.setBackground(fond); // Utile pour certains composants enfants

    // Au lieu de remplacer le panel, on lui donne une bordure personnalisée
    // ou on modifie son UI via un Border spécifique qui gère l'arrondi.
    panel.setBorder(new javax.swing.border.AbstractBorder() {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // On peint le fond d'abord
            g2.setColor(fond);
            g2.fillRoundRect(x, y, width - 1, height - 1, radius, radius);
            
            // On peint la bordure
            g2.setColor(bordure);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2); // Padding interne
        }
    });
    
    panel.repaint();
}
    public static void boutonArrondi(JButton bouton, Color fond, Color bordure, Color texte, int radius) {
        bouton.setOpaque(false);
        bouton.setContentAreaFilled(false);
        bouton.setFocusPainted(false);
        bouton.setBorderPainted(false);
        bouton.setForeground(texte);
        bouton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        bouton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // On dessine le fond et la bordure
                g2.setColor(fond);
                g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, radius, radius);
                g2.setColor(bordure);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, radius, radius);

                g2.dispose();

                // TRÈS IMPORTANT : super.paint permet au bouton de respecter 
                // les setHorizontalAlignment que tu feras plus tard !
                super.paint(g, c);
            }
        });
    }
    public static void stylisationBarreLateral(
        JButton btn,
        boolean estActif) {

    // ── Couleurs du design ──────────────────────────────────────────
    final Color FOND_NORMAL   = new Color(26,  77,  46);   // #1a4d2e
    final Color FOND_HOVER    = new Color(45, 106,  66);   // #2d6a42
    final Color FOND_ACTIF    = new Color(45, 106,  66);   // #2d6a42
    final Color TEXTE_NORMAL  = new Color(255, 255, 255);  // blanc
    final Color TEXTE_SUBTIL  = new Color(122, 176, 138);  // #7ab08a
    final Color BARRE_ACTIF   = new Color(74,  222, 128);  // vert vif

    // ── Apparence de base ───────────────────────────────────────────
    btn.setOpaque(false);
    btn.setContentAreaFilled(false);
    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setForeground(estActif ? TEXTE_NORMAL : TEXTE_SUBTIL);
    btn.setHorizontalAlignment(SwingConstants.LEFT);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setFont(btn.getFont().deriveFont(estActif ? Font.BOLD : Font.PLAIN, 13f));
    btn.setMargin(new Insets(10, 16, 10, 16));

    final boolean[] survol = {false};

    btn.setUI(new BasicButtonUI() {
        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b  = (AbstractButton) c;
            Graphics2D     g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int w = c.getWidth();
            int h = c.getHeight();

            // 1. ── Fond ────────────────────────────────────────────
            boolean activer = survol[0] || estActif;
            g2.setColor(activer ? FOND_HOVER : FOND_NORMAL);
            g2.fillRoundRect(4, 2, w - 8, h - 4, 10, 10);

            // 2. ── Barre latérale gauche (actif / hover) ──────────
            if (activer) {
                g2.setColor(BARRE_ACTIF);
                g2.setStroke(new BasicStroke(3.5f,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int barH = (int) (h * 0.50);
                int barY = (h - barH) / 2;
                g2.drawLine(6, barY, 6, barY + barH);
            }

            // 3. ── Icône ───────────────────────────────────────────
            Icon icon  = b.getIcon();
            int  iconW = 0;
            if (icon != null) {
                iconW      = icon.getIconWidth();
                int iconH  = icon.getIconHeight();
                int iconX  = 18 + (survol[0] ? 2 : 0);
                int iconY  = (h - iconH) / 2;
                icon.paintIcon(c, g2, iconX, iconY);
            }

            // 4. ── Texte ───────────────────────────────────────────
            String text = b.getText();
            if (text != null && !text.isEmpty()) {
                g2.setColor(activer ? TEXTE_NORMAL : TEXTE_SUBTIL);
                g2.setFont(b.getFont());
                FontMetrics fm    = g2.getFontMetrics();
                int         gap   = (iconW > 0) ? 12 : 0;
                int         textX = 18 + iconW + gap + (survol[0] ? 2 : 0);
                int         textY = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, textX, textY);
            }

            g2.dispose();
        }
    });

    // ── Hover ───────────────────────────────────────────────────────
    btn.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            survol[0] = true;
            btn.setForeground(TEXTE_NORMAL);
            btn.repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            survol[0] = false;
            btn.setForeground(estActif ? TEXTE_NORMAL : TEXTE_SUBTIL);
            btn.repaint();
        }
    });
}

    
    
     /* This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     * 
     */

    @SuppressWarnings("unchecked")
     


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
