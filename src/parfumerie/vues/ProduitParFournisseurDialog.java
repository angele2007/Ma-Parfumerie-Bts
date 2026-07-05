/*
 * ProduitParFournisseurDialog.java
 * Dialog qui affiche les produits liés à un fournisseur via les entrées stock
 */
package parfumerie.vues;

import parfumerie.dao.EntreeStockDAO;
import parfumerie.dao.ProduitDAO;
import parfumerie.modele.Produit;
import parfumerie.modeles.EntreeStock;
import parfumerie.modeles.Fournisseur;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * Dialog thème sombre luxe — Produits liés à un fournisseur.
 */
public class ProduitParFournisseurDialog extends JDialog {

    // ── Palette ───────────────────────────────────
    private static final Color BG_DARK      = new Color(18, 16, 14);
    private static final Color BG_PANEL     = new Color(26, 23, 19);
    private static final Color BG_ROW_ALT   = new Color(32, 28, 22);
    private static final Color BG_FIELD     = new Color(36, 32, 26);
    private static final Color GOLD         = new Color(212, 175, 55);
    private static final Color GOLD_LIGHT   = new Color(240, 215, 140);
    private static final Color TEXT_PRIMARY = new Color(240, 230, 200);
    private static final Color TEXT_MUTED   = new Color(160, 148, 120);
    private static final Color BORDER       = new Color(55, 48, 36);
    private static final Color SEL_BG       = new Color(180, 150, 80);
    private static final Color SEL_FG       = new Color(18, 16, 14);
    private static final Color OK_GREEN     = new Color(100, 200, 130);
    private static final Color ALERT_RED    = new Color(255, 110, 80);

    private static final Font FONT_TITRE    = new Font("Baskerville Old Face", Font.BOLD, 18);
    private static final Font FONT_LABEL    = new Font("Baskerville Old Face", Font.PLAIN, 12);
    private static final Font FONT_TABLE    = new Font("Baskerville Old Face", Font.PLAIN, 14);
    private static final Font FONT_HEADER   = new Font("Baskerville Old Face", Font.BOLD, 12);
    private static final Font FONT_BTN      = new Font("Baskerville Old Face", Font.PLAIN, 13);

    public ProduitParFournisseurDialog(Frame parent, Fournisseur fournisseur) {
        super(parent, "Produits — " + fournisseur.getNom(), true);
        setSize(750, 520);
        setLocationRelativeTo(parent);
        setResizable(true);
        construireUI(fournisseur);
    }

