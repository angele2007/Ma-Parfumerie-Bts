/*
 * Panel Fournisseurs — Thème sombre luxe
 */
package parfumerie.vues;

import parfumerie.dao.FournisseurDAO;
import parfumerie.modeles.Fournisseur;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel Fournisseurs — Thème sombre luxe.
 * @author Angele
 */
public class panelFournisseur extends javax.swing.JPanel {

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
    private static final Color ERR_BG       = new Color(80, 20, 20);
    private static final Color ERR_BORDER   = new Color(200, 60, 60);

    // ── Polices ───────────────────────────────────
    private static final Font FONT_TITRE    = new Font("Baskerville Old Face", Font.BOLD, 22);
    private static final Font FONT_LABEL    = new Font("Baskerville Old Face", Font.PLAIN, 12);
    private static final Font FONT_FIELD    = new Font("Baskerville Old Face", Font.PLAIN, 14);
    private static final Font FONT_TABLE    = new Font("Baskerville Old Face", Font.PLAIN, 15);
    private static final Font FONT_HEADER   = new Font("Baskerville Old Face", Font.BOLD, 13);
    private static final Font FONT_BTN      = new Font("Baskerville Old Face", Font.PLAIN, 13);
    private static final Font FONT_CAT_LBL  = new Font("Baskerville Old Face", Font.BOLD, 16);

    // ─── État ─────────────────────────────────────
    private int     idEnEdition      = -1;
    private boolean modeModification = false;

    // ─── DAO / modèle ─────────────────────────────
    private FournisseurDAO    fournisseurDAO;
    private DefaultTableModel tableModel;
    private List<Object[]>    cacheTableau = new ArrayList<>();

