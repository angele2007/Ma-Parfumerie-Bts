/*
 * Panel Sortie de Stock — Thème sombre luxe
 */
package parfumerie.vues;

import parfumerie.dao.ProduitDAO;
import parfumerie.dao.SortieStockDAO;
import parfumerie.modele.Produit;
import parfumerie.modeles.SortieStock;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel Sortie de Stock — Thème sombre luxe cohérent avec le tableau de bord.
 * @author Angele
 */
public class panelSortieStock extends javax.swing.JPanel {

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
    private static final Font FONT_TITRE   = new Font("Baskerville Old Face", Font.BOLD, 22);
    private static final Font FONT_LABEL   = new Font("Baskerville Old Face", Font.PLAIN, 12);
    private static final Font FONT_FIELD   = new Font("Baskerville Old Face", Font.PLAIN, 14);
    private static final Font FONT_TABLE   = new Font("Baskerville Old Face", Font.PLAIN, 15);
    private static final Font FONT_HEADER  = new Font("Baskerville Old Face", Font.BOLD, 13);
    private static final Font FONT_BTN     = new Font("Baskerville Old Face", Font.PLAIN, 14);
    private static final Font FONT_CAT_LBL = new Font("Baskerville Old Face", Font.BOLD, 16);

    // ─── État ─────────────────────────────────────
    private int     idEnEdition      = -1;
    private boolean modeModification = false;

    // ─── Listes en mémoire ────────────────────────
    private List<Produit> listeProduits = new ArrayList<>();

    // ─── DAO ──────────────────────────────────────
    private SortieStockDAO sortieStockDAO;
    private ProduitDAO     produitDAO;

