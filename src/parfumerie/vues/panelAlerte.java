/*
 * Panel Alertes Stock — Thème sombre luxe
 */
package parfumerie.vues;

import parfumerie.dao.AlerteDAO;
import parfumerie.modeles.Alerte;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;

/**
 * Panel Alertes Stock — Thème sombre luxe cohérent avec le tableau de bord.
 * @author Angele
 */
public class panelAlerte extends javax.swing.JPanel {

    // ── Palette thème sombre luxe ──────────────────
    private static final Color BG_DARK       = new Color(18, 16, 14);
    private static final Color BG_PANEL      = new Color(26, 23, 19);
    private static final Color BG_CARD       = new Color(32, 28, 22);
    private static final Color GOLD          = new Color(212, 175, 55);
    private static final Color GOLD_LIGHT    = new Color(240, 215, 140);
    private static final Color TEXT_PRIMARY  = new Color(240, 230, 200);
    private static final Color TEXT_MUTED    = new Color(160, 148, 120);
    private static final Color BORDER        = new Color(55, 48, 36);
    private static final Color RED_BG        = new Color(60, 20, 20);
    private static final Color RED_ACCENT    = new Color(220, 80, 60);
    private static final Color YELLOW_BG     = new Color(50, 40, 10);
    private static final Color YELLOW_ACCENT = new Color(200, 160, 30);

    // ── Polices ───────────────────────────────────
    private static final Font FONT_TITRE     = new Font("Baskerville Old Face", Font.BOLD, 22);
    private static final Font FONT_SECTION   = new Font("Baskerville Old Face", Font.BOLD, 14);
    private static final Font FONT_CARD_NOM  = new Font("Baskerville Old Face", Font.BOLD, 14);
    private static final Font FONT_CARD_SUB  = new Font("Baskerville Old Face", Font.PLAIN, 12);
    private static final Font FONT_BTN       = new Font("Baskerville Old Face", Font.PLAIN, 13);

    private AlerteDAO alerteDAO;
    private JButton   btnNouveau;
    private JPanel    pnlRupture;
    private JPanel    pnlStockFaible;

    // =========================================================================
    //  CONSTRUCTEUR
    // =========================================================================
    public panelAlerte() {
        try {
            alerteDAO = new AlerteDAO();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Connexion BD impossible :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        construireUI();
        chargerAlertes();
    }

    // =========================================================================
    //  CONSTRUCTION UI
    // =========================================================================
    private void construireUI() {
        setBackground(BG_DARK);
        setLayout(new BorderLayout());

        // ── Header ─────────────────────────────────
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(BG_DARK);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        JPanel panelTitre = new JPanel();
        panelTitre.setBackground(BG_DARK);
        panelTitre.setLayout(new BoxLayout(panelTitre, BoxLayout.Y_AXIS));
        JLabel lblTitre = new JLabel("Alertes Stock");
        lblTitre.setFont(FONT_TITRE);
        lblTitre.setForeground(TEXT_PRIMARY);
        panelTitre.add(lblTitre);
        panelHeader.add(panelTitre, BorderLayout.CENTER);

        // Séparateur doré
        JPanel separator = new JPanel();
        separator.setBackground(GOLD);
        separator.setPreferredSize(new Dimension(1, 1));
        panelHeader.add(separator, BorderLayout.SOUTH);

        btnNouveau = new JButton("+ Réapprovisionner");
        btnNouveau.setBackground(GOLD);
        btnNouveau.setForeground(BG_DARK);
        btnNouveau.setFont(FONT_BTN);
        btnNouveau.setBorderPainted(false);
        btnNouveau.setFocusPainted(false);
        btnNouveau.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNouveau.setPreferredSize(new Dimension(200, 38));
        btnNouveau.addActionListener(e -> allerVersEntreeStock());
        panelHeader.add(btnNouveau, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);

        // ── Centre : 2 colonnes ────────────────────
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlCenter.setBackground(BG_DARK);
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        // ── Colonne gauche : Rupture ───────────────
        JPanel colGauche = new JPanel(new BorderLayout(0, 10));
        colGauche.setBackground(BG_DARK);

        JPanel headerRupture = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerRupture.setBackground(BG_DARK);
        headerRupture.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel dotRouge = new JPanel();
        dotRouge.setBackground(RED_ACCENT);
        dotRouge.setPreferredSize(new Dimension(10, 10));
        dotRouge.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 8));

        JLabel lblRupture = new JLabel("  Rupture de Stock Imminente");
        lblRupture.setFont(FONT_SECTION);
        lblRupture.setForeground(RED_ACCENT);
        headerRupture.add(dotRouge);
        headerRupture.add(lblRupture);
        colGauche.add(headerRupture, BorderLayout.NORTH);

        JPanel wrapRupture = new JPanel(new BorderLayout());
        wrapRupture.setBackground(BG_PANEL);
        wrapRupture.setBorder(BorderFactory.createLineBorder(new Color(80, 30, 30), 1));

        pnlRupture = new JPanel();
        pnlRupture.setBackground(BG_PANEL);
        pnlRupture.setLayout(new BoxLayout(pnlRupture, BoxLayout.Y_AXIS));

        JScrollPane scrollRupture = new JScrollPane(pnlRupture);
        scrollRupture.setBorder(BorderFactory.createEmptyBorder());
        scrollRupture.getViewport().setBackground(BG_PANEL);
        scrollRupture.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapRupture.add(scrollRupture, BorderLayout.CENTER);
        colGauche.add(wrapRupture, BorderLayout.CENTER);
        pnlCenter.add(colGauche);

