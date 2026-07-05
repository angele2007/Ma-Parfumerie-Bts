/*
 * Panel Produits — Thème sombre luxe
 */
package parfumerie.vues;

import parfumerie.dao.ProduitDAO;
import parfumerie.modele.Produit;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Angele
 */
public class panelproduit extends javax.swing.JPanel {

    // ═══════════════════════════════════════════════
    //  CHAMP D'ÉTAT : -1 = mode ajout, >0 = mode édition
    // ═══════════════════════════════════════════════
    private int idEnEdition = -1;

    // ── Palette thème sombre luxe ──────────────────
    private static final Color BG_DARK       = new Color(18, 16, 14);
    private static final Color BG_PANEL      = new Color(26, 23, 19);
    private static final Color BG_ROW_ALT    = new Color(32, 28, 22);
    private static final Color BG_FORM       = new Color(26, 23, 19);
    private static final Color GOLD          = new Color(212, 175, 55);
    private static final Color GOLD_LIGHT    = new Color(240, 215, 140);
    private static final Color TEXT_PRIMARY  = new Color(240, 230, 200);
    private static final Color TEXT_MUTED    = new Color(160, 148, 120);
    private static final Color BORDER        = new Color(55, 48, 36);
    private static final Color SEL_BG        = new Color(180, 150, 80);
    private static final Color SEL_FG        = new Color(18, 16, 14);
    private static final Color OK_GREEN      = new Color(100, 200, 130);
    private static final Color ALERT_RED     = new Color(255, 110, 80);

    // ── Polices ───────────────────────────────────
    private static final Font FONT_TITRE     = new Font("Baskerville Old Face", Font.BOLD, 22);
    private static final Font FONT_LABEL     = new Font("Baskerville Old Face", Font.PLAIN, 12);
    private static final Font FONT_FIELD     = new Font("Baskerville Old Face", Font.PLAIN, 14);
    private static final Font FONT_TABLE     = new Font("Baskerville Old Face", Font.PLAIN, 15);
    private static final Font FONT_HEADER    = new Font("Baskerville Old Face", Font.BOLD, 13);
    private static final Font FONT_BTN       = new Font("Baskerville Old Face", Font.PLAIN, 14);
    private static final Font FONT_CAT_LBL   = new Font("Baskerville Old Face", Font.BOLD, 16);