    private void construireUI(Fournisseur fournisseur) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);

        // ── Header ─────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        JPanel pnlTitre = new JPanel();
        pnlTitre.setBackground(BG_DARK);
        pnlTitre.setLayout(new BoxLayout(pnlTitre, BoxLayout.Y_AXIS));

        JLabel lblTitre = new JLabel("🏢  " + fournisseur.getNom());
        lblTitre.setFont(FONT_TITRE);
        lblTitre.setForeground(TEXT_PRIMARY);
        pnlTitre.add(lblTitre);
        pnlTitre.add(Box.createVerticalStrut(4));

        // Infos fournisseur
        JPanel pnlInfos = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        pnlInfos.setBackground(BG_DARK);
        if (fournisseur.getTelephone() != null && !fournisseur.getTelephone().isEmpty())
            pnlInfos.add(infoChip("📞 " + fournisseur.getTelephone()));
        if (fournisseur.getEmail() != null && !fournisseur.getEmail().isEmpty())
            pnlInfos.add(infoChip("✉ " + fournisseur.getEmail()));
        if (fournisseur.getAdresse() != null && !fournisseur.getAdresse().isEmpty())
            pnlInfos.add(infoChip("📍 " + fournisseur.getAdresse()));
        pnlTitre.add(pnlInfos);

        header.add(pnlTitre, BorderLayout.CENTER);

        // Ligne dorée
        JPanel sep = new JPanel();
        sep.setBackground(GOLD);
        sep.setPreferredSize(new Dimension(1, 1));
        header.add(sep, BorderLayout.SOUTH);

        root.add(header, BorderLayout.NORTH);

        // ── Corps ─────────────────────────────────
        JPanel corps = new JPanel(new BorderLayout(0, 10));
        corps.setBackground(BG_DARK);
        corps.setBorder(BorderFactory.createEmptyBorder(12, 24, 16, 24));

        // Compteur
        JLabel lblCompteur = new JLabel("Produits approvisionnés");
        lblCompteur.setFont(new Font("Baskerville Old Face", Font.BOLD, 15));
        lblCompteur.setForeground(GOLD);
        corps.add(lblCompteur, BorderLayout.NORTH);

        // Tableau
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"NOM", "MARQUE", "CATÉGORIE", "CONTENANCE",
                         "PRIX VENTE", "STOCK", "SEUIL", "ÉTAT"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(FONT_TABLE);
        table.setRowHeight(44);
        table.setBackground(BG_PANEL);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER);
        table.setSelectionBackground(SEL_BG);
        table.setSelectionForeground(SEL_FG);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 0));

        // En-tête
        JTableHeader th = table.getTableHeader();
        th.setFont(FONT_HEADER);
        th.setBackground(BG_DARK);
        th.setForeground(GOLD);
        th.setPreferredSize(new Dimension(th.getWidth(), 42));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GOLD));
        ((DefaultTableCellRenderer) th.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.LEFT);

        // Renderer lignes
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(FONT_TABLE);
                if (sel) { setBackground(SEL_BG); setForeground(SEL_FG); }
                else {
                    setBackground(row % 2 == 0 ? BG_PANEL : BG_ROW_ALT);
                    setForeground(TEXT_PRIMARY);
                    if (col == 4) setForeground(GOLD_LIGHT); // Prix
                    if (col == 7 && v != null) {             // État
                        setForeground(v.toString().contains("Alerte") ? ALERT_RED : OK_GREEN);
                        setFont(new Font("Baskerville Old Face", Font.BOLD, 13));
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        // Largeurs colonnes
        int[] widths = {160, 110, 90, 85, 95, 55, 55, 75};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scroll.getViewport().setBackground(BG_PANEL);
        corps.add(scroll, BorderLayout.CENTER);

        // ── Footer ─────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setBackground(BG_DARK);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JButton btnFermer = new JButton("Fermer");
        btnFermer.setBackground(new Color(50, 45, 36));
        btnFermer.setForeground(TEXT_MUTED);
        btnFermer.setFont(FONT_BTN);
        btnFermer.setBorderPainted(false);
        btnFermer.setFocusPainted(false);
        btnFermer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFermer.setPreferredSize(new Dimension(110, 34));
        btnFermer.addActionListener(e -> dispose());
        footer.add(btnFermer);

        corps.add(footer, BorderLayout.SOUTH);
        root.add(corps, BorderLayout.CENTER);
        setContentPane(root);

        // Charger les données
        chargerProduits(fournisseur.getIdFournisseur(), model, lblCompteur);
    }

    // =========================================================================
    //  CHARGEMENT — Produits via EntreeStock
    // =========================================================================
    private void chargerProduits(int idFournisseur, DefaultTableModel model, JLabel lblCompteur) {
        model.setRowCount(0);
        try {
            // Récupérer les ids produits approvisionnés par ce fournisseur
            List<EntreeStock> entrees = new EntreeStockDAO().findAll();
            Set<Integer> idsProduits = new LinkedHashSet<>();
            for (EntreeStock es : entrees) {
                if (es.getIdFournisseur() == idFournisseur)
                    idsProduits.add(es.getIdProduit());
            }

            if (idsProduits.isEmpty()) {
                lblCompteur.setText("Aucun produit approvisionné par ce fournisseur");
                return;
            }

            // Charger les détails de chaque produit
            ProduitDAO dao = new ProduitDAO();
            int count = 0;
            for (int idP : idsProduits) {
                Produit p = dao.findById(idP);
                if (p == null) continue;
                String etat = p.getQuantiteStock() <= p.getSeuilAlerte()
                    ? "⚠ Alerte" : "✓ OK";
                model.addRow(new Object[]{
                    p.getNom(),
                    p.getMarque(),
                    p.getCategorie() != null ? p.getCategorie().name() : "",
                    p.getContenanceMl() != null ? p.getContenanceMl() + " ml" : "-",
                    p.getPrixVente() + " FCFA",
                    p.getQuantiteStock(),
                    p.getSeuilAlerte(),
                    etat
                });
                count++;
            }
            lblCompteur.setText(count + " produit" + (count > 1 ? "s" : "") + " approvisionné" + (count > 1 ? "s" : ""));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur BD :\n" + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper chip info
    private JLabel infoChip(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Baskerville Old Face", Font.PLAIN, 11));
        l.setForeground(TEXT_MUTED);
        return l;
    }
}