        // ── Colonne droite : Stock Faible ──────────
        JPanel colDroite = new JPanel(new BorderLayout(0, 10));
        colDroite.setBackground(BG_DARK);

        JPanel headerFaible = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerFaible.setBackground(BG_DARK);
        headerFaible.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel dotJaune = new JPanel();
        dotJaune.setBackground(YELLOW_ACCENT);
        dotJaune.setPreferredSize(new Dimension(10, 10));
        dotJaune.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 8));

        JLabel lblFaible = new JLabel("  Stock Faible");
        lblFaible.setFont(FONT_SECTION);
        lblFaible.setForeground(YELLOW_ACCENT);
        headerFaible.add(dotJaune);
        headerFaible.add(lblFaible);
        colDroite.add(headerFaible, BorderLayout.NORTH);

        JPanel wrapFaible = new JPanel(new BorderLayout());
        wrapFaible.setBackground(BG_PANEL);
        wrapFaible.setBorder(BorderFactory.createLineBorder(new Color(70, 55, 10), 1));

        pnlStockFaible = new JPanel();
        pnlStockFaible.setBackground(BG_PANEL);
        pnlStockFaible.setLayout(new BoxLayout(pnlStockFaible, BoxLayout.Y_AXIS));

        JScrollPane scrollFaible = new JScrollPane(pnlStockFaible);
        scrollFaible.setBorder(BorderFactory.createEmptyBorder());
        scrollFaible.getViewport().setBackground(BG_PANEL);
        scrollFaible.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapFaible.add(scrollFaible, BorderLayout.CENTER);
        colDroite.add(wrapFaible, BorderLayout.CENTER);
        pnlCenter.add(colDroite);

        add(pnlCenter, BorderLayout.CENTER);
    }

    // =========================================================================
    //  CHARGEMENT DES ALERTES
    // =========================================================================
    private void chargerAlertes() {
        pnlRupture.removeAll();
        pnlStockFaible.removeAll();

        try {
            if (alerteDAO == null) return;
            List<Alerte> alertes = alerteDAO.findNonLues();

            boolean auMoinsUneRupture    = false;
            boolean auMoinsUnStockFaible = false;

            for (Alerte a : alertes) {
                if ("CRITIQUE".equals(a.getNiveau())) {
                    pnlRupture.add(creerCarte(a, RED_BG, RED_ACCENT, "⚠  Rupture imminente"));
                    auMoinsUneRupture = true;
                } else {
                    pnlStockFaible.add(creerCarte(a, YELLOW_BG, YELLOW_ACCENT, "⚡  Stock faible"));
                    auMoinsUnStockFaible = true;
                }
            }

            if (!auMoinsUneRupture)
                ajouterCarteVide(pnlRupture, "✓  Aucune rupture de stock");
            if (!auMoinsUnStockFaible)
                ajouterCarteVide(pnlStockFaible, "✓  Aucun stock faible");

        } catch (SQLException ex) {
            ajouterCarteVide(pnlRupture,     "Erreur de chargement");
            ajouterCarteVide(pnlStockFaible, "Erreur de chargement");
        }

        pnlRupture.revalidate();     pnlRupture.repaint();
        pnlStockFaible.revalidate(); pnlStockFaible.repaint();
    }

    // =========================================================================
    //  UTILITAIRES UI
    // =========================================================================
    private JPanel creerCarte(Alerte a, Color bgCarte, Color accentCouleur, String sousTitre) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(bgCarte);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        // Barre colorée à gauche
        JPanel barre = new JPanel();
        barre.setBackground(accentCouleur);
        barre.setPreferredSize(new Dimension(4, 0));
        card.add(barre, BorderLayout.WEST);

        // Texte
        JPanel info = new JPanel(new GridLayout(2, 1, 0, 4));
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel nom = new JLabel(a.getMessage());
        nom.setFont(FONT_CARD_NOM);
        nom.setForeground(TEXT_PRIMARY);

        JLabel niveauLbl = new JLabel(sousTitre);
        niveauLbl.setFont(FONT_CARD_SUB);
        niveauLbl.setForeground(accentCouleur);

        info.add(nom);
        info.add(niveauLbl);
        card.add(info, BorderLayout.CENTER);

        // Bouton Réappro
        JButton btn = new JButton("Réappro →");
        btn.setBackground(GOLD);
        btn.setForeground(BG_DARK);
        btn.setFont(new Font("Baskerville Old Face", Font.PLAIN, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 30));
        btn.addActionListener(e -> allerVersEntreeStock());
        card.add(btn, BorderLayout.EAST);

        return card;
    }

    private void ajouterCarteVide(JPanel panel, String message) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_PANEL);
        wrap.setBorder(BorderFactory.createEmptyBorder(24, 16, 24, 16));

        JLabel lbl = new JLabel(message, SwingConstants.CENTER);
        lbl.setFont(new Font("Baskerville Old Face", Font.ITALIC, 13));
        lbl.setForeground(TEXT_MUTED);
        wrap.add(lbl, BorderLayout.CENTER);
        panel.add(wrap);
    }

    private void allerVersEntreeStock() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof Acceuil)) {
            parent = parent.getParent();
        }
        if (parent instanceof Acceuil) {
            ((Acceuil) parent).chargerPage(Acceuil.Page.EntrerStock);
        }
    }
}