    // ═══════════════════════════════════════════════
    //  CONSTRUCTEUR
    // ═══════════════════════════════════════════════
    public panelproduit() {
        initComponents();
        appliquerFiltreTexte(txtNom);
        appliquerFiltreTexte(txtMarque);
        pnlForm.setVisible(false);
        configurerTableau();
        chargerTableau();

        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = jTable1.getSelectedRow();
                boolean ligneSelectionnee = (row >= 0);
                btnModifier.setEnabled(ligneSelectionnee);
                btnSupprimer.setEnabled(ligneSelectionnee);
                if (ligneSelectionnee) {
                    Object valId = jTable1.getValueAt(row, 0);
                    if (valId != null) {
                        idEnEdition = Integer.parseInt(valId.toString());
                        remplirFormulaire(idEnEdition);
                        pnlForm.setVisible(true);
                    }
                }
            }
        });
    }

    // ═══════════════════════════════════════════════
    //  CHARGER TABLEAU
    // ═══════════════════════════════════════════════
    private void chargerTableau() {
        try {
            ProduitDAO dao = new ProduitDAO();
            List<Produit> liste = dao.findAll();
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            for (Produit p : liste) {
                String etat = (p.getQuantiteStock() <= p.getSeuilAlerte()) ? "⚠ Alerte" : "✓ OK";
                model.addRow(new Object[]{
                    p.getIdProduit(),
                    p.getNom(),
                    p.getMarque(),
                    p.getCategorie() != null ? p.getCategorie().name() : "",
                    p.getContenanceMl() != null ? p.getContenanceMl() + " ml" : "-",
                    p.getPrixAchat(),
                    p.getPrixVente(),
                    p.getQuantiteStock(),
                    p.getSeuilAlerte(),
                    etat
                });
            }
            lblCatProd.setText("🫙  Catalogue Produits  (" + liste.size() + ")");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des produits :\n" + ex.getMessage(),
                "Erreur base de données", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ═══════════════════════════════════════════════
    //  REMPLIR FORMULAIRE
    // ═══════════════════════════════════════════════
    private void remplirFormulaire(int idProduit) {
        try {
            ProduitDAO dao = new ProduitDAO();
            Produit p = dao.findById(idProduit);
            if (p == null) return;
            txtNom.setText(p.getNom());
            txtMarque.setText(p.getMarque());
            if (p.getCategorie() != null) cmbCat.setSelectedItem(p.getCategorie().name());
            spnCont.setValue(p.getContenanceMl() != null ? p.getContenanceMl() : 0);
            spnPrixA.setValue(p.getPrixAchat() != null ? p.getPrixAchat().intValue() : 0);
            spnPrixV.setValue(p.getPrixVente() != null ? p.getPrixVente().intValue() : 0);
            spnQte.setValue(p.getQuantiteStock());
            spnSeuilA.setValue(p.getSeuilAlerte());
            reinitialiserCouleurs();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement du produit :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ═══════════════════════════════════════════════
    //  VALIDATION
    // ═══════════════════════════════════════════════
    private boolean validerSaisies() {
        boolean valide = true;
        reinitialiserCouleurs();

        if (txtNom.getText().trim().isEmpty()) {
            txtNom.setBackground(new Color(80, 30, 30));
            valide = false;
        }
        if (txtMarque.getText().trim().isEmpty()) {
            txtMarque.setBackground(new Color(80, 30, 30));
            valide = false;
        }
        int cont = (Integer) spnCont.getValue();
        if (cont <= 0) { spnCont.setBackground(new Color(80, 30, 30)); valide = false; }

        int pa = (Integer) spnPrixA.getValue();
        if (pa <= 0) { spnPrixA.setBackground(new Color(80, 30, 30)); valide = false; }

        int pv = (Integer) spnPrixV.getValue();
        if (pv <= 0 || pv <= pa) { spnPrixV.setBackground(new Color(80, 30, 30)); valide = false; }

        int qte = (Integer) spnQte.getValue();
        if (qte < 0) { spnQte.setBackground(new Color(80, 30, 30)); valide = false; }

        int seuil = (Integer) spnSeuilA.getValue();
        if (seuil < 0) { spnSeuilA.setBackground(new Color(80, 30, 30)); valide = false; }

        if (!valide) {
            JOptionPane.showMessageDialog(this,
                "Veuillez corriger les champs surlignés :\n"
                + "• Nom et Marque obligatoires\n"
                + "• Contenance et Prix achat > 0\n"
                + "• Prix de vente > Prix d'achat\n"
                + "• Quantité et Seuil >= 0",
                "Erreur de saisie", JOptionPane.WARNING_MESSAGE);
        }
        return valide;
    }

    private void reinitialiserCouleurs() {
        Color bg = new Color(36, 32, 26);
        txtNom.setBackground(bg);
        txtMarque.setBackground(bg);
        spnCont.setBackground(bg);
        spnPrixA.setBackground(bg);
        spnPrixV.setBackground(bg);
        spnQte.setBackground(bg);
        spnSeuilA.setBackground(bg);
    }

    // ═══════════════════════════════════════════════
    //  VIDER FORMULAIRE
    // ═══════════════════════════════════════════════
    private void viderFormulaire() {
        idEnEdition = -1;
        txtNom.setText("");
        txtMarque.setText("");
        cmbCat.setSelectedIndex(0);
        spnCont.setValue(0);
        spnPrixA.setValue(0);
        spnPrixV.setValue(0);
        spnQte.setValue(0);
        spnSeuilA.setValue(0);
        reinitialiserCouleurs();
        jTable1.clearSelection();
    }

    // ═══════════════════════════════════════════════
    //  CONFIGURER TABLEAU — thème sombre luxe
    // ═══════════════════════════════════════════════
    private void configurerTableau() {
        jTable1.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"id", "NOM", "MARQUE", "CATÉGORIE", "CONTENANCE", "PRIX ACHAT", "PRIX VENTE", "STOCK", "SEUIL", "ÉTAT"}
        ) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });

        // ── Taille police & hauteur lignes ─────────
        jTable1.setFont(FONT_TABLE);
        jTable1.setRowHeight(48);

        // ── Couleurs tableau ───────────────────────
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
        header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, GOLD));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        // ── ScrollPane ─────────────────────────────
        scrollProduits.getViewport().setBackground(BG_PANEL);
        scrollProduits.setBorder(javax.swing.BorderFactory.createLineBorder(BORDER, 1));
        scrollProduits.getVerticalScrollBar().setBackground(BG_DARK);
        scrollProduits.getHorizontalScrollBar().setBackground(BG_DARK);

        // ── Renderer lignes alternées + colonne ÉTAT ─
        jTable1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(FONT_TABLE);
                if (isSelected) {
                    setBackground(SEL_BG);
                    setForeground(SEL_FG);
                } else {
                    setBackground(row % 2 == 0 ? BG_PANEL : BG_ROW_ALT);
                    setForeground(TEXT_PRIMARY);
                    // Colonne ÉTAT colorée
                    if (column == 9) {
                        String val = value != null ? value.toString() : "";
                        setForeground(val.contains("Alerte") ? ALERT_RED : OK_GREEN);
                        setFont(new Font("Baskerville Old Face", Font.BOLD, 14));
                    }
                    // Colonne prix en doré
                    if (column == 5 || column == 6) {
                        setForeground(GOLD_LIGHT);
                    }
                }
                setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return this;
            }
        });

        // ── Masquer colonne id ─────────────────────
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);
    }

    // ═══════════════════════════════════════════════
    //  FILTRE TEXTE (pas de chiffres)
    // ═══════════════════════════════════════════════
    private void appliquerFiltreTexte(javax.swing.JTextField champ) {
        ((javax.swing.text.AbstractDocument) champ.getDocument())
            .setDocumentFilter(new javax.swing.text.DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String text,
                        javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                    if (text != null && text.matches("[\\p{L}\\s'\\-\\.]*"))
                        super.insertString(fb, offset, text, attr);
                }
                @Override
                public void replace(FilterBypass fb, int offset, int length, String text,
                        javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                    if (text != null && text.matches("[\\p{L}\\s'\\-\\.]*"))
                        super.replace(fb, offset, length, text, attr);
                }
            });
    }

    // ═══════════════════════════════════════════════
    //  INIT COMPONENTS
    // ═══════════════════════════════════════════════
    @SuppressWarnings("unchecked")
    private void initComponents() {

        panelHeader   = new javax.swing.JPanel();
        panelTitre    = new javax.swing.JPanel();
        lblTitre      = new javax.swing.JLabel();
        btnNouveau    = new javax.swing.JButton();
        pnlCenter     = new javax.swing.JPanel();
        pnlForm       = new javax.swing.JPanel();
        lblNom        = new javax.swing.JLabel();
        lblMarque     = new javax.swing.JLabel();
        txtNom        = new javax.swing.JTextField();
        txtMarque     = new javax.swing.JTextField();
        lblcat        = new javax.swing.JLabel();
        lblCont       = new javax.swing.JLabel();
        cmbCat        = new javax.swing.JComboBox<>();
        spnCont       = new javax.swing.JSpinner();
        lblPrixA      = new javax.swing.JLabel();
        lblPrixV      = new javax.swing.JLabel();
        spnPrixA      = new javax.swing.JSpinner();
        spnPrixV      = new javax.swing.JSpinner();
        lblQte        = new javax.swing.JLabel();
        lblSeuilAlert = new javax.swing.JLabel();
        spnQte        = new javax.swing.JSpinner();
        spnSeuilA     = new javax.swing.JSpinner();
        jPanel1       = new javax.swing.JPanel();
        btnEnregistrer= new javax.swing.JButton();
        btnAnnuler    = new javax.swing.JButton();
        pnlTableau    = new javax.swing.JPanel();
        lblCatProd    = new javax.swing.JLabel();
        scrollProduits= new javax.swing.JScrollPane();
        jTable1       = new javax.swing.JTable();
        pnlBoutons    = new javax.swing.JPanel();
        btnModifier   = new javax.swing.JButton();
        btnSupprimer  = new javax.swing.JButton();

        // ── Panel principal ────────────────────────
        setBackground(BG_DARK);
        setLayout(new java.awt.BorderLayout());

        // ── Header ─────────────────────────────────
        panelHeader.setBackground(BG_DARK);
        panelHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 24, 12, 24));
        panelHeader.setLayout(new java.awt.BorderLayout());

        panelTitre.setBackground(BG_DARK);
        panelTitre.setLayout(new javax.swing.BoxLayout(panelTitre, javax.swing.BoxLayout.Y_AXIS));

        lblTitre.setFont(FONT_TITRE);
        lblTitre.setForeground(TEXT_PRIMARY);
        lblTitre.setText("Produits");
        panelTitre.add(lblTitre);
        panelHeader.add(panelTitre, java.awt.BorderLayout.CENTER);

        // ── Bouton Ajouter ─────────────────────────
        btnNouveau.setBackground(GOLD);
        btnNouveau.setForeground(BG_DARK);
        btnNouveau.setFont(FONT_BTN);
        btnNouveau.setText("+ Ajouter un Produit");
        btnNouveau.setBorderPainted(false);
        btnNouveau.setFocusPainted(false);
        btnNouveau.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNouveau.setPreferredSize(new Dimension(200, 38));
        btnNouveau.addActionListener(this::btnNouveauActionPerformed);
        panelHeader.add(btnNouveau, java.awt.BorderLayout.EAST);

        // Séparateur doré sous le header
        javax.swing.JPanel separator = new javax.swing.JPanel();
        separator.setBackground(GOLD);
        separator.setPreferredSize(new Dimension(1, 1));
        panelHeader.add(separator, java.awt.BorderLayout.SOUTH);

        add(panelHeader, java.awt.BorderLayout.NORTH);

        // ── Centre ────────────────────────────────
        pnlCenter.setBackground(BG_DARK);
        pnlCenter.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 24, 24, 24));
        pnlCenter.setLayout(new java.awt.BorderLayout(16, 0));

        // ── Formulaire (droite) ────────────────────
        pnlForm.setBackground(BG_FORM);
        pnlForm.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER, 1),
            javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        pnlForm.setMaximumSize(new Dimension(420, 520));
        pnlForm.setMinimumSize(new Dimension(420, 480));
        pnlForm.setPreferredSize(new Dimension(420, 480));

        // ── Labels champs ──────────────────────────
        Color fieldBg = new Color(36, 32, 26);

        lblNom.setFont(FONT_LABEL);
        lblNom.setForeground(TEXT_MUTED);
        lblNom.setText("NOM DU PARFUM");

        lblMarque.setFont(FONT_LABEL);
        lblMarque.setForeground(TEXT_MUTED);
        lblMarque.setText("MARQUE");

        txtNom.setFont(FONT_FIELD);
        txtNom.setBackground(fieldBg);
        txtNom.setForeground(TEXT_PRIMARY);
        txtNom.setCaretColor(GOLD);
        txtNom.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER),
            javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        txtMarque.setFont(FONT_FIELD);
        txtMarque.setBackground(fieldBg);
        txtMarque.setForeground(TEXT_PRIMARY);
        txtMarque.setCaretColor(GOLD);
        txtMarque.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(BORDER),
            javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        lblcat.setFont(FONT_LABEL);
        lblcat.setForeground(TEXT_MUTED);
        lblcat.setText("CATÉGORIE");

        lblCont.setFont(FONT_LABEL);
        lblCont.setForeground(TEXT_MUTED);
        lblCont.setText("CONTENANCE ( ML )");

        cmbCat.setFont(FONT_FIELD);
        cmbCat.setBackground(fieldBg);
        cmbCat.setForeground(TEXT_PRIMARY);
        cmbCat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Homme", "Femme", "Mixte", "Enfant"}));

        spnCont.setFont(FONT_FIELD);
        spnCont.setBackground(fieldBg);
        spnCont.setForeground(TEXT_PRIMARY);

        lblPrixA.setFont(FONT_LABEL);
        lblPrixA.setForeground(TEXT_MUTED);
        lblPrixA.setText("PRIX D'ACHAT ( FCFA )");

        lblPrixV.setFont(FONT_LABEL);
        lblPrixV.setForeground(TEXT_MUTED);
        lblPrixV.setText("PRIX DE VENTE ( FCFA )");

        spnPrixA.setFont(FONT_FIELD);
        spnPrixA.setBackground(fieldBg);
        spnPrixA.setForeground(TEXT_PRIMARY);

        spnPrixV.setFont(FONT_FIELD);
        spnPrixV.setBackground(fieldBg);
        spnPrixV.setForeground(TEXT_PRIMARY);

        lblQte.setFont(FONT_LABEL);
        lblQte.setForeground(TEXT_MUTED);
        lblQte.setText("QUANTITÉ INITIALE");

        lblSeuilAlert.setFont(FONT_LABEL);
        lblSeuilAlert.setForeground(TEXT_MUTED);
        lblSeuilAlert.setText("SEUIL D'ALERTE");

        spnQte.setFont(FONT_FIELD);
        spnQte.setBackground(fieldBg);
        spnQte.setForeground(TEXT_PRIMARY);

        spnSeuilA.setFont(FONT_FIELD);
        spnSeuilA.setBackground(fieldBg);
        spnSeuilA.setForeground(TEXT_PRIMARY);

        // ── Boutons formulaire ─────────────────────
        jPanel1.setBackground(BG_FORM);
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));

        btnEnregistrer.setBackground(GOLD);
        btnEnregistrer.setForeground(BG_DARK);
        btnEnregistrer.setFont(FONT_BTN);
        btnEnregistrer.setText("✓ Enregistrer");
        btnEnregistrer.setBorderPainted(false);
        btnEnregistrer.setFocusPainted(false);
        btnEnregistrer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEnregistrer.setPreferredSize(new Dimension(150, 36));
        btnEnregistrer.addActionListener(this::btnEnregistrerActionPerformed);

        btnAnnuler.setBackground(new Color(50, 45, 36));
        btnAnnuler.setForeground(TEXT_MUTED);
        btnAnnuler.setFont(FONT_BTN);
        btnAnnuler.setText("Annuler");
        btnAnnuler.setBorderPainted(false);
        btnAnnuler.setFocusPainted(false);
        btnAnnuler.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAnnuler.setPreferredSize(new Dimension(110, 36));
        btnAnnuler.addActionListener(this::btnAnnulerActionPerformed);

        jPanel1.add(btnEnregistrer);
        jPanel1.add(btnAnnuler);

        // ── Layout du formulaire ───────────────────
        javax.swing.GroupLayout pnlFormLayout = new javax.swing.GroupLayout(pnlForm);
        pnlForm.setLayout(pnlFormLayout);
        pnlFormLayout.setHorizontalGroup(
            pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addComponent(lblNom, 175, 175, 175).addGap(18)
                        .addComponent(lblMarque, 175, 175, 175))
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addComponent(txtNom, 175, 175, 175).addGap(18)
                        .addComponent(txtMarque, 175, 175, 175))
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addComponent(lblcat, 175, 175, 175).addGap(18)
                        .addComponent(lblCont, 175, 175, 175))
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addComponent(cmbCat, 175, 175, 175).addGap(18)
                        .addComponent(spnCont, 175, 175, 175))
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addComponent(lblPrixA, 175, 175, 175).addGap(18)
                        .addComponent(lblPrixV, 175, 175, 175))
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addComponent(spnPrixA, 175, 175, 175).addGap(18)
                        .addComponent(spnPrixV, 175, 175, 175))
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addComponent(lblQte, 175, 175, 175).addGap(18)
                        .addComponent(lblSeuilAlert, 175, 175, 175))
                    .addGroup(pnlFormLayout.createSequentialGroup()
                        .addComponent(spnQte, 175, 175, 175).addGap(18)
                        .addComponent(spnSeuilA, 175, 175, 175)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlFormLayout.setVerticalGroup(
            pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormLayout.createSequentialGroup()
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNom, 28, 28, 28).addComponent(lblMarque, 28, 28, 28))
                .addGap(6)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtNom, 38, 38, 38).addComponent(txtMarque, 38, 38, 38))
                .addGap(14)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblcat, 28, 28, 28).addComponent(lblCont, 28, 28, 28))
                .addGap(6)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbCat, 38, 38, 38).addComponent(spnCont, 38, 38, 38))
                .addGap(14)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPrixA, 28, 28, 28).addComponent(lblPrixV, 28, 28, 28))
                .addGap(6)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spnPrixA, 38, 38, 38).addComponent(spnPrixV, 38, 38, 38))
                .addGap(14)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblQte, 28, 28, 28).addComponent(lblSeuilAlert, 28, 28, 28))
                .addGap(6)
                .addGroup(pnlFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spnQte, 38, 38, 38).addComponent(spnSeuilA, 38, 38, 38))
                .addGap(16)
                .addComponent(jPanel1, 44, 44, 44)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        pnlCenter.add(pnlForm, java.awt.BorderLayout.LINE_END);

        // ── Panel tableau (centre) ─────────────────
        pnlTableau.setBackground(BG_DARK);
        pnlTableau.setLayout(new java.awt.BorderLayout(0, 8));

        lblCatProd.setFont(FONT_CAT_LBL);
        lblCatProd.setForeground(GOLD);
        lblCatProd.setText("🫙  Catalogue Produits");
        lblCatProd.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 2, 8, 0));
        pnlTableau.add(lblCatProd, java.awt.BorderLayout.NORTH);

        jTable1.setFont(FONT_TABLE);
        jTable1.setBackground(BG_PANEL);
        jTable1.setForeground(TEXT_PRIMARY);
        jTable1.setGridColor(BORDER);
        jTable1.setRowHeight(48);
        jTable1.setSelectionBackground(SEL_BG);
        jTable1.setSelectionForeground(SEL_FG);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{"id", "NOM", "MARQUE", "CATÉGORIE", "CONTENANCE", "PRIX ACHAT", "PRIX VENTE", "STOCK", "SEUIL", "ÉTAT"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });

        scrollProduits.setViewportView(jTable1);
        scrollProduits.setBorder(javax.swing.BorderFactory.createLineBorder(BORDER, 1));
        scrollProduits.getViewport().setBackground(BG_PANEL);
        pnlTableau.add(scrollProduits, java.awt.BorderLayout.CENTER);

        // ── Boutons Modifier / Supprimer ───────────
        pnlBoutons.setBackground(BG_DARK);
        pnlBoutons.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 0, 0));
        pnlBoutons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));

        btnModifier.setBackground(GOLD);
        btnModifier.setForeground(BG_DARK);
        btnModifier.setFont(FONT_BTN);
        btnModifier.setText("✏  Modifier");
        btnModifier.setBorderPainted(false);
        btnModifier.setFocusPainted(false);
        btnModifier.setEnabled(false);
        btnModifier.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnModifier.setPreferredSize(new Dimension(140, 38));
        btnModifier.addActionListener(this::btnModifierActionPerformed);

        btnSupprimer.setBackground(new Color(80, 30, 30));
        btnSupprimer.setForeground(new Color(255, 160, 140));
        btnSupprimer.setFont(FONT_BTN);
        btnSupprimer.setText("🗑  Supprimer");
        btnSupprimer.setBorderPainted(false);
        btnSupprimer.setFocusPainted(false);
        btnSupprimer.setEnabled(false);
        btnSupprimer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSupprimer.setPreferredSize(new Dimension(150, 38));
        btnSupprimer.addActionListener(this::btnSupprimerActionPerformed);

        pnlBoutons.add(btnModifier);
        pnlBoutons.add(btnSupprimer);
        pnlTableau.add(pnlBoutons, java.awt.BorderLayout.SOUTH);

        pnlCenter.add(pnlTableau, java.awt.BorderLayout.CENTER);
        add(pnlCenter, java.awt.BorderLayout.CENTER);
    }

    // ═══════════════════════════════════════════════
    //  ACTIONS
    // ═══════════════════════════════════════════════
    private void btnNouveauActionPerformed(java.awt.event.ActionEvent evt) {
        viderFormulaire();
        pnlForm.setVisible(true);
        txtNom.requestFocusInWindow();
    }

    private void btnAnnulerActionPerformed(java.awt.event.ActionEvent evt) {
        viderFormulaire();
        pnlForm.setVisible(false);
    }

    private void btnEnregistrerActionPerformed(java.awt.event.ActionEvent evt) {
        if (!validerSaisies()) return;
        try {
            ProduitDAO dao = new ProduitDAO();
            Produit p = new Produit();
            p.setNom(txtNom.getText().trim());
            p.setMarque(txtMarque.getText().trim());
            p.setCategorie(Produit.Categorie.valueOf(cmbCat.getSelectedItem().toString()));
            p.setContenanceMl((Integer) spnCont.getValue());
            p.setPrixAchat(new BigDecimal((Integer) spnPrixA.getValue()));
            p.setPrixVente(new BigDecimal((Integer) spnPrixV.getValue()));
            p.setQuantiteStock((Integer) spnQte.getValue());
            p.setSeuilAlerte((Integer) spnSeuilA.getValue());

            if (idEnEdition > 0) {
                p.setIdProduit(idEnEdition);
                boolean ok = dao.update(p);
                if (ok) JOptionPane.showMessageDialog(this, "✓ Produit modifié avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                else    JOptionPane.showMessageDialog(this, "Aucun produit mis à jour.", "Attention", JOptionPane.WARNING_MESSAGE);
            } else {
                int newId = dao.insert(p);
                if (newId > 0) JOptionPane.showMessageDialog(this, "✓ Produit ajouté (id=" + newId + ").", "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
            chargerTableau();
            viderFormulaire();
            pnlForm.setVisible(false);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur base de données :\n" + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnModifierActionPerformed(java.awt.event.ActionEvent evt) {
        if (idEnEdition <= 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit.", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        pnlForm.setVisible(true);
        txtNom.requestFocusInWindow();
        JOptionPane.showMessageDialog(this, "Modifiez les champs puis cliquez sur Enregistrer.", "Mode modification", JOptionPane.INFORMATION_MESSAGE);
    }

    private void btnSupprimerActionPerformed(java.awt.event.ActionEvent evt) {
        if (idEnEdition <= 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit.", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = jTable1.getSelectedRow();
        String nomProduit = jTable1.getValueAt(row, 1).toString();
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vraiment supprimer \"" + nomProduit + "\" ?\nCette action est irréversible.",
            "Confirmer la suppression", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                ProduitDAO dao = new ProduitDAO();
                boolean ok = dao.delete(idEnEdition);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "✓ Produit \"" + nomProduit + "\" supprimé.", "Suppression réussie", JOptionPane.INFORMATION_MESSAGE);
                    chargerTableau();
                    viderFormulaire();
                    pnlForm.setVisible(false);
                    btnModifier.setEnabled(false);
                    btnSupprimer.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Impossible de supprimer ce produit.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur base de données :\n" + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Variables declaration ──────────────────────
    private javax.swing.JButton btnAnnuler;
    private javax.swing.JButton btnEnregistrer;
    private javax.swing.JButton btnModifier;
    private javax.swing.JButton btnNouveau;
    private javax.swing.JButton btnSupprimer;
    private javax.swing.JComboBox<String> cmbCat;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblCatProd;
    private javax.swing.JLabel lblCont;
    private javax.swing.JLabel lblMarque;
    private javax.swing.JLabel lblNom;
    private javax.swing.JLabel lblPrixA;
    private javax.swing.JLabel lblPrixV;
    private javax.swing.JLabel lblQte;
    private javax.swing.JLabel lblSeuilAlert;
    private javax.swing.JLabel lblTitre;
    private javax.swing.JLabel lblcat;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelTitre;
    private javax.swing.JPanel pnlBoutons;
    private javax.swing.JPanel pnlCenter;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JPanel pnlTableau;
    private javax.swing.JScrollPane scrollProduits;
    private javax.swing.JSpinner spnCont;
    private javax.swing.JSpinner spnPrixA;
    private javax.swing.JSpinner spnPrixV;
    private javax.swing.JSpinner spnQte;
    private javax.swing.JSpinner spnSeuilA;
    private javax.swing.JTextField txtMarque;
    private javax.swing.JTextField txtNom;
}
