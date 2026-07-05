/*
 * Panel Entrée de Stock — Thème sombre luxe
 */
package parfumerie.vues;

import parfumerie.dao.EntreeStockDAO;
import parfumerie.dao.ProduitDAO;
import parfumerie.dao.FournisseurDAO;
import parfumerie.modeles.EntreeStock;
import parfumerie.modeles.Fournisseur;
import parfumerie.modele.Produit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel Entrée de Stock — Thème sombre luxe cohérent avec le tableau de bord.
 * @author Angele
 */
public class panelEntrerStock extends javax.swing.JPanel {

    // ── Palette thème sombre luxe ──────────────────
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

    // ── Polices ───────────────────────────────────
    private static final Font FONT_TITRE    = new Font("Baskerville Old Face", Font.BOLD, 22);
    private static final Font FONT_LABEL    = new Font("Baskerville Old Face", Font.PLAIN, 12);
    private static final Font FONT_FIELD    = new Font("Baskerville Old Face", Font.PLAIN, 14);
    private static final Font FONT_TABLE    = new Font("Baskerville Old Face", Font.PLAIN, 15);
    private static final Font FONT_HEADER   = new Font("Baskerville Old Face", Font.BOLD, 13);
    private static final Font FONT_BTN      = new Font("Baskerville Old Face", Font.PLAIN, 14);
    private static final Font FONT_CAT_LBL  = new Font("Baskerville Old Face", Font.BOLD, 16);

    // ─── État ─────────────────────────────────────
    private int     idEnEdition      = -1;
    private boolean modeModification = false;

    // ─── Listes en mémoire ────────────────────────
    private List<Produit>     listeProduits     = new ArrayList<>();
    private List<Fournisseur> listeFournisseurs = new ArrayList<>();

    // ─── DAO ──────────────────────────────────────
    private EntreeStockDAO entreeStockDAO;
    private ProduitDAO     produitDAO;
    private FournisseurDAO fournisseurDAO;

    // ─── Modèle de tableau ────────────────────────
    private DefaultTableModel tableModel;

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    // =========================================================================
    //  CONSTRUCTEUR
    // =========================================================================
    public panelEntrerStock() {
        try {
            entreeStockDAO = new EntreeStockDAO();
            produitDAO     = new ProduitDAO();
            fournisseurDAO = new FournisseurDAO();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Connexion BD impossible :\n" + ex.getMessage(),
                "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
        }

        initComponents();
        configurerTableau();
        configurerListeners();
        chargerCombos();
        chargerTableau();
        pnlForm.setVisible(false);
    }