    // =========================================================================
    //  CONSTRUCTEUR
    // =========================================================================
    public panelFournisseur() {
        try {
            fournisseurDAO = new FournisseurDAO();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Connexion BD impossible :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        initComponents();
        configurerTableau();
        configurerListeners();
        chargerTableau();
        pnlForm.setVisible(false);
    }

    // =========================================================================
    //  CONFIGURATION TABLEAU
    // =========================================================================
    private void configurerTableau() {
        tableModel = new DefaultTableModel(
            new String[]{"id_fournisseur", "NOM", "TÉLÉPHONE", "EMAIL", "ADRESSE"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        jTable1.setModel(tableModel);

        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(160);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(170);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(190);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Style
        jTable1.setFont(FONT_TABLE);
        jTable1.setRowHeight(46);
        jTable1.setBackground(BG_PANEL);
        jTable1.setForeground(TEXT_PRIMARY);
        jTable1.setGridColor(BORDER);
        jTable1.setSelectionBackground(SEL_BG);
        jTable1.setSelectionForeground(SEL_FG);
        jTable1.setShowVerticalLines(true);
        jTable1.setShowHorizontalLines(true);
        jTable1.setIntercellSpacing(new Dimension(0, 0));

        // En-tête
        JTableHeader header = jTable1.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(BG_DARK);
        header.setForeground(GOLD);
        header.setPreferredSize(new Dimension(header.getWidth(), 44));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GOLD));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.LEFT);

        scrollProduits.getViewport().setBackground(BG_PANEL);
        scrollProduits.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        // Renderer lignes alternées + email en doré
        jTable1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(FONT_TABLE);
                if (isSelected) {
                    setBackground(SEL_BG); setForeground(SEL_FG);
                } else {
                    setBackground(row % 2 == 0 ? BG_PANEL : BG_ROW_ALT);
                    setForeground(TEXT_PRIMARY);
                    if (column == 2) setForeground(TEXT_MUTED);         // Téléphone
                    if (column == 3) setForeground(GOLD_LIGHT);         // Email
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

        // Sélection ligne
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = jTable1.getSelectedRow();
                boolean sel = row >= 0;
                btnModifier.setEnabled(sel);
                btnSupprimer.setEnabled(sel);
                btnVoirProduits.setEnabled(sel);
                if (sel) {
                    idEnEdition = (int) tableModel.getValueAt(row, 0);
                    txtNom    .setText(tableModel.getValueAt(row, 1).toString());
                    txtTel    .setText(tableModel.getValueAt(row, 2).toString());
                    txtEmail  .setText(tableModel.getValueAt(row, 3).toString());
                    txtAdresse.setText(tableModel.getValueAt(row, 4).toString());
                    resetAllFields();
                }
            }
        });

        // Nouveau
        btnNouveau.addActionListener(e -> {
            modeModification = false; idEnEdition = -1;
            viderFormulaire();
            pnlForm.setVisible(true);
            jTable1.clearSelection();
            btnModifier.setEnabled(false); btnSupprimer.setEnabled(false); btnVoirProduits.setEnabled(false);
            revalidate(); repaint();
        });

        // Enregistrer
        btnEnregistrer.addActionListener(e -> enregistrer());

        // Annuler
        btnAnnuler.addActionListener(e -> {
            viderFormulaire(); pnlForm.setVisible(false);
            idEnEdition = -1; modeModification = false;
            jTable1.clearSelection();
            btnModifier.setEnabled(false); btnSupprimer.setEnabled(false); btnVoirProduits.setEnabled(false);
            revalidate(); repaint();
        });

        // Modifier
        btnModifier.addActionListener(e -> {
            if (idEnEdition < 0) {
                JOptionPane.showMessageDialog(this, "Sélectionnez une ligne.",
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE); return;
            }
            modeModification = true;
            pnlForm.setVisible(true);
            txtNom.requestFocusInWindow();
            revalidate(); repaint();
        });

        // Supprimer
        btnSupprimer.addActionListener(e -> supprimer());

        // Voir produits
        btnVoirProduits.addActionListener(e -> afficherProduitsFournisseur());

        // Recherche temps réel
        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String t = txtRecherche.getText().trim();
                if (!t.equals("Rechercher...")) filtrerParRecherche(t);
            }
        });
        txtRecherche.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (txtRecherche.getText().equals("Rechercher...")) {
                    txtRecherche.setText(""); txtRecherche.setForeground(TEXT_PRIMARY);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (txtRecherche.getText().isEmpty()) {
                    txtRecherche.setText("Rechercher..."); txtRecherche.setForeground(TEXT_MUTED);
                }
            }
        });

        // Validation à la perte de focus
        txtNom.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (txtNom.getText().trim().isEmpty()) marquerErreur(txtNom, "Champ obligatoire");
                else resetField(txtNom);
            }
        });
        txtTel.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                String v = txtTel.getText().trim();
                if (!v.isEmpty() && !v.matches("[+\\d\\s\\-\\.()]{6,20}"))
                    marquerErreur(txtTel, "Format invalide (ex: +228 90 00 11 22)");
                else resetField(txtTel);
            }
        });
        txtEmail.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                String v = txtEmail.getText().trim();
                if (!v.isEmpty() && !v.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$"))
                    marquerErreur(txtEmail, "Format email invalide (ex: nom@domaine.com)");
                else resetField(txtEmail);
            }
        });

        // Filtre saisie : nom → lettres uniquement
        appliquerFiltreTexte(txtNom);
        // Filtre saisie : tel → chiffres + +, espace, tiret
        appliquerFiltreTelephone(txtTel);
    }

    // =========================================================================
    //  CHARGEMENT
    // =========================================================================
    private void chargerTableau() {
        if (fournisseurDAO == null) return;
        cacheTableau.clear();
        tableModel.setRowCount(0);
        try {
            for (Fournisseur f : fournisseurDAO.findAll()) {
                Object[] row = {
                    f.getIdFournisseur(),
                    f.getNom(),
                    f.getTelephone() != null ? f.getTelephone() : "",
                    f.getEmail()     != null ? f.getEmail()     : "",
                    f.getAdresse()   != null ? f.getAdresse()   : ""
                };
                cacheTableau.add(row);
                tableModel.addRow(row);
            }
            lblCatProd.setText("🏢  Fournisseurs  (" + cacheTableau.size() + ")");
        } catch (SQLException ex) { afficherErreurBD(ex); }
    }

    // =========================================================================
    //  RECHERCHE
    // =========================================================================
    private void filtrerParRecherche(String terme) {
        tableModel.setRowCount(0);
        if (terme.isEmpty()) {
            for (Object[] r : cacheTableau) tableModel.addRow(r);
            lblCatProd.setText("🏢  Fournisseurs  (" + cacheTableau.size() + ")");
            return;
        }
        String t = terme.toLowerCase(); int count = 0;
        for (Object[] r : cacheTableau) {
            String nom  = r[1] != null ? r[1].toString().toLowerCase() : "";
            String tel  = r[2] != null ? r[2].toString().toLowerCase() : "";
            String mail = r[3] != null ? r[3].toString().toLowerCase() : "";
            String adr  = r[4] != null ? r[4].toString().toLowerCase() : "";
            if (nom.contains(t) || tel.contains(t) || mail.contains(t) || adr.contains(t)) {
                tableModel.addRow(r); count++;
            }
        }
        lblCatProd.setText("🏢  Résultats  (" + count + ")");
    }

    // =========================================================================
    //  ENREGISTRER
    // =========================================================================
    private void enregistrer() {
        resetAllFields();
        boolean ok = true;
        if (txtNom.getText().trim().isEmpty()) {
            marquerErreur(txtNom, "Champ obligatoire"); ok = false;
        }
        String tel = txtTel.getText().trim();
        if (!tel.isEmpty() && !tel.matches("[+\\d\\s\\-\\.()]{6,20}")) {
            marquerErreur(txtTel, "Format invalide"); ok = false;
        }
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            marquerErreur(txtEmail, "Format email invalide"); ok = false;
        }
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Corrigez les champs en rouge.",
                "Saisie invalide", JOptionPane.WARNING_MESSAGE); return;
        }
        try {
            Fournisseur f = new Fournisseur();
            f.setNom      (txtNom    .getText().trim());
            f.setTelephone(txtTel    .getText().trim());
            f.setEmail    (txtEmail  .getText().trim());
            f.setAdresse  (txtAdresse.getText().trim());

            if (modeModification && idEnEdition >= 0) {
                f.setIdFournisseur(idEnEdition);
                boolean res = fournisseurDAO.update(f);
                if (!res) { JOptionPane.showMessageDialog(this, "Impossible de modifier.", "Erreur", JOptionPane.ERROR_MESSAGE); return; }
                JOptionPane.showMessageDialog(this, "✓ Fournisseur modifié.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                int newId = fournisseurDAO.insert(f);
                if (newId <= 0) { JOptionPane.showMessageDialog(this, "Impossible d'enregistrer.", "Erreur", JOptionPane.ERROR_MESSAGE); return; }
                JOptionPane.showMessageDialog(this, "✓ Fournisseur enregistré.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
            chargerTableau(); viderFormulaire(); pnlForm.setVisible(false);
            idEnEdition = -1; modeModification = false;
            btnModifier.setEnabled(false); btnSupprimer.setEnabled(false); btnVoirProduits.setEnabled(false);
            revalidate(); repaint();
        } catch (SQLException ex) { afficherErreurBD(ex); }
    }

    // =========================================================================
    //  SUPPRIMER
    // =========================================================================
    private void supprimer() {
        if (idEnEdition < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une ligne.", "Aucune sélection", JOptionPane.WARNING_MESSAGE); return; }
        int row = jTable1.getSelectedRow();
        String nom = tableModel.getValueAt(row, 1).toString();
        int choix = JOptionPane.showConfirmDialog(this,
            "Supprimer \"" + nom + "\" ?\nIrréversible.",
            "Confirmer", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choix == JOptionPane.YES_OPTION) {
            try {
                if (fournisseurDAO.delete(idEnEdition)) {
                    JOptionPane.showMessageDialog(this, "✓ Fournisseur supprimé.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    chargerTableau(); viderFormulaire(); pnlForm.setVisible(false);
                    idEnEdition = -1; btnModifier.setEnabled(false); btnSupprimer.setEnabled(false);
                    revalidate(); repaint();
                } else {
                    JOptionPane.showMessageDialog(this, "Impossible de supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) { afficherErreurBD(ex); }
        }
    }

    // =========================================================================
    //  CONTRÔLES DE SAISIE
    // =========================================================================
    private void appliquerFiltreTexte(JTextField f) {
        ((javax.swing.text.AbstractDocument) f.getDocument())
            .setDocumentFilter(new javax.swing.text.DocumentFilter() {
                @Override public void insertString(FilterBypass fb, int off, String t,
                        javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                    if (t != null && t.matches("[\\p{L}\\s'\\-\\.]*")) super.insertString(fb, off, t, a);
                }
                @Override public void replace(FilterBypass fb, int off, int len, String t,
                        javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                    if (t != null && t.matches("[\\p{L}\\s'\\-\\.]*")) super.replace(fb, off, len, t, a);
                }
            });
    }

    private void appliquerFiltreTelephone(JTextField f) {
        ((javax.swing.text.AbstractDocument) f.getDocument())
            .setDocumentFilter(new javax.swing.text.DocumentFilter() {
                @Override public void insertString(FilterBypass fb, int off, String t,
                        javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                    if (t != null && t.matches("[+\\d\\s\\-\\.()]*")) super.insertString(fb, off, t, a);
                }
                @Override public void replace(FilterBypass fb, int off, int len, String t,
                        javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                    if (t != null && t.matches("[+\\d\\s\\-\\.()]*")) super.replace(fb, off, len, t, a);
                }
            });
    }

    private void marquerErreur(JTextField f, String tip) {
        f.setBackground(ERR_BG);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ERR_BORDER),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        f.setToolTipText(tip);
    }

    private void resetField(JTextField f) {
        f.setBackground(BG_FIELD);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        f.setToolTipText(null);
    }

    private void resetAllFields() {
        resetField(txtNom); resetField(txtTel); resetField(txtEmail); resetField(txtAdresse);
    }

    // =========================================================================
    //  UTILITAIRES
    // =========================================================================
    // =========================================================================
    //  DIALOG PRODUITS DU FOURNISSEUR
    // =========================================================================
    private void afficherProduitsFournisseur() {
        if (idEnEdition < 0) return;
        try {
            parfumerie.modeles.Fournisseur f = fournisseurDAO.findById(idEnEdition);
            if (f == null) {
                JOptionPane.showMessageDialog(this, "Fournisseur introuvable.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
            ProduitParFournisseurDialog dlg = new ProduitParFournisseurDialog(frame, f);
            dlg.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur BD :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viderFormulaire() {
        txtNom.setText(""); txtTel.setText("");
        txtEmail.setText(""); txtAdresse.setText("");
        resetAllFields();
    }

    private void afficherErreurBD(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Erreur BD :\n" + ex.getMessage(), "Erreur BD", JOptionPane.ERROR_MESSAGE);
    }

    private JTextField makeField() {
        JTextField f = new JTextField();
        f.setFont(FONT_FIELD); f.setBackground(BG_FIELD);
        f.setForeground(TEXT_PRIMARY); f.setCaretColor(GOLD);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        f.setPreferredSize(new Dimension(160, 34));
        return f;
    }

    private void addLbl(JPanel p, String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(FONT_LABEL); l.setForeground(TEXT_MUTED); l.setBackground(BG_PANEL);
        p.add(l);
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
        pnlForm        = new JPanel();
        pnlBtn         = new JPanel();
        btnAnnuler     = new JButton();
        btnEnregistrer = new JButton();
        pnlBoutons     = new JPanel();
        btnModifier    = new JButton();
        btnSupprimer   = new JButton();
        btnVoirProduits = new JButton();
        pnlTableau     = new JPanel();
        lblCatProd     = new JLabel();
        scrollProduits = new JScrollPane();
        jTable1        = new JTable();
        txtRecherche   = new JTextField();

        // Champs formulaire
        txtNom     = makeField();
        txtTel     = makeField();
        txtEmail   = makeField();
        txtAdresse = makeField();

        // ── Panel principal ────────────────────────
        setBackground(BG_DARK);
        setLayout(new BorderLayout());

        // ── Header ─────────────────────────────────
        panelHeader.setBackground(BG_DARK);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        panelHeader.setLayout(new BorderLayout(12, 0));

        panelTitre.setBackground(BG_DARK);
        panelTitre.setLayout(new BoxLayout(panelTitre, BoxLayout.Y_AXIS));
        lblTitre.setFont(FONT_TITRE); lblTitre.setForeground(TEXT_PRIMARY);
        lblTitre.setText("Fournisseurs");
        panelTitre.add(lblTitre);
        panelHeader.add(panelTitre, BorderLayout.CENTER);

        // Séparateur doré
        JPanel sep = new JPanel();
        sep.setBackground(GOLD); sep.setPreferredSize(new Dimension(1, 1));
        panelHeader.add(sep, BorderLayout.SOUTH);

        // Recherche + bouton
        JPanel pnlHeaderRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlHeaderRight.setBackground(BG_DARK);

        txtRecherche.setFont(FONT_FIELD); txtRecherche.setBackground(BG_FIELD);
        txtRecherche.setForeground(TEXT_MUTED); txtRecherche.setCaretColor(GOLD);
        txtRecherche.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        txtRecherche.setPreferredSize(new Dimension(200, 36));
        txtRecherche.setText("Rechercher...");
        pnlHeaderRight.add(txtRecherche);

        btnNouveau.setBackground(GOLD); btnNouveau.setForeground(BG_DARK);
        btnNouveau.setFont(FONT_BTN); btnNouveau.setText("+ Ajouter");
        btnNouveau.setBorderPainted(false); btnNouveau.setFocusPainted(false);
        btnNouveau.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNouveau.setPreferredSize(new Dimension(130, 36));
        pnlHeaderRight.add(btnNouveau);
        panelHeader.add(pnlHeaderRight, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // ── Centre ────────────────────────────────
        pnlCenter.setBackground(BG_DARK);
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(12, 24, 24, 24));
        pnlCenter.setLayout(new BorderLayout(16, 0));

        // ── Tableau ────────────────────────────────
        pnlTableau.setBackground(BG_DARK);
        pnlTableau.setLayout(new BorderLayout(0, 8));

        lblCatProd.setFont(FONT_CAT_LBL); lblCatProd.setForeground(GOLD);
        lblCatProd.setText("🏢  Fournisseurs");
        lblCatProd.setBorder(BorderFactory.createEmptyBorder(0, 2, 8, 0));
        pnlTableau.add(lblCatProd, BorderLayout.NORTH);

        jTable1.setBackground(BG_PANEL); jTable1.setForeground(TEXT_PRIMARY);
        jTable1.setGridColor(BORDER); jTable1.setRowHeight(46); jTable1.setFont(FONT_TABLE);
        jTable1.setSelectionBackground(SEL_BG); jTable1.setSelectionForeground(SEL_FG);

        scrollProduits.setViewportView(jTable1);
        scrollProduits.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scrollProduits.getViewport().setBackground(BG_PANEL);
        pnlTableau.add(scrollProduits, BorderLayout.CENTER);

        // Boutons tableau
        pnlBoutons.setBackground(BG_DARK);
        pnlBoutons.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        pnlBoutons.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        btnModifier.setBackground(GOLD); btnModifier.setForeground(BG_DARK);
        btnModifier.setFont(FONT_BTN); btnModifier.setText("Modifier");
        btnModifier.setBorderPainted(false); btnModifier.setFocusPainted(false);
        btnModifier.setEnabled(false); btnModifier.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModifier.setPreferredSize(new Dimension(120, 36));

        btnSupprimer.setBackground(new Color(80, 30, 30));
        btnSupprimer.setForeground(new Color(255, 160, 140));
        btnSupprimer.setFont(FONT_BTN); btnSupprimer.setText("Supprimer");
        btnSupprimer.setBorderPainted(false); btnSupprimer.setFocusPainted(false);
        btnSupprimer.setEnabled(false); btnSupprimer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSupprimer.setPreferredSize(new Dimension(120, 36));

        btnVoirProduits.setBackground(new Color(30, 40, 65));
        btnVoirProduits.setForeground(new Color(140, 180, 255));
        btnVoirProduits.setFont(FONT_BTN); btnVoirProduits.setText("Voir produits");
        btnVoirProduits.setBorderPainted(false); btnVoirProduits.setFocusPainted(false);
        btnVoirProduits.setEnabled(false); btnVoirProduits.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoirProduits.setPreferredSize(new Dimension(140, 36));

        pnlBoutons.add(btnModifier); pnlBoutons.add(btnSupprimer); pnlBoutons.add(btnVoirProduits);
        pnlTableau.add(pnlBoutons, BorderLayout.SOUTH);
        pnlCenter.add(pnlTableau, BorderLayout.CENTER);

        // ── Formulaire (droite) — COMPACT ──────────
        pnlForm.setBackground(BG_PANEL);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        // Largeur fixe réduite + hauteur naturelle
        pnlForm.setPreferredSize(new Dimension(340, 0));
        pnlForm.setMaximumSize(new Dimension(340, Integer.MAX_VALUE));
        pnlForm.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.gridwidth = 1;

        // Titre formulaire
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 14, 0);
        JLabel lblTitreForm = new JLabel("FOURNISSEUR");
        lblTitreForm.setFont(new Font("Baskerville Old Face", Font.BOLD, 14));
        lblTitreForm.setForeground(GOLD);
        pnlForm.add(lblTitreForm, gbc);

        // ── NOM / TÉLÉPHONE ─────────────────────────
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0; gbc.insets = new Insets(0, 0, 4, 6);
        JLabel lNom = new JLabel("NOM *"); lNom.setFont(FONT_LABEL); lNom.setForeground(TEXT_MUTED);
        pnlForm.add(lNom, gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 0, 4, 0);
        JLabel lTel = new JLabel("TÉLÉPHONE"); lTel.setFont(FONT_LABEL); lTel.setForeground(TEXT_MUTED);
        pnlForm.add(lTel, gbc);

        gbc.gridy = 2; gbc.gridx = 0; gbc.insets = new Insets(0, 0, 12, 6);
        txtNom.setPreferredSize(new Dimension(140, 34));
        pnlForm.add(txtNom, gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 0, 12, 0);
        txtTel.setPreferredSize(new Dimension(140, 34));
        pnlForm.add(txtTel, gbc);

        // ── EMAIL ────────────────────────────────────
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 4, 0);
        JLabel lEmail = new JLabel("EMAIL"); lEmail.setFont(FONT_LABEL); lEmail.setForeground(TEXT_MUTED);
        pnlForm.add(lEmail, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 12, 0);
        txtEmail.setPreferredSize(new Dimension(300, 34));
        pnlForm.add(txtEmail, gbc);

        // ── ADRESSE ──────────────────────────────────
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 4, 0);
        JLabel lAdr = new JLabel("ADRESSE"); lAdr.setFont(FONT_LABEL); lAdr.setForeground(TEXT_MUTED);
        pnlForm.add(lAdr, gbc);

        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 16, 0);
        txtAdresse.setPreferredSize(new Dimension(300, 34));
        pnlForm.add(txtAdresse, gbc);

        // ── Boutons ────────────────────────────────
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 0, 0);
        pnlBtn.setBackground(BG_PANEL);
        pnlBtn.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        btnEnregistrer.setBackground(GOLD); btnEnregistrer.setForeground(BG_DARK);
        btnEnregistrer.setFont(FONT_BTN); btnEnregistrer.setText("Enregistrer");
        btnEnregistrer.setBorderPainted(false); btnEnregistrer.setFocusPainted(false);
        btnEnregistrer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEnregistrer.setPreferredSize(new Dimension(130, 34));

        btnAnnuler.setBackground(new Color(50, 45, 36)); btnAnnuler.setForeground(TEXT_MUTED);
        btnAnnuler.setFont(FONT_BTN); btnAnnuler.setText("Annuler");
        btnAnnuler.setBorderPainted(false); btnAnnuler.setFocusPainted(false);
        btnAnnuler.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnnuler.setPreferredSize(new Dimension(100, 34));

        pnlBtn.add(btnEnregistrer); pnlBtn.add(btnAnnuler);
        pnlForm.add(pnlBtn, gbc);

        pnlCenter.add(pnlForm, BorderLayout.LINE_END);
        add(pnlCenter, BorderLayout.CENTER);
    }

    // ── Variables declaration ──────────────────────
    private JButton       btnAnnuler, btnEnregistrer;
    private JButton       btnModifier, btnNouveau, btnSupprimer, btnVoirProduits;
    private JLabel        lblCatProd, lblTitre;
    private JTable        jTable1;
    private JPanel        panelHeader, panelTitre;
    private JPanel        pnlBoutons, pnlBtn;
    private JPanel        pnlCenter, pnlForm, pnlTableau;
    private JScrollPane   scrollProduits;
    private JTextField    txtAdresse, txtEmail, txtNom, txtTel, txtRecherche;
}