    // ─── Modèle de tableau ────────────────────────
    private DefaultTableModel tableModel;

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    // =========================================================================
    //  CONSTRUCTEUR
    // =========================================================================
    public panelSortieStock() {
        try {
            sortieStockDAO = new SortieStockDAO();
            produitDAO     = new ProduitDAO();
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
            new String[]{"id_sortie", "id_produit", "PRODUIT",
                         "QUANTITÉ", "MOTIF", "PRIX VENTE", "DATE"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        jTable1.setModel(tableModel);

        // Masquer colonnes internes
        for (int i = 0; i < 2; i++) {
            jTable1.getColumnModel().getColumn(i).setMinWidth(0);
            jTable1.getColumnModel().getColumn(i).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(i).setWidth(0);
        }
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(180);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(90);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
                    // Prix vente en doré
                    if (column == 5) setForeground(GOLD_LIGHT);
                    // Date en muted
                    if (column == 6) setForeground(TEXT_MUTED);
                    // Motif coloré
                    if (column == 4 && value != null) {
                        switch (value.toString()) {
                            case "Vente"  -> setForeground(new Color(100, 200, 130));
                            case "Perte"  -> setForeground(new Color(255, 110, 80));
                            case "Offert" -> setForeground(new Color(150, 180, 255));
                            case "Retour" -> setForeground(new Color(255, 200, 80));
                        }
                    }
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
                    selectionnerComboParId(cmbProd, idProd,
                        listeProduits.stream().mapToInt(Produit::getIdProduit).toArray());
                    spnQte.setValue(tableModel.getValueAt(row, 3));
                    String motif = tableModel.getValueAt(row, 4).toString();
                    for (int i = 0; i < cmbMotif.getItemCount(); i++) {
                        if (cmbMotif.getItemAt(i).equals(motif)) { cmbMotif.setSelectedIndex(i); break; }
                    }
                    txtPrixV.setText(tableModel.getValueAt(row, 5).toString());
                    txtDate.setText(tableModel.getValueAt(row, 6).toString());
                }
            }
        });

        btnNouveau.addActionListener(e -> {
            modeModification = false; idEnEdition = -1;
            viderFormulaire(); pnlForm.setVisible(true);
            jTable1.clearSelection();
            btnModifier.setEnabled(false); btnSupprimer.setEnabled(false);
            revalidate(); repaint();
        });

        btnEnregistrer.addActionListener(e -> enregistrer());

        btnAnnuler.addActionListener(e -> {
            viderFormulaire(); pnlForm.setVisible(false);
            idEnEdition = -1; modeModification = false;
            jTable1.clearSelection();
            btnModifier.setEnabled(false); btnSupprimer.setEnabled(false);
            revalidate(); repaint();
        });

        btnModifier.addActionListener(e -> {
            if (idEnEdition < 0) {
                JOptionPane.showMessageDialog(this, "Sélectionnez une ligne.",
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            modeModification = true; pnlForm.setVisible(true);
            cmbProd.requestFocusInWindow(); revalidate(); repaint();
        });

        btnSupprimer.addActionListener(e -> supprimer());

        txtPrixV.addFocusListener(new java.awt.event.FocusAdapter() {
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
        if (produitDAO == null) return;
        try {
            cmbProd.removeAllItems();
            listeProduits = produitDAO.findAll();
            for (Produit p : listeProduits) cmbProd.addItem(p.getNom());
        } catch (SQLException ex) { afficherErreurBD(ex); }
    }

    private void chargerTableau() {
        if (sortieStockDAO == null) return;
        tableModel.setRowCount(0);
        try {
            for (SortieStock s : sortieStockDAO.findAll()) {
                String dateStr = (s.getDateSortie() != null)
                    ? s.getDateSortie().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
                tableModel.addRow(new Object[]{
                    s.getIdSortie(), s.getIdProduit(),
                    nomProduitById(s.getIdProduit()),
                    s.getQuantite(), s.getMotif(), s.getPrixVente(), dateStr
                });
            }
        } catch (SQLException ex) { afficherErreurBD(ex); }
    }

    // =========================================================================
    //  ENREGISTRER
    // =========================================================================
    private void enregistrer() {
        if (!validerFormulaire() || sortieStockDAO == null) return;
        try {
            int idxProd = cmbProd.getSelectedIndex();
            if (idxProd < 0) return;
            Produit produit = listeProduits.get(idxProd);
            int qte = (Integer) spnQte.getValue();

            if (qte > produit.getQuantiteStock()) {
                JOptionPane.showMessageDialog(this,
                    "Stock insuffisant ! Stock disponible : " + produit.getQuantiteStock(),
                    "Stock insuffisant", JOptionPane.WARNING_MESSAGE);
                return;
            }

            SortieStock ss = new SortieStock();
            ss.setIdProduit (produit.getIdProduit());
            ss.setQuantite  (qte);
            ss.setMotif     (cmbMotif.getSelectedItem().toString());
            ss.setPrixVente (new BigDecimal(txtPrixV.getText().trim().replace(",", ".")));
            ss.setDateSortie(parseDateVersLocalDate(txtDate.getText().trim()));

            if (modeModification && idEnEdition >= 0) {
                if (!sortieStockDAO.delete(idEnEdition)) {
                    JOptionPane.showMessageDialog(this, "Impossible de modifier.",
                        "Erreur", JOptionPane.ERROR_MESSAGE); return;
                }
                int newId = sortieStockDAO.insert(ss);
                if (newId <= 0) {
                    JOptionPane.showMessageDialog(this, "Ré-insertion échouée.",
                        "Erreur partielle", JOptionPane.WARNING_MESSAGE);
                    chargerTableau(); return;
                }
                JOptionPane.showMessageDialog(this, "✓ Sortie modifiée avec succès.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                int newId = sortieStockDAO.insert(ss);
                if (newId <= 0) {
                    JOptionPane.showMessageDialog(this, "Impossible d'enregistrer.",
                        "Erreur", JOptionPane.ERROR_MESSAGE); return;
                }
                JOptionPane.showMessageDialog(this, "✓ Sortie enregistrée avec succès.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            }

            chargerTableau(); viderFormulaire(); pnlForm.setVisible(false);
            idEnEdition = -1; modeModification = false;
            btnModifier.setEnabled(false); btnSupprimer.setEnabled(false);
            revalidate(); repaint();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Prix invalide (ex : 15000 ou 15000,50).",
                "Erreur de saisie", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) { afficherErreurBD(ex); }
    }

    // =========================================================================
    //  SUPPRIMER
    // =========================================================================
    private void supprimer() {
        if (idEnEdition < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une ligne.",
                "Aucune sélection", JOptionPane.WARNING_MESSAGE); return;
        }
        int row = jTable1.getSelectedRow();
        String nomP = tableModel.getValueAt(row, 2).toString();
        int choix = JOptionPane.showConfirmDialog(this,
            "Supprimer la sortie du produit \"" + nomP + "\" ?\nIrréversible.",
            "Confirmer", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choix == JOptionPane.YES_OPTION) {
            try {
                if (sortieStockDAO.delete(idEnEdition)) {
                    JOptionPane.showMessageDialog(this, "✓ Sortie supprimée.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                    chargerTableau(); viderFormulaire(); pnlForm.setVisible(false);
                    idEnEdition = -1; btnModifier.setEnabled(false); btnSupprimer.setEnabled(false);
                    revalidate(); repaint();
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
        if (cmbProd.getSelectedIndex() < 0) { erreur("Sélectionnez un produit.", cmbProd); return false; }
        if ((Integer) spnQte.getValue() <= 0) { erreur("La quantité doit être > 0.", spnQte); return false; }
        return validerPrix() && validerDate();
    }

    private boolean validerPrix() {
        String s = txtPrixV.getText().trim().replace(",", ".");
        if (s.isEmpty()) { marquerInvalide(txtPrixV, "Le prix est obligatoire."); return false; }
        try {
            if (Double.parseDouble(s) < 0) {
                marquerInvalide(txtPrixV, "Le prix ne peut pas être négatif."); return false;
            }
        } catch (NumberFormatException ex) {
            marquerInvalide(txtPrixV, "Prix invalide (ex : 15000,50)."); return false;
        }
        marquerValide(txtPrixV); return true;
    }

    private boolean validerDate() {
        String s = txtDate.getText().trim();
        if (s.isEmpty()) { marquerInvalide(txtDate, "La date est obligatoire (jj/MM/aaaa)."); return false; }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(false); sdf.parse(s);
        } catch (ParseException ex) {
            marquerInvalide(txtDate, "Format invalide. Ex : 15/05/2025."); return false;
        }
        marquerValide(txtDate); return true;
    }

    // =========================================================================
    //  UTILITAIRES
    // =========================================================================
    private LocalDate parseDateVersLocalDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            java.util.Date d = sdf.parse(dateStr);
            return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException ex) { return LocalDate.now(); }
    }

    private String nomProduitById(int id) {
        return listeProduits.stream()
            .filter(p -> p.getIdProduit() == id)
            .map(Produit::getNom).findFirst().orElse("(id=" + id + ")");
    }

    private void selectionnerComboParId(JComboBox<String> combo, int targetId, int[] idArray) {
        for (int i = 0; i < idArray.length; i++) {
            if (idArray[i] == targetId) { combo.setSelectedIndex(i); return; }
        }
    }

    private void viderFormulaire() {
        if (cmbProd.getItemCount() > 0) cmbProd.setSelectedIndex(0);
        cmbMotif.setSelectedIndex(0);
        spnQte.setValue(0);
        txtPrixV.setText(""); txtDate.setText("");
        marquerValide(txtPrixV); marquerValide(txtDate);
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
        lblMotif       = new JLabel();
        lblPrixV       = new JLabel();
        cmbMotif       = new JComboBox<>();
        txtPrixV       = new JTextField();
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
        lblTitre.setText("Sorties de Stock");
        panelTitre.add(lblTitre);
        panelHeader.add(panelTitre, BorderLayout.CENTER);

        JPanel separator = new JPanel();
        separator.setBackground(GOLD);
        separator.setPreferredSize(new Dimension(1, 1));
        panelHeader.add(separator, BorderLayout.SOUTH);

        btnNouveau.setBackground(GOLD);
        btnNouveau.setForeground(BG_DARK);
        btnNouveau.setFont(FONT_BTN);
        btnNouveau.setText("+ Nouvelle Sortie");
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
        lblCatProd.setText("🛒  Sorties de Stock");
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
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.insets  = new Insets(0, 0, 6, 0);
        gbc.weightx = 0.5;
        gbc.gridwidth = 1;

        // ── Ligne 1 : labels PRODUIT / QUANTITÉ ────
        gbc.gridy = 0; gbc.gridx = 0;
        lblProd.setFont(FONT_LABEL); lblProd.setForeground(TEXT_MUTED); lblProd.setText("PRODUIT");
        pnlForm.add(lblProd, gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 12, 6, 0);
        lblQte.setFont(FONT_LABEL); lblQte.setForeground(TEXT_MUTED); lblQte.setText("QUANTITÉ");
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

        // ── Ligne 3 : labels MOTIF / PRIX ──────────
        gbc.gridy = 2; gbc.gridx = 0; gbc.insets = new Insets(0, 0, 6, 0);
        lblMotif.setFont(FONT_LABEL); lblMotif.setForeground(TEXT_MUTED); lblMotif.setText("MOTIF");
        pnlForm.add(lblMotif, gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 12, 6, 0);
        lblPrixV.setFont(FONT_LABEL); lblPrixV.setForeground(TEXT_MUTED); lblPrixV.setText("PRIX DE VENTE ( FCFA )");
        pnlForm.add(lblPrixV, gbc);

        // ── Ligne 4 : combo motif / prix vente ─────
        gbc.gridy = 3; gbc.gridx = 0; gbc.insets = new Insets(0, 0, 14, 0);
        styleCombo(cmbMotif);
        cmbMotif.setModel(new DefaultComboBoxModel<>(new String[]{"Vente", "Offert", "Perte", "Retour"}));
        cmbMotif.setPreferredSize(new Dimension(160, 38));
        pnlForm.add(cmbMotif, gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 12, 14, 0);
        styleField(txtPrixV);
        pnlForm.add(txtPrixV, gbc);

        // ── Ligne 5 : label DATE ───────────────────
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 6, 0);
        lblDate.setFont(FONT_LABEL); lblDate.setForeground(TEXT_MUTED); lblDate.setText("DATE ( jj/MM/aaaa )");
        pnlForm.add(lblDate, gbc);

        // ── Ligne 6 : champ date ───────────────────
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 20, 0);
        styleField(txtDate);
        pnlForm.add(txtDate, gbc);

        // ── Ligne 7 : boutons ──────────────────────
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 0, 0);
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
    private JButton           btnAnnuler, btnEnregistrer, btnModifier, btnNouveau, btnSupprimer;
    private JComboBox<String> cmbMotif, cmbProd;
    private JTable            jTable1;
    private JLabel            lblCatProd, lblDate, lblMotif, lblPrixV, lblProd, lblQte, lblTitre;
    private JPanel            panelHeader, panelTitre, pnlBoutons, pnlBtn;
    private JPanel            pnlCenter, pnlForm, pnlTableau;
    private JScrollPane       scrollProduits;
    private JSpinner          spnQte;
    private JTextField        txtDate, txtPrixV;
}