    // =========================================================================
    //  CONFIGURATION TABLEAU
    // =========================================================================
    private void configurerTableau() {
        tableModel = new DefaultTableModel(
            new String[]{"id_entree", "id_produit", "id_fournisseur",
                         "PRODUIT", "FOURNISSEUR", "QUANTITÉ", "PRIX UNITAIRE", "DATE"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        jTable1.setModel(tableModel);

        // Masquer colonnes internes
        for (int i = 0; i < 3; i++) {
            jTable1.getColumnModel().getColumn(i).setMinWidth(0);
            jTable1.getColumnModel().getColumn(i).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(i).setWidth(0);
        }
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(180);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(160);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);

        // ── Style tableau ──────────────────────────
        jTable1.setFont(FONT_TABLE);
        jTable1.setRowHeight(48);
        jTable1.setBackground(BG_PANEL);
        jTable1.setForeground(TEXT_PRIMARY);
        jTable1.setGridColor(BORDER);
        jTable1.setSelectionBackground(SEL_BG);
        jTable1.setSelectionForeground(SEL_FG);
        jTable1.setShowVerticalLines(true);
        jTable1.setShowHorizontalLines(true);
        jTable1.setIntercellSpacing(new Dimension(0, 0));
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // ── En-tête ────────────────────────────────
        JTableHeader header = jTable1.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(BG_DARK);
        header.setForeground(GOLD);
        header.setPreferredSize(new Dimension(header.getWidth(), 44));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GOLD));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.LEFT);

        // ── ScrollPane ─────────────────────────────
        scrollProduits.getViewport().setBackground(BG_PANEL);
        scrollProduits.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        // ── Renderer lignes alternées ──────────────
        jTable1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(FONT_TABLE);
                if (isSelected) {
                    setBackground(SEL_BG);
                    setForeground(SEL_FG);
                } else {
                    setBackground(row % 2 == 0 ? BG_PANEL : BG_ROW_ALT);
                    setForeground(TEXT_PRIMARY);
                    // Prix en doré
                    if (column == 6) setForeground(GOLD_LIGHT);
                    // Date en muted
                    if (column == 7) setForeground(TEXT_MUTED);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return this;
            }
        });
    }

    // =========================================================================
    //  LISTENERS
    // =========================================================================
    private void configurerListeners() {
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = jTable1.getSelectedRow();
                boolean sel = row >= 0;
                btnModifier.setEnabled(sel);
                btnSupprimer.setEnabled(sel);
                if (sel) {
                    idEnEdition = (int) tableModel.getValueAt(row, 0);
                    int idProd  = (int) tableModel.getValueAt(row, 1);
                    int idFour  = (int) tableModel.getValueAt(row, 2);
                    selectionnerComboParId(cmbProd,
                        idProd,
                        listeProduits.stream().mapToInt(Produit::getIdProduit).toArray());
                    selectionnerComboParId(cmbFournisseur,
                        idFour,
                        listeFournisseurs.stream().mapToInt(Fournisseur::getIdFournisseur).toArray());
                    spnQte.setValue(tableModel.getValueAt(row, 5));
                    txtPrixU.setText(tableModel.getValueAt(row, 6).toString());
                    txtDate.setText(tableModel.getValueAt(row, 7).toString());
                }
            }
        });

        btnNouveau.addActionListener(e -> {
            modeModification = false;
            idEnEdition      = -1;
            viderFormulaire();
            pnlForm.setVisible(true);
            jTable1.clearSelection();
            btnModifier.setEnabled(false);
            btnSupprimer.setEnabled(false);
            revalidate();
        });

        btnEnregistrer.addActionListener(e -> enregistrer());

        btnAnnuler.addActionListener(e -> {
            viderFormulaire();
            pnlForm.setVisible(false);
            idEnEdition      = -1;
            modeModification = false;
            jTable1.clearSelection();
            btnModifier.setEnabled(false);
            btnSupprimer.setEnabled(false);
        });

        btnModifier.addActionListener(e -> {
            if (idEnEdition < 0) {
                JOptionPane.showMessageDialog(this, "Sélectionnez une ligne.",
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            modeModification = true;
            pnlForm.setVisible(true);
            cmbProd.requestFocusInWindow();
            revalidate();
        });

        btnSupprimer.addActionListener(e -> supprimer());

        txtPrixU.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) { validerPrix(); }
        });
        txtDate.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) { validerDate(); }
        });
    }

    // =========================================================================
    //  CHARGEMENT
    // =========================================================================
    private void chargerCombos() {
        if (produitDAO == null || fournisseurDAO == null) return;
        try {
            cmbProd.removeAllItems();
            listeProduits = produitDAO.findAll();
            for (Produit p : listeProduits) cmbProd.addItem(p.getNom());

            cmbFournisseur.removeAllItems();
            listeFournisseurs = fournisseurDAO.findAll();
            for (Fournisseur f : listeFournisseurs) cmbFournisseur.addItem(f.getNom());
        } catch (SQLException ex) { afficherErreurBD(ex); }
    }

    private void chargerTableau() {
        if (entreeStockDAO == null) return;
        tableModel.setRowCount(0);
        try {
            for (EntreeStock es : entreeStockDAO.findAll()) {
                String dateStr = "";
                if (es.getDateEntree() != null) {
                    dateStr = String.format("%02d/%02d/%04d",
                        es.getDateEntree().getDayOfMonth(),
                        es.getDateEntree().getMonthValue(),
                        es.getDateEntree().getYear());
                }
                tableModel.addRow(new Object[]{
                    es.getIdEntree(), es.getIdProduit(), es.getIdFournisseur(),
                    nomProduitById(es.getIdProduit()),
                    nomFournisseurById(es.getIdFournisseur()),
                    es.getQuantite(), es.getPrixUnitaire(), dateStr
                });
            }
        } catch (SQLException ex) { afficherErreurBD(ex); }
    }

    // =========================================================================
    //  ENREGISTRER
    // =========================================================================
    private void enregistrer() {
        if (!validerFormulaire() || entreeStockDAO == null) return;
        try {
            int idxProd = cmbProd.getSelectedIndex();
            int idxFour = cmbFournisseur.getSelectedIndex();
            if (idxProd < 0 || idxFour < 0) return;

            EntreeStock es = new EntreeStock();
            es.setIdProduit    (listeProduits.get(idxProd).getIdProduit());
            es.setIdFournisseur(listeFournisseurs.get(idxFour).getIdFournisseur());
            es.setQuantite     ((Integer) spnQte.getValue());
            es.setPrixUnitaire (new BigDecimal(txtPrixU.getText().trim().replace(",", ".")));
            es.setDateEntree   (parseDateVersLocalDateTime(txtDate.getText().trim()));

            if (modeModification && idEnEdition >= 0) {
                boolean suppOk = entreeStockDAO.delete(idEnEdition);
                if (!suppOk) {
                    JOptionPane.showMessageDialog(this, "Impossible de modifier cette entrée.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int newId = entreeStockDAO.insert(es);
                if (newId <= 0) {
                    JOptionPane.showMessageDialog(this, "Suppression OK mais ré-insertion échouée.",
                        "Erreur partielle", JOptionPane.WARNING_MESSAGE);
                    chargerTableau();
                    return;
                }
                JOptionPane.showMessageDialog(this, "✓ Entrée modifiée avec succès.",
                    "Modification réussie", JOptionPane.INFORMATION_MESSAGE);
            } else {
                int newId = entreeStockDAO.insert(es);
                if (newId <= 0) {
                    JOptionPane.showMessageDialog(this, "Impossible d'enregistrer l'entrée.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(this, "✓ Entrée enregistrée avec succès.",
                    "Enregistrement réussi", JOptionPane.INFORMATION_MESSAGE);
            }

            chargerTableau();
            viderFormulaire();
            pnlForm.setVisible(false);
            idEnEdition      = -1;
            modeModification = false;
            btnModifier.setEnabled(false);
            btnSupprimer.setEnabled(false);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Prix invalide (ex : 1500 ou 1500,50).",
                "Erreur de saisie", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) { afficherErreurBD(ex); }
    }

    // =========================================================================
    //  SUPPRIMER
    // =========================================================================
    private void supprimer() {
        if (idEnEdition < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une ligne.",
                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = jTable1.getSelectedRow();
        String nomP = tableModel.getValueAt(row, 3).toString();
        int choix = JOptionPane.showConfirmDialog(this,
            "Supprimer l'entrée du produit \"" + nomP + "\" ?\nCette action est irréversible.",
            "Confirmer la suppression", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choix == JOptionPane.YES_OPTION) {
            try {
                if (entreeStockDAO.delete(idEnEdition)) {
                    JOptionPane.showMessageDialog(this, "✓ Entrée supprimée.",
                        "Suppression réussie", JOptionPane.INFORMATION_MESSAGE);
                    chargerTableau();
                    viderFormulaire();
                    pnlForm.setVisible(false);
                    idEnEdition = -1;
                    btnModifier.setEnabled(false);
                    btnSupprimer.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Impossible de supprimer.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) { afficherErreurBD(ex); }
        }
    }

    // =========================================================================
    //  VALIDATION
    // =========================================================================
    private boolean validerFormulaire() {
        if (cmbProd.getSelectedIndex() < 0) {
            erreur("Sélectionnez un produit.", cmbProd); return false;
        }
        if (cmbFournisseur.getSelectedIndex() < 0) {
            erreur("Sélectionnez un fournisseur.", cmbFournisseur); return false;
        }
        if ((Integer) spnQte.getValue() <= 0) {
            erreur("La quantité doit être > 0.", spnQte); return false;
        }
        return validerPrix() && validerDate();
    }

    private boolean validerPrix() {
        String s = txtPrixU.getText().trim().replace(",", ".");
        if (s.isEmpty()) { marquerInvalide(txtPrixU, "Le prix est obligatoire."); return false; }
        try {
            if (Double.parseDouble(s) <= 0) {
                marquerInvalide(txtPrixU, "Le prix doit être > 0."); return false;
            }
        } catch (NumberFormatException ex) {
            marquerInvalide(txtPrixU, "Prix invalide (ex : 1500,50)."); return false;
        }
        marquerValide(txtPrixU);
        return true;
    }

    private boolean validerDate() {
        String s = txtDate.getText().trim();
        if (s.isEmpty()) { marquerInvalide(txtDate, "La date est obligatoire (jj/MM/aaaa)."); return false; }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(false);
            sdf.parse(s);
        } catch (ParseException ex) {
            marquerInvalide(txtDate, "Format invalide. Ex : 15/05/2025."); return false;
        }
        marquerValide(txtDate);
        return true;
    }

    // =========================================================================
    //  UTILITAIRES
    // =========================================================================
    private LocalDateTime parseDateVersLocalDateTime(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            java.util.Date d = sdf.parse(dateStr);
            return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        } catch (ParseException ex) { return LocalDateTime.now(); }
    }

    private String nomProduitById(int id) {
        return listeProduits.stream()
            .filter(p -> p.getIdProduit() == id)
            .map(Produit::getNom).findFirst().orElse("(id=" + id + ")");
    }

    private String nomFournisseurById(int id) {
        return listeFournisseurs.stream()
            .filter(f -> f.getIdFournisseur() == id)
            .map(Fournisseur::getNom).findFirst().orElse("(id=" + id + ")");
    }

    private void selectionnerComboParId(JComboBox<String> combo, int targetId, int[] idArray) {
        for (int i = 0; i < idArray.length; i++) {
            if (idArray[i] == targetId) { combo.setSelectedIndex(i); return; }
        }
    }

    private void viderFormulaire() {
        if (cmbProd.getItemCount()        > 0) cmbProd.setSelectedIndex(0);
        if (cmbFournisseur.getItemCount() > 0) cmbFournisseur.setSelectedIndex(0);
        spnQte.setValue(0);
        txtPrixU.setText("");
        txtDate.setText("");
        marquerValide(txtPrixU);
        marquerValide(txtDate);
    }

    private void erreur(String msg, Component c) {
        JOptionPane.showMessageDialog(this, msg, "Saisie invalide", JOptionPane.WARNING_MESSAGE);
        c.requestFocusInWindow();
    }

    private void marquerInvalide(JTextField champ, String msg) {
        champ.setBackground(new Color(80, 30, 30));
        champ.setBorder(BorderFactory.createLineBorder(new Color(200, 60, 60)));
        champ.setToolTipText(msg);
        JOptionPane.showMessageDialog(this, msg, "Saisie invalide", JOptionPane.WARNING_MESSAGE);
        champ.requestFocusInWindow();
    }

    private void marquerValide(JTextField champ) {
        champ.setBackground(BG_FIELD);
        champ.setBorder(BorderFactory.createLineBorder(BORDER));
        champ.setToolTipText(null);
    }

    private void afficherErreurBD(SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Erreur base de données :\n" + ex.getMessage(),
            "Erreur BD", JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================================
    //  INIT COMPONENTS
    // =========================================================================
    @SuppressWarnings("unchecked")
    private void initComponents() {

        panelHeader    = new JPanel();
        panelTitre     = new JPanel();
        lblTitre       = new JLabel();
        btnNouveau     = new JButton();
        pnlCenter      = new JPanel();
        pnlTableau     = new JPanel();
        lblCatProd     = new JLabel();
        scrollProduits = new JScrollPane();
        jTable1        = new JTable();
        pnlBoutons     = new JPanel();
        btnModifier    = new JButton();
        btnSupprimer   = new JButton();
        pnlForm        = new JPanel();
        lblProd        = new JLabel();
        lblQte         = new JLabel();
        cmbProd        = new JComboBox<>();
        spnQte         = new JSpinner();
        lblFournisseur = new JLabel();
        lblPrixU       = new JLabel();
        cmbFournisseur = new JComboBox<>();
        txtPrixU       = new JTextField();
        lblDate        = new JLabel();
        txtDate        = new JTextField();
        pnlBtn         = new JPanel();
        btnAnnuler     = new JButton();
        btnEnregistrer = new JButton();

        // ── Panel principal ────────────────────────
        setBackground(BG_DARK);
        setLayout(new BorderLayout());

        // ── Header ─────────────────────────────────
        panelHeader.setBackground(BG_DARK);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        panelHeader.setLayout(new BorderLayout());

        panelTitre.setBackground(BG_DARK);
        panelTitre.setLayout(new BoxLayout(panelTitre, BoxLayout.Y_AXIS));
        lblTitre.setFont(FONT_TITRE);
        lblTitre.setForeground(TEXT_PRIMARY);
        lblTitre.setText("Entrée de Stock");
        panelTitre.add(lblTitre);
        panelHeader.add(panelTitre, BorderLayout.CENTER);

        // Séparateur sous le header
        JPanel separator = new JPanel();
        separator.setBackground(GOLD);
        separator.setPreferredSize(new Dimension(1, 1));
        panelHeader.add(separator, BorderLayout.SOUTH);

        // ── Bouton Ajouter ─────────────────────────
        btnNouveau.setBackground(GOLD);
        btnNouveau.setForeground(BG_DARK);
        btnNouveau.setFont(FONT_BTN);
        btnNouveau.setText("+ Nouvelle Entrée");
        btnNouveau.setBorderPainted(false);
        btnNouveau.setFocusPainted(false);
        btnNouveau.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNouveau.setPreferredSize(new Dimension(180, 38));
        panelHeader.add(btnNouveau, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);

        // ── Centre ────────────────────────────────
        pnlCenter.setBackground(BG_DARK);
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(12, 24, 24, 24));
        pnlCenter.setLayout(new BorderLayout(16, 0));

        // ── Panel Tableau ──────────────────────────
        pnlTableau.setBackground(BG_DARK);
        pnlTableau.setLayout(new BorderLayout(0, 8));

        lblCatProd.setFont(FONT_CAT_LBL);
        lblCatProd.setForeground(GOLD);
        lblCatProd.setText("📦  Entrée de Produits");
        lblCatProd.setBorder(BorderFactory.createEmptyBorder(0, 2, 8, 0));
        pnlTableau.add(lblCatProd, BorderLayout.NORTH);

        jTable1.setBackground(BG_PANEL);
        jTable1.setForeground(TEXT_PRIMARY);
        jTable1.setGridColor(BORDER);
        jTable1.setRowHeight(48);
        jTable1.setFont(FONT_TABLE);
        jTable1.setSelectionBackground(SEL_BG);
        jTable1.setSelectionForeground(SEL_FG);

        scrollProduits.setViewportView(jTable1);
        scrollProduits.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scrollProduits.getViewport().setBackground(BG_PANEL);
        pnlTableau.add(scrollProduits, BorderLayout.CENTER);

        // ── Boutons Modifier / Supprimer ───────────
        pnlBoutons.setBackground(BG_DARK);
        pnlBoutons.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        pnlBoutons.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        btnModifier.setBackground(GOLD);
        btnModifier.setForeground(BG_DARK);
        btnModifier.setFont(FONT_BTN);
        btnModifier.setText("✏  Modifier");
        btnModifier.setBorderPainted(false);
        btnModifier.setFocusPainted(false);
        btnModifier.setEnabled(false);
        btnModifier.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModifier.setPreferredSize(new Dimension(140, 38));

        btnSupprimer.setBackground(new Color(80, 30, 30));
        btnSupprimer.setForeground(new Color(255, 160, 140));
        btnSupprimer.setFont(FONT_BTN);
        btnSupprimer.setText("🗑  Supprimer");
        btnSupprimer.setBorderPainted(false);
        btnSupprimer.setFocusPainted(false);
        btnSupprimer.setEnabled(false);
        btnSupprimer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSupprimer.setPreferredSize(new Dimension(150, 38));

        pnlBoutons.add(btnModifier);
        pnlBoutons.add(btnSupprimer);
        pnlTableau.add(pnlBoutons, BorderLayout.SOUTH);
        pnlCenter.add(pnlTableau, BorderLayout.CENTER);

        // ── Formulaire (droite) ────────────────────
        pnlForm.setBackground(BG_PANEL);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        pnlForm.setPreferredSize(new Dimension(400, 0));
        pnlForm.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.insets    = new Insets(0, 0, 6, 0);
        gbc.weightx   = 0.5;
        gbc.gridwidth = 1;

        // ── Ligne 1 : labels PRODUIT / QUANTITÉ ────
        gbc.gridy = 0; gbc.gridx = 0;
        lblProd.setFont(FONT_LABEL);
        lblProd.setForeground(TEXT_MUTED);
        lblProd.setText("PRODUIT");
        pnlForm.add(lblProd, gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 12, 6, 0);
        lblQte.setFont(FONT_LABEL);
        lblQte.setForeground(TEXT_MUTED);
        lblQte.setText("QUANTITÉ");
        pnlForm.add(lblQte, gbc);

        // ── Ligne 2 : combo produit / spinner qté ─
        gbc.gridy = 1; gbc.gridx = 0; gbc.insets = new Insets(0, 0, 14, 0);
        styleCombo(cmbProd);
        cmbProd.setPreferredSize(new Dimension(160, 38));
        pnlForm.add(cmbProd, gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 12, 14, 0);
        spnQte.setModel(new SpinnerNumberModel(0, 0, 99999, 1));
        styleSpinner(spnQte);
        pnlForm.add(spnQte, gbc);

        // ── Ligne 3 : labels FOURNISSEUR / PRIX ────
        gbc.gridy = 2; gbc.gridx = 0; gbc.insets = new Insets(0, 0, 6, 0);
        lblFournisseur.setFont(FONT_LABEL);
        lblFournisseur.setForeground(TEXT_MUTED);
        lblFournisseur.setText("FOURNISSEUR");
        pnlForm.add(lblFournisseur, gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 12, 6, 0);
        lblPrixU.setFont(FONT_LABEL);
        lblPrixU.setForeground(TEXT_MUTED);
        lblPrixU.setText("PRIX UNITAIRE ( FCFA )");
        pnlForm.add(lblPrixU, gbc);

        // ── Ligne 4 : combo fournisseur / prix ─────
        gbc.gridy = 3; gbc.gridx = 0; gbc.insets = new Insets(0, 0, 14, 0);
        styleCombo(cmbFournisseur);
        cmbFournisseur.setPreferredSize(new Dimension(160, 38));
        pnlForm.add(cmbFournisseur, gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 12, 14, 0);
        styleField(txtPrixU);
        pnlForm.add(txtPrixU, gbc);

        // ── Ligne 5 : label DATE ───────────────────
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 6, 0);
        lblDate.setFont(FONT_LABEL);
        lblDate.setForeground(TEXT_MUTED);
        lblDate.setText("DATE ( jj/MM/aaaa )");
        pnlForm.add(lblDate, gbc);

        // ── Ligne 6 : champ date ───────────────────
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        styleField(txtDate);
        pnlForm.add(txtDate, gbc);

        // ── Ligne 7 : boutons ──────────────────────
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        pnlBtn.setBackground(BG_PANEL);
        pnlBtn.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        btnEnregistrer.setBackground(GOLD);
        btnEnregistrer.setForeground(BG_DARK);
        btnEnregistrer.setFont(FONT_BTN);
        btnEnregistrer.setText("✓ Enregistrer");
        btnEnregistrer.setBorderPainted(false);
        btnEnregistrer.setFocusPainted(false);
        btnEnregistrer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEnregistrer.setPreferredSize(new Dimension(150, 36));

        btnAnnuler.setBackground(new Color(50, 45, 36));
        btnAnnuler.setForeground(TEXT_MUTED);
        btnAnnuler.setFont(FONT_BTN);
        btnAnnuler.setText("Annuler");
        btnAnnuler.setBorderPainted(false);
        btnAnnuler.setFocusPainted(false);
        btnAnnuler.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnnuler.setPreferredSize(new Dimension(110, 36));

        pnlBtn.add(btnEnregistrer);
        pnlBtn.add(btnAnnuler);
        pnlForm.add(pnlBtn, gbc);

        pnlCenter.add(pnlForm, BorderLayout.LINE_END);
        add(pnlCenter, BorderLayout.CENTER);
    }

    // ── Helpers de style ──────────────────────────
    private void styleField(JTextField f) {
        f.setFont(FONT_FIELD);
        f.setBackground(BG_FIELD);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(GOLD);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        f.setPreferredSize(new Dimension(160, 38));
    }

    private void styleCombo(JComboBox<String> c) {
        c.setFont(FONT_FIELD);
        c.setBackground(BG_FIELD);
        c.setForeground(TEXT_PRIMARY);
    }

    private void styleSpinner(JSpinner s) {
        s.setFont(FONT_FIELD);
        s.setBackground(BG_FIELD);
        s.setForeground(TEXT_PRIMARY);
        s.setPreferredSize(new Dimension(160, 38));
    }

    // ── Variables declaration ──────────────────────
    private JButton       btnAnnuler;
    private JButton       btnEnregistrer;
    private JButton       btnModifier;
    private JButton       btnNouveau;
    private JButton       btnSupprimer;
    private JComboBox<String> cmbFournisseur;
    private JComboBox<String> cmbProd;
    private JTable        jTable1;
    private JLabel        lblCatProd;
    private JLabel        lblDate;
    private JLabel        lblFournisseur;
    private JLabel        lblPrixU;
    private JLabel        lblProd;
    private JLabel        lblQte;
    private JLabel        lblTitre;
    private JPanel        panelHeader;
    private JPanel        panelTitre;
    private JPanel        pnlBoutons;
    private JPanel        pnlBtn;
    private JPanel        pnlCenter;
    private JPanel        pnlForm;
    private JPanel        pnlTableau;
    private JScrollPane   scrollProduits;
    private JSpinner      spnQte;
    private JTextField    txtDate;
    private JTextField    txtPrixU;
}
