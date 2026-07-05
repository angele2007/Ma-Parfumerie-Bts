/*
 * Panel Factures — Thème sombre luxe + calendrier + contrôles + impression
 */
package parfumerie.vues;

import parfumerie.dao.FactureDAO;
import parfumerie.dao.LigneFactureDAO;
import parfumerie.dao.ProduitDAO;
import parfumerie.modeles.Facture;
import parfumerie.modeles.Facture.Statut;
import parfumerie.modeles.LigneFacture;
import parfumerie.modele.Produit;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Panel Factures — Thème sombre luxe.
 * Nouveautés : calendrier popup date, contrôles de saisie, bouton Imprimer.
 * @author Angele
 */
public class panelFacture extends javax.swing.JPanel {

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
    private static final Color GREEN_SOFT   = new Color(100, 200, 130);
    private static final Color RED_SOFT     = new Color(255, 110, 80);
    private static final Color ORANGE_SOFT  = new Color(255, 180, 60);
    private static final Color ERR_BG       = new Color(80, 20, 20);
    private static final Color ERR_BORDER   = new Color(200, 60, 60);

    // ── Polices ───────────────────────────────────
    private static final Font FONT_TITRE    = new Font("Baskerville Old Face", Font.BOLD, 22);
    private static final Font FONT_LABEL    = new Font("Baskerville Old Face", Font.PLAIN, 12);
    private static final Font FONT_FIELD    = new Font("Baskerville Old Face", Font.PLAIN, 14);
    private static final Font FONT_TABLE    = new Font("Baskerville Old Face", Font.PLAIN, 14);
    private static final Font FONT_HEADER   = new Font("Baskerville Old Face", Font.BOLD, 13);
    private static final Font FONT_BTN      = new Font("Baskerville Old Face", Font.PLAIN, 13);
    private static final Font FONT_FORM_TIT = new Font("Baskerville Old Face", Font.BOLD, 15);

    // ─── DAO ──────────────────────────────────────
    private FactureDAO      factureDAO;
    private LigneFactureDAO ligneDAO;
    private ProduitDAO      produitDAO;

    // ─── État ─────────────────────────────────────
    private int     idEnEdition  = -1;
    private List<Produit>      listeProduits = new ArrayList<>();
    private List<LigneFacture> lignesForm    = new ArrayList<>();
    private List<Object[]>     cacheTableau  = new ArrayList<>();
    private DefaultTableModel  tableModel;

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    // =========================================================================
    //  CONSTRUCTEUR
    // =========================================================================
    public panelFacture() {
        try {
            factureDAO = new FactureDAO();
            ligneDAO   = new LigneFactureDAO();
            produitDAO = new ProduitDAO();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Connexion BD impossible :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        construireUI();
        configurerTableau();
        configurerListeners();
        chargerProduits();
        chargerTableau(null);
        pnlForm.setVisible(false);
    }

    // =========================================================================
    //  CONSTRUCTION UI
    // =========================================================================
    private void construireUI() {
        setBackground(BG_DARK);
        setLayout(new BorderLayout());

        // ── HEADER ─────────────────────────────────
        JPanel panelHeader = new JPanel(new BorderLayout(12, 0));
        panelHeader.setBackground(BG_DARK);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        JPanel panelTitre = new JPanel();
        panelTitre.setBackground(BG_DARK);
        panelTitre.setLayout(new BoxLayout(panelTitre, BoxLayout.Y_AXIS));
        JLabel lblTitre = new JLabel("Factures");
        lblTitre.setFont(FONT_TITRE);
        lblTitre.setForeground(TEXT_PRIMARY);
        panelTitre.add(lblTitre);
        panelHeader.add(panelTitre, BorderLayout.CENTER);

        JPanel sep = new JPanel();
        sep.setBackground(GOLD);
        sep.setPreferredSize(new Dimension(1, 1));
        panelHeader.add(sep, BorderLayout.SOUTH);

        // Recherche + bouton nouvelle facture
        JPanel pnlHeaderRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlHeaderRight.setBackground(BG_DARK);

        jTextField1 = new JTextField();
        jTextField1.setFont(FONT_FIELD);
        jTextField1.setBackground(BG_FIELD);
        jTextField1.setForeground(TEXT_MUTED);
        jTextField1.setCaretColor(GOLD);
        jTextField1.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        jTextField1.setPreferredSize(new Dimension(220, 36));
        jTextField1.setText("Rechercher une facture...");
        jTextField1.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (jTextField1.getText().equals("Rechercher une facture...")) {
                    jTextField1.setText(""); jTextField1.setForeground(TEXT_PRIMARY);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (jTextField1.getText().isEmpty()) {
                    jTextField1.setText("Rechercher une facture..."); jTextField1.setForeground(TEXT_MUTED);
                }
            }
        });
        pnlHeaderRight.add(jTextField1);

        btnNouveau = makeBtn("+ Nouvelle Facture", GOLD, BG_DARK, 175);
        pnlHeaderRight.add(btnNouveau);
        panelHeader.add(pnlHeaderRight, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // ── CENTRE ────────────────────────────────
        JPanel pnlCenter = new JPanel(new BorderLayout(16, 0));
        pnlCenter.setBackground(BG_DARK);
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(12, 24, 24, 24));

        // ── ZONE TABLEAU ───────────────────────────
        JPanel pnlTableauZone = new JPanel(new BorderLayout(0, 8));
        pnlTableauZone.setBackground(BG_DARK);

        // Filtres
        JPanel jPanel6 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        jPanel6.setBackground(BG_DARK);
        jPanel6.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        ButtonGroup bg = new ButtonGroup();
        tglToutes  = creerFiltreBtn("Toutes",  true,  bg, jPanel6);
        tglImpaye  = creerFiltreBtn("Impayée", false, bg, jPanel6);
        tglPaye    = creerFiltreBtn("Payée",   false, bg, jPanel6);
        tglAnnuler = creerFiltreBtn("Annulée", false, bg, jPanel6);
        pnlTableauZone.add(jPanel6, BorderLayout.NORTH);

        // Compteur
        JPanel pnlCompteur = new JPanel(new BorderLayout());
        pnlCompteur.setBackground(BG_DARK);
        pnlCompteur.setBorder(BorderFactory.createEmptyBorder(0, 2, 6, 2));
        JLabel lblFacLabel = new JLabel("🧾  Factures");
        lblFacLabel.setFont(new Font("Baskerville Old Face", Font.BOLD, 16));
        lblFacLabel.setForeground(GOLD);
        pnlCompteur.add(lblFacLabel, BorderLayout.LINE_START);
        jLabel2 = new JLabel("");
        jLabel2.setFont(FONT_LABEL);
        jLabel2.setForeground(TEXT_MUTED);
        pnlCompteur.add(jLabel2, BorderLayout.LINE_END);

        JPanel pnlTableauWrap = new JPanel(new BorderLayout(0, 6));
        pnlTableauWrap.setBackground(BG_DARK);
        pnlTableauWrap.add(pnlCompteur, BorderLayout.NORTH);

        jTable1 = new JTable();
        jTable1.setFont(FONT_TABLE);
        jTable1.setRowHeight(44);
        jTable1.setBackground(BG_PANEL);
        jTable1.setForeground(TEXT_PRIMARY);
        jTable1.setGridColor(BORDER);
        jTable1.setSelectionBackground(SEL_BG);
        jTable1.setSelectionForeground(SEL_FG);
        jTable1.setShowVerticalLines(true);
        jTable1.setShowHorizontalLines(true);
        jTable1.setIntercellSpacing(new Dimension(0, 0));

        JScrollPane jScrollPane1 = new JScrollPane(jTable1);
        jScrollPane1.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        jScrollPane1.getViewport().setBackground(BG_PANEL);
        pnlTableauWrap.add(jScrollPane1, BorderLayout.CENTER);

        // Boutons sous tableau
        JPanel pnlBoutons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlBoutons.setBackground(BG_DARK);
        pnlBoutons.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        btnModifierStatut = new JButton("Marquer Payee");
        btnModifierStatut.setBackground(new Color(30, 60, 35));
        btnModifierStatut.setForeground(GREEN_SOFT);
        btnModifierStatut.setFont(FONT_BTN); btnModifierStatut.setBorderPainted(false);
        btnModifierStatut.setFocusPainted(false); btnModifierStatut.setEnabled(false);
        btnModifierStatut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModifierStatut.setPreferredSize(new Dimension(155, 36));

        btnAnnulerFac = new JButton("Annuler facture");
        btnAnnulerFac.setBackground(new Color(55, 35, 10));
        btnAnnulerFac.setForeground(ORANGE_SOFT);
        btnAnnulerFac.setFont(FONT_BTN); btnAnnulerFac.setBorderPainted(false);
        btnAnnulerFac.setFocusPainted(false); btnAnnulerFac.setEnabled(false);
        btnAnnulerFac.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnnulerFac.setPreferredSize(new Dimension(140, 36));

        btnSupprimer = new JButton("Supprimer");
        btnSupprimer.setBackground(new Color(80, 30, 30));
        btnSupprimer.setForeground(RED_SOFT);
        btnSupprimer.setFont(FONT_BTN); btnSupprimer.setBorderPainted(false);
        btnSupprimer.setFocusPainted(false); btnSupprimer.setEnabled(false);
        btnSupprimer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSupprimer.setPreferredSize(new Dimension(120, 36));

        // ── Bouton Imprimer ────────────────────────
        btnImprimer = new JButton("Imprimer");
        btnImprimer.setBackground(new Color(30, 40, 65));
        btnImprimer.setForeground(new Color(140, 180, 255));
        btnImprimer.setFont(FONT_BTN); btnImprimer.setBorderPainted(false);
        btnImprimer.setFocusPainted(false); btnImprimer.setEnabled(false);
        btnImprimer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnImprimer.setPreferredSize(new Dimension(110, 36));

        // ── Bouton Modifier ────────────────────────
        btnModifier = new JButton("✏  Modifier");
        btnModifier.setBackground(new Color(40, 50, 25));
        btnModifier.setForeground(new Color(160, 220, 100));
        btnModifier.setFont(FONT_BTN); btnModifier.setBorderPainted(false);
        btnModifier.setFocusPainted(false); btnModifier.setEnabled(false);
        btnModifier.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModifier.setPreferredSize(new Dimension(130, 36));

        pnlBoutons.add(btnModifierStatut);
        pnlBoutons.add(btnModifier);
        pnlBoutons.add(btnAnnulerFac);
        pnlBoutons.add(btnImprimer);
        pnlBoutons.add(btnSupprimer);
        pnlTableauWrap.add(pnlBoutons, BorderLayout.SOUTH);
        pnlTableauZone.add(pnlTableauWrap, BorderLayout.CENTER);
        pnlCenter.add(pnlTableauZone, BorderLayout.CENTER);

        // ── FORMULAIRE ─────────────────────────────
        pnlForm = new JPanel();
        pnlForm.setBackground(BG_PANEL);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(20, 18, 20, 18)));
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setPreferredSize(new Dimension(420, 0));

        lblFormTitre = new JLabel("NOUVELLE FACTURE");
        lblFormTitre.setFont(FONT_FORM_TIT);
        lblFormTitre.setForeground(GOLD);
        lblFormTitre.setAlignmentX(LEFT_ALIGNMENT);
        pnlForm.add(lblFormTitre);
        pnlForm.add(Box.createVerticalStrut(16));

        // ── Champs CLIENT / TÉLÉPHONE ──────────────
        JPanel pnlChamps = new JPanel(new GridLayout(0, 2, 10, 8));
        pnlChamps.setBackground(BG_PANEL);
        pnlChamps.setAlignmentX(LEFT_ALIGNMENT);

        addLabel(pnlChamps, "CLIENT *");
        addLabel(pnlChamps, "TÉLÉPHONE");
        txtNomClient = styleField(new JTextField());
        txtTelephone = styleField(new JTextField());
        pnlChamps.add(txtNomClient);
        pnlChamps.add(txtTelephone);

        // ── DATE avec bouton calendrier ────────────
        addLabel(pnlChamps, "DATE *");
        addLabel(pnlChamps, "REMISE (%)");

        // Panneau date = champ + bouton calendrier
        JPanel pnlDate = new JPanel(new BorderLayout(4, 0));
        pnlDate.setBackground(BG_PANEL);
        txtDate = styleField(new JTextField());
        // Pré-remplir avec aujourd'hui
        txtDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        txtDate.setForeground(TEXT_PRIMARY);
        pnlDate.add(txtDate, BorderLayout.CENTER);

        btnCalendrier = new JButton("📅");
        btnCalendrier.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btnCalendrier.setBackground(BG_FIELD);
        btnCalendrier.setForeground(GOLD);
        btnCalendrier.setBorderPainted(false);
        btnCalendrier.setFocusPainted(false);
        btnCalendrier.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalendrier.setPreferredSize(new Dimension(36, 36));
        btnCalendrier.setToolTipText("Choisir une date");
        pnlDate.add(btnCalendrier, BorderLayout.EAST);
        pnlChamps.add(pnlDate);

        txtRemise = styleField(new JTextField("0"));
        pnlChamps.add(txtRemise);

        pnlForm.add(pnlChamps);
        pnlForm.add(Box.createVerticalStrut(14));

        // ── Section produits ───────────────────────
        JLabel lblProduits = new JLabel("PRODUITS *");
        lblProduits.setFont(FONT_LABEL);
        lblProduits.setForeground(TEXT_MUTED);
        lblProduits.setAlignmentX(LEFT_ALIGNMENT);
        pnlForm.add(lblProduits);
        pnlForm.add(Box.createVerticalStrut(6));

        JPanel pnlAjoutLigne = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlAjoutLigne.setBackground(BG_PANEL);
        pnlAjoutLigne.setAlignmentX(LEFT_ALIGNMENT);

        cmbProduit = new JComboBox<>();
        cmbProduit.setFont(FONT_FIELD);
        cmbProduit.setBackground(BG_FIELD);
        cmbProduit.setForeground(TEXT_PRIMARY);
        cmbProduit.setPreferredSize(new Dimension(165, 32));
        pnlAjoutLigne.add(cmbProduit);

        spnQteLigne = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        spnQteLigne.setFont(FONT_FIELD);
        spnQteLigne.setBackground(BG_FIELD);
        spnQteLigne.setForeground(TEXT_PRIMARY);
        spnQteLigne.setPreferredSize(new Dimension(70, 32));
        pnlAjoutLigne.add(spnQteLigne);

        btnAjouterLigne = new JButton("+ / Modifier");
        btnAjouterLigne.setBackground(GOLD); btnAjouterLigne.setForeground(BG_DARK);
        btnAjouterLigne.setFont(new Font("Baskerville Old Face", Font.BOLD, 12));
        btnAjouterLigne.setBorderPainted(false); btnAjouterLigne.setFocusPainted(false);
        btnAjouterLigne.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAjouterLigne.setPreferredSize(new Dimension(95, 32));
        btnAjouterLigne.setToolTipText("Ajouter une ligne ou modifier la ligne sélectionnée");
        pnlAjoutLigne.add(btnAjouterLigne);

        pnlForm.add(pnlAjoutLigne);
        pnlForm.add(Box.createVerticalStrut(8));

        tableLignesModel = new DefaultTableModel(
            new String[]{"Produit", "Qté", "Prix Unit.", "Sous-total"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableLignes = new JTable(tableLignesModel);
        tableLignes.setFont(FONT_LABEL); tableLignes.setRowHeight(30);
        tableLignes.setBackground(BG_FIELD); tableLignes.setForeground(TEXT_PRIMARY);
        tableLignes.setGridColor(BORDER); tableLignes.setSelectionBackground(SEL_BG);
        tableLignes.setSelectionForeground(SEL_FG);
        JTableHeader lh = tableLignes.getTableHeader();
        lh.setBackground(BG_DARK); lh.setForeground(GOLD);
        lh.setFont(new Font("Baskerville Old Face", Font.BOLD, 11));

        JScrollPane scrollLignes = new JScrollPane(tableLignes);
        scrollLignes.setPreferredSize(new Dimension(380, 120));
        scrollLignes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        scrollLignes.setAlignmentX(LEFT_ALIGNMENT);
        scrollLignes.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scrollLignes.getViewport().setBackground(BG_FIELD);
        pnlForm.add(scrollLignes);
        pnlForm.add(Box.createVerticalStrut(6));

        btnSupprimerLigne = new JButton("✖  Retirer ligne sélectionnée");
        btnSupprimerLigne.setFont(FONT_LABEL);
        btnSupprimerLigne.setBackground(new Color(80, 30, 30));
        btnSupprimerLigne.setForeground(new Color(255, 160, 140));
        btnSupprimerLigne.setBorderPainted(false); btnSupprimerLigne.setFocusPainted(false);
        btnSupprimerLigne.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSupprimerLigne.setAlignmentX(LEFT_ALIGNMENT);
        btnSupprimerLigne.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pnlForm.add(btnSupprimerLigne);
        pnlForm.add(Box.createVerticalStrut(12));

        lblTotal = new JLabel("TOTAL NET : 0 FCFA");
        lblTotal.setFont(new Font("Baskerville Old Face", Font.BOLD, 14));
        lblTotal.setForeground(GOLD_LIGHT);
        lblTotal.setAlignmentX(LEFT_ALIGNMENT);
        pnlForm.add(lblTotal);
        pnlForm.add(Box.createVerticalStrut(14));

        // Boutons bas formulaire
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBtn.setBackground(BG_PANEL);
        pnlBtn.setAlignmentX(LEFT_ALIGNMENT);

        btnEnregistrer = makeBtn("✓ Enregistrer", GOLD, BG_DARK, 150);
        btnAnnulerForm = makeBtn("Annuler", new Color(50, 45, 36), TEXT_MUTED, 110);
        pnlBtn.add(btnEnregistrer);
        pnlBtn.add(btnAnnulerForm);
        pnlForm.add(pnlBtn);

        pnlCenter.add(pnlForm, BorderLayout.LINE_END);
        add(pnlCenter, BorderLayout.CENTER);
    }

    // =========================================================================
    //  CALENDRIER POPUP
    // =========================================================================
    private void afficherCalendrier() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Choisir une date", true);
        dlg.setUndecorated(true);

        Calendar cal = Calendar.getInstance();
        // Essayer de parser la date déjà saisie
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            java.util.Date d = sdf.parse(txtDate.getText().trim());
            cal.setTime(d);
        } catch (Exception ignored) {}

        final int[] annee  = {cal.get(Calendar.YEAR)};
        final int[] mois   = {cal.get(Calendar.MONTH)};   // 0-based

        // ── Conteneur principal ────────────────────
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_PANEL);
        root.setBorder(BorderFactory.createLineBorder(GOLD, 1));

        // ── Barre navigation mois/année ─────────────
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(BG_DARK);
        navBar.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JButton btnPrev = calNavBtn("◀");
        JButton btnNext = calNavBtn("▶");
        JLabel  lblMois = new JLabel("", SwingConstants.CENTER);
        lblMois.setFont(new Font("Baskerville Old Face", Font.BOLD, 13));
        lblMois.setForeground(GOLD);

        navBar.add(btnPrev, BorderLayout.WEST);
        navBar.add(lblMois, BorderLayout.CENTER);
        navBar.add(btnNext, BorderLayout.EAST);
        root.add(navBar, BorderLayout.NORTH);

        // ── Grille jours ───────────────────────────
        JPanel grille = new JPanel(new GridLayout(0, 7, 2, 2));
        grille.setBackground(BG_PANEL);
        grille.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));
        root.add(grille, BorderLayout.CENTER);

        String[] MOIS_NOMS = {"Janvier","Février","Mars","Avril","Mai","Juin",
                               "Juillet","Août","Septembre","Octobre","Novembre","Décembre"};
        String[] JOURS_NOMS = {"Lu","Ma","Me","Je","Ve","Sa","Di"};

        // Fermer sans choisir
        JButton btnFermer = calNavBtn("✕");
        btnFermer.setForeground(RED_SOFT);
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        footer.setBackground(BG_DARK);
        JButton btnAuj = new JButton("Aujourd'hui");
        btnAuj.setFont(FONT_LABEL); btnAuj.setBackground(BG_FIELD);
        btnAuj.setForeground(TEXT_MUTED); btnAuj.setBorderPainted(false);
        btnAuj.setFocusPainted(false); btnAuj.setCursor(new Cursor(Cursor.HAND_CURSOR));
        footer.add(btnAuj); footer.add(btnFermer);
        root.add(footer, BorderLayout.SOUTH);

        // Remplir la grille
        Runnable[] remplir = {null};
        remplir[0] = () -> {
            grille.removeAll();
            lblMois.setText(MOIS_NOMS[mois[0]] + " " + annee[0]);
            // En-têtes jours
            for (String j : JOURS_NOMS) {
                JLabel lj = new JLabel(j, SwingConstants.CENTER);
                lj.setFont(new Font("Baskerville Old Face", Font.BOLD, 11));
                lj.setForeground(GOLD); grille.add(lj);
            }
            Calendar tmp = Calendar.getInstance();
            tmp.set(annee[0], mois[0], 1);
            // Décalage : lundi = 0
            int premier = (tmp.get(Calendar.DAY_OF_WEEK) + 5) % 7;
            for (int i = 0; i < premier; i++) grille.add(new JLabel());

            int nbJours = tmp.getActualMaximum(Calendar.DAY_OF_MONTH);
            Calendar auj = Calendar.getInstance();
            for (int d = 1; d <= nbJours; d++) {
                final int jour = d;
                JButton bj = new JButton(String.valueOf(d));
                bj.setFont(FONT_LABEL);
                bj.setBorderPainted(false); bj.setFocusPainted(false);
                bj.setCursor(new Cursor(Cursor.HAND_CURSOR));
                boolean estAujourd_hui = (d == auj.get(Calendar.DAY_OF_MONTH)
                    && mois[0] == auj.get(Calendar.MONTH)
                    && annee[0] == auj.get(Calendar.YEAR));
                if (estAujourd_hui) {
                    bj.setBackground(GOLD); bj.setForeground(BG_DARK);
                } else {
                    bj.setBackground(BG_FIELD); bj.setForeground(TEXT_PRIMARY);
                }
                bj.addActionListener(ev -> {
                    String dateChoisie = String.format("%02d/%02d/%04d",
                        jour, mois[0] + 1, annee[0]);
                    txtDate.setText(dateChoisie);
                    txtDate.setForeground(TEXT_PRIMARY);
                    validerChampDate();
                    dlg.dispose();
                });
                grille.add(bj);
            }
            grille.revalidate(); grille.repaint();
        };

        btnPrev.addActionListener(e -> {
            mois[0]--; if (mois[0] < 0) { mois[0] = 11; annee[0]--; }
            remplir[0].run();
        });
        btnNext.addActionListener(e -> {
            mois[0]++; if (mois[0] > 11) { mois[0] = 0; annee[0]++; }
            remplir[0].run();
        });
        btnAuj.addActionListener(e -> {
            Calendar n = Calendar.getInstance();
            annee[0] = n.get(Calendar.YEAR); mois[0] = n.get(Calendar.MONTH);
            remplir[0].run();
        });
        btnFermer.addActionListener(e -> dlg.dispose());

        remplir[0].run();
        dlg.setContentPane(root);
        dlg.pack();
        dlg.setSize(260, 280);

        // Positionner sous le bouton calendrier
        Point loc = btnCalendrier.getLocationOnScreen();
        dlg.setLocation(loc.x, loc.y + btnCalendrier.getHeight() + 2);
        dlg.setVisible(true);
    }

    private JButton calNavBtn(String txt) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        b.setBackground(BG_DARK); b.setForeground(TEXT_MUTED);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(30, 26));
        return b;
    }

    // =========================================================================
    //  IMPRESSION
    // =========================================================================
    private void imprimerFacture() {
        if (idEnEdition < 0) return;
        int row = jTable1.getSelectedRow();
        if (row < 0) return;

        String numFac = tableModel.getValueAt(row, 1).toString();
        String date   = tableModel.getValueAt(row, 2).toString();
        String client = tableModel.getValueAt(row, 3).toString();
        String total  = tableModel.getValueAt(row, 4).toString();
        String remise = tableModel.getValueAt(row, 5).toString();
        String net    = tableModel.getValueAt(row, 6).toString();
        String statut = tableModel.getValueAt(row, 7).toString();

        List<LigneFacture> lignes = new ArrayList<>();
        try { if (ligneDAO != null) lignes = ligneDAO.findByFacture(idEnEdition); }
        catch (SQLException ignored) {}
        final List<LigneFacture> lignesFinal = lignes;

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Facture " + numFac);
        PageFormat pf = job.defaultPage();
        pf.setOrientation(PageFormat.PORTRAIT);

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) graphics;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            int x = 0, y = 0;
            int w = (int) pageFormat.getImageableWidth();
            // Colonnes : produit 0%, qte 55%, sous-total 72%
            int col1 = x;
            int col2 = x + (int)(w * 0.55);
            int col3 = x + (int)(w * 0.72);

            // ── En-tête société ──
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Serif", Font.BOLD, 22));
            g2.drawString("MA PARFUMERIE", x, y + 22); y += 30;
            g2.setFont(new Font("Serif", Font.PLAIN, 10));
            g2.setColor(new Color(100, 100, 100));
            g2.drawString("Gestion de parfumerie", x, y + 10); y += 20;
            g2.setColor(Color.BLACK);
            g2.drawLine(x, y, x + w, y); y += 18;

            // ── Titre + infos ──
            g2.setFont(new Font("Serif", Font.BOLD, 15));
            g2.drawString("FACTURE  " + numFac, x, y + 14); y += 24;
            g2.setFont(new Font("Serif", Font.PLAIN, 11));
            g2.drawString("Date : " + date, x, y + 12);
            g2.drawString("Statut : " + statut, x + w - 120, y + 12); y += 18;
            g2.drawString("Client : " + client, x, y + 12); y += 30;

            // ── En-tête tableau ──
            g2.setColor(new Color(220, 220, 220));
            g2.fillRect(x, y, w, 20);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Serif", Font.BOLD, 11));
            g2.drawString("Produit",     col1 + 4, y + 14);
            g2.drawString("Qte",         col2 + 4, y + 14);
            g2.drawString("Sous-total",  col3 + 4, y + 14);
            y += 22;

            // ── Lignes produits ──
            g2.setFont(new Font("Serif", Font.PLAIN, 11));
            boolean alt = false;
            for (LigneFacture lf : lignesFinal) {
                String nomP = nomProduitById(lf.getIdProduit());
                BigDecimal st = lf.getPrixUnitaire()
                    .multiply(BigDecimal.valueOf(lf.getQuantite()));
                if (alt) { g2.setColor(new Color(248, 248, 248)); g2.fillRect(x, y, w, 18); }
                g2.setColor(Color.BLACK);
                String nomAff = nomP.length() > 35 ? nomP.substring(0, 33) + "..." : nomP;
                g2.drawString(nomAff,                           col1 + 4, y + 13);
                g2.drawString(String.valueOf(lf.getQuantite()), col2 + 4, y + 13);
                g2.drawString(st + " FCFA",                     col3 + 4, y + 13);
                y += 18; alt = !alt;
            }
            g2.setColor(Color.BLACK);
            g2.drawLine(x, y, x + w, y); y += 18;

            // ── Totaux ──
            g2.setFont(new Font("Serif", Font.PLAIN, 11));
            g2.drawString("Total brut : " + total + " FCFA", col2, y + 13); y += 18;
            g2.drawString("Remise : " + remise,              col2, y + 13); y += 18;
            g2.setFont(new Font("Serif", Font.BOLD, 12));
            g2.drawString("NET A PAYER : " + net + " FCFA",  col2, y + 14); y += 36;

            // ── Pied de page ──
            g2.setFont(new Font("Serif", Font.ITALIC, 9));
            g2.setColor(new Color(130, 130, 130));
            g2.drawString("Merci pour votre confiance  --  MA PARFUMERIE", x, y);
            return Printable.PAGE_EXISTS;
        }, pf);

        if (job.printDialog()) {
            try { job.print(); }
            catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur d'impression :\n" + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    //  CONTRÔLES DE SAISIE
    // =========================================================================
    // Filtre : lettres + espaces uniquement
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

    // Filtre : chiffres + point/virgule
    private void appliquerFiltreNumerique(JTextField f) {
        ((javax.swing.text.AbstractDocument) f.getDocument())
            .setDocumentFilter(new javax.swing.text.DocumentFilter() {
                @Override public void insertString(FilterBypass fb, int off, String t,
                        javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                    if (t != null && t.matches("[0-9.,]*")) super.insertString(fb, off, t, a);
                }
                @Override public void replace(FilterBypass fb, int off, int len, String t,
                        javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                    if (t != null && t.matches("[0-9.,]*")) super.replace(fb, off, len, t, a);
                }
            });
    }

    // Filtre : format date jj/MM/aaaa
    private void appliquerFiltreDateManuel(JTextField f) {
        ((javax.swing.text.AbstractDocument) f.getDocument())
            .setDocumentFilter(new javax.swing.text.DocumentFilter() {
                @Override public void insertString(FilterBypass fb, int off, String t,
                        javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                    if (t != null && t.matches("[0-9/]*") && fb.getDocument().getLength() + t.length() <= 10)
                        super.insertString(fb, off, t, a);
                }
                @Override public void replace(FilterBypass fb, int off, int len, String t,
                        javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                    int newLen = fb.getDocument().getLength() - len + (t != null ? t.length() : 0);
                    if (t != null && t.matches("[0-9/]*") && newLen <= 10)
                        super.replace(fb, off, len, t, a);
                }
            });
    }

    // Validation visuelle champ date
    private void validerChampDate() {
        String s = txtDate.getText().trim();
        if (s.isEmpty()) { resetField(txtDate); return; }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(false); sdf.parse(s);
            resetField(txtDate);
        } catch (ParseException ex) {
            marquerErreur(txtDate, "Format attendu : jj/MM/aaaa");
        }
    }

    private void validerChampRemise() {
        String s = txtRemise.getText().trim().replace(",", ".");
        try {
            double v = Double.parseDouble(s.isEmpty() ? "0" : s);
            if (v < 0 || v > 100) marquerErreur(txtRemise, "Remise entre 0 et 100");
            else { resetField(txtRemise); recalculerTotal(); }
        } catch (NumberFormatException ex) {
            marquerErreur(txtRemise, "Valeur numérique requise");
        }
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

    // =========================================================================
    //  CONFIGURATION TABLEAU
    // =========================================================================
    private void configurerTableau() {
        tableModel = new DefaultTableModel(
            new String[]{"id_facture","N° FACTURE","DATE","CLIENT",
                         "TOTAL","REMISE","NET À PAYER","STATUT"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        jTable1.setModel(tableModel);
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(85);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(130);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(90);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(65);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(85);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = jTable1.getTableHeader();
        header.setFont(FONT_HEADER); header.setBackground(BG_DARK);
        header.setForeground(GOLD);
        header.setPreferredSize(new Dimension(header.getWidth(), 44));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GOLD));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.LEFT);

        jTable1.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(FONT_TABLE);
                if (isSelected) { setBackground(SEL_BG); setForeground(SEL_FG); }
                else {
                    setBackground(row % 2 == 0 ? BG_PANEL : BG_ROW_ALT);
                    setForeground(TEXT_PRIMARY);
                    if (column == 6) setForeground(GOLD_LIGHT);
                    if (column == 7 && value != null) switch (value.toString()) {
                        case "Payée"   -> { setForeground(GREEN_SOFT);  setFont(new Font("Baskerville Old Face", Font.BOLD, 13)); }
                        case "Impayée" -> { setForeground(RED_SOFT);    setFont(new Font("Baskerville Old Face", Font.BOLD, 13)); }
                        case "Annulée" -> { setForeground(TEXT_MUTED);  setFont(new Font("Baskerville Old Face", Font.ITALIC, 13)); }
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
        // Sélection ligne
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = jTable1.getSelectedRow();
                boolean sel = row >= 0;
                btnModifierStatut.setEnabled(sel); btnAnnulerFac.setEnabled(sel);
                btnSupprimer.setEnabled(sel);      btnImprimer.setEnabled(sel);
                btnModifier.setEnabled(sel);
                if (sel) idEnEdition = (int) tableModel.getValueAt(row, 0);
            }
        });

        // Filtres
        tglToutes .addActionListener(e -> { appliquerFiltreSurCache(null);           mettreFiltreActif(tglToutes); });
        tglImpaye .addActionListener(e -> { appliquerFiltreSurCache(Statut.Impayée); mettreFiltreActif(tglImpaye); });
        tglPaye   .addActionListener(e -> { appliquerFiltreSurCache(Statut.Payée);   mettreFiltreActif(tglPaye); });
        tglAnnuler.addActionListener(e -> { appliquerFiltreSurCache(Statut.Annulée); mettreFiltreActif(tglAnnuler); });

        // Recherche temps réel
        jTextField1.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String t = jTextField1.getText().trim();
                if (!t.equals("Rechercher une facture...")) filtrerParRecherche(t);
            }
        });

        btnNouveau.addActionListener(e -> {
            idEnEdition = -1; viderFormulaire(); pnlForm.setVisible(true);
            revalidate(); repaint();
        });
        btnAnnulerForm.addActionListener(e -> { viderFormulaire(); pnlForm.setVisible(false); revalidate(); repaint(); });
        btnEnregistrer.addActionListener(e -> enregistrer());
        btnModifierStatut.addActionListener(e -> changerStatut(Statut.Payée));
        btnAnnulerFac.addActionListener(e -> changerStatut(Statut.Annulée));
        btnSupprimer.addActionListener(e -> supprimer());
        btnImprimer.addActionListener(e -> imprimerFacture());
        btnAjouterLigne.addActionListener(e -> ajouterLigne());
        btnCalendrier.addActionListener(e -> afficherCalendrier());
        btnModifier.addActionListener(e -> ouvrirModificationFacture());

        btnSupprimerLigne.addActionListener(e -> {
            int row = tableLignes.getSelectedRow();
            if (row >= 0) { lignesForm.remove(row); tableLignesModel.removeRow(row); recalculerTotal(); }
        });

        // Sélection ligne dans tableLignes → sync combo produit + spinner quantité
        tableLignes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableLignes.getSelectedRow();
                if (row >= 0 && row < lignesForm.size()) {
                    LigneFacture lf = lignesForm.get(row);
                    // Sélectionner le bon produit dans le combo
                    for (int i = 0; i < listeProduits.size(); i++) {
                        if (listeProduits.get(i).getIdProduit() == lf.getIdProduit()) {
                            cmbProduit.setSelectedIndex(i);
                            break;
                        }
                    }
                    // Mettre la quantité dans le spinner
                    spnQteLigne.setValue(lf.getQuantite());
                }
            }
        });

        // Validation champs au focus perdu
        txtDate.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { validerChampDate(); }
        });
        txtRemise.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { validerChampRemise(); }
        });
        txtNomClient.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                if (txtNomClient.getText().trim().isEmpty()) marquerErreur(txtNomClient, "Champ obligatoire");
                else resetField(txtNomClient);
            }
        });

        // Appliquer filtres de saisie
        appliquerFiltreTexte(txtNomClient);
        appliquerFiltreNumerique(txtRemise);
        appliquerFiltreDateManuel(txtDate);
    }

    private void mettreFiltreActif(JToggleButton actif) {
        for (JToggleButton t : new JToggleButton[]{tglToutes, tglImpaye, tglPaye, tglAnnuler}) {
            t.setBackground(t == actif ? GOLD : new Color(40, 36, 28));
            t.setForeground(t == actif ? BG_DARK : TEXT_MUTED);
        }
    }

    // =========================================================================
    //  CHARGEMENT
    // =========================================================================
    private void chargerProduits() {
        if (produitDAO == null) return;
        try {
            listeProduits = produitDAO.findAll();
            cmbProduit.removeAllItems();
            for (Produit p : listeProduits) cmbProduit.addItem(p.getNom());
        } catch (SQLException ex) { afficherErreurBD(ex); }
    }

    private void chargerTableau(Statut statut) {
        if (factureDAO == null) return;
        cacheTableau.clear(); tableModel.setRowCount(0);
        try {
            List<Facture> liste = (statut == null) ? factureDAO.findAll() : factureDAO.findByStatut(statut);
            for (Facture f : liste) {
                String dateStr = f.getDateFacture() != null
                    ? f.getDateFacture().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
                Object[] row = { f.getIdFacture(), f.getNumeroFacture(), dateStr,
                    f.getNomClient(), f.getMontantTotal(), f.getRemise() + "%",
                    f.getMontantNet(), f.getStatut() != null ? f.getStatut().name() : "" };
                cacheTableau.add(row); tableModel.addRow(row);
            }
            jLabel2.setText(liste.size() + " Facture" + (liste.size() > 1 ? "s" : ""));
        } catch (SQLException ex) { afficherErreurBD(ex); }
    }

    // =========================================================================
    //  RECHERCHE
    // =========================================================================
    private void filtrerParRecherche(String terme) {
        tableModel.setRowCount(0);
        if (terme.isEmpty()) {
            for (Object[] r : cacheTableau) tableModel.addRow(r);
            jLabel2.setText(cacheTableau.size() + " Facture" + (cacheTableau.size() > 1 ? "s" : ""));
            return;
        }
        String t = terme.toLowerCase(); int count = 0;
        for (Object[] r : cacheTableau) {
            String num    = r[1] != null ? r[1].toString().toLowerCase() : "";
            String client = r[3] != null ? r[3].toString().toLowerCase() : "";
            String date   = r[2] != null ? r[2].toString().toLowerCase() : "";
            String st     = r[7] != null ? r[7].toString().toLowerCase() : "";
            if (num.contains(t) || client.contains(t) || date.contains(t) || st.contains(t)) {
                tableModel.addRow(r); count++;
            }
        }
        jLabel2.setText(count + " résultat" + (count > 1 ? "s" : ""));
    }

    private void appliquerFiltreSurCache(Statut statut) {
        chargerTableau(statut);
        String terme = jTextField1.getText().trim();
        if (!terme.isEmpty() && !terme.equals("Rechercher une facture...")) filtrerParRecherche(terme);
    }

    // =========================================================================
    //  AJOUTER LIGNE
    // =========================================================================
    private void ajouterLigne() {
        int idx = cmbProduit.getSelectedIndex();
        if (idx < 0) return;
        Produit p = listeProduits.get(idx);
        int qte = (Integer) spnQteLigne.getValue();

        int rowSelectionne = tableLignes.getSelectedRow();

        if (rowSelectionne >= 0 && rowSelectionne < lignesForm.size()) {
            // ── MODE MODIFICATION DE LIGNE EXISTANTE ──────────────────────────
            // Vérifier le stock (sans compter la quantité déjà allouée à cette ligne)
            LigneFacture ancienne = lignesForm.get(rowSelectionne);
            int stockDispo = p.getQuantiteStock();
            // Si même produit, on récupère la quantité déjà prise
            if (ancienne.getIdProduit() == p.getIdProduit()) stockDispo += ancienne.getQuantite();
            if (qte > stockDispo) {
                JOptionPane.showMessageDialog(this,
                    "Stock insuffisant ! Stock disponible : " + p.getQuantiteStock(),
                    "Stock insuffisant", JOptionPane.WARNING_MESSAGE); return;
            }
            LigneFacture lf = new LigneFacture();
            lf.setIdProduit(p.getIdProduit()); lf.setQuantite(qte); lf.setPrixUnitaire(p.getPrixVente());
            lignesForm.set(rowSelectionne, lf);
            BigDecimal st = p.getPrixVente().multiply(BigDecimal.valueOf(qte));
            tableLignesModel.setValueAt(p.getNom(),            rowSelectionne, 0);
            tableLignesModel.setValueAt(qte,                   rowSelectionne, 1);
            tableLignesModel.setValueAt(p.getPrixVente(),      rowSelectionne, 2);
            tableLignesModel.setValueAt(st,                    rowSelectionne, 3);
            recalculerTotal();
            // Désélectionner pour retourner en mode ajout
            tableLignes.clearSelection();
        } else {
            // ── MODE AJOUT NOUVELLE LIGNE ─────────────────────────────────────
            if (qte > p.getQuantiteStock()) {
                JOptionPane.showMessageDialog(this,
                    "Stock insuffisant ! Stock disponible : " + p.getQuantiteStock(),
                    "Stock insuffisant", JOptionPane.WARNING_MESSAGE); return;
            }
            LigneFacture lf = new LigneFacture();
            lf.setIdProduit(p.getIdProduit()); lf.setQuantite(qte); lf.setPrixUnitaire(p.getPrixVente());
            lignesForm.add(lf);
            BigDecimal st = p.getPrixVente().multiply(BigDecimal.valueOf(qte));
            tableLignesModel.addRow(new Object[]{p.getNom(), qte, p.getPrixVente(), st});
            recalculerTotal();
        }
    }

    private void recalculerTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (LigneFacture lf : lignesForm)
            total = total.add(lf.getPrixUnitaire().multiply(BigDecimal.valueOf(lf.getQuantite())));
        BigDecimal remise = BigDecimal.ZERO;
        try { remise = new BigDecimal(txtRemise.getText().trim().replace(",", ".")); }
        catch (NumberFormatException ignored) {}
        if (remise.compareTo(BigDecimal.ZERO) < 0 || remise.compareTo(BigDecimal.valueOf(100)) > 0)
            remise = BigDecimal.ZERO;
        BigDecimal net = total.multiply(BigDecimal.ONE.subtract(remise.divide(BigDecimal.valueOf(100))));
        lblTotal.setText("TOTAL NET : " + net + " FCFA");
    }

    // =========================================================================
    //  ENREGISTRER
    // =========================================================================
    private void enregistrer() {
        // Réinitialiser erreurs
        resetField(txtNomClient); resetField(txtDate);
        boolean ok = true;
        if (txtNomClient.getText().trim().isEmpty()) { marquerErreur(txtNomClient, "Champ obligatoire"); ok = false; }
        if (!validerDateEnregistrement()) ok = false;
        if (lignesForm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ajoutez au moins un produit.",
                "Saisie invalide", JOptionPane.WARNING_MESSAGE); ok = false;
        }
        if (!ok) { JOptionPane.showMessageDialog(this, "Corrigez les champs en rouge.",
            "Saisie invalide", JOptionPane.WARNING_MESSAGE); return; }
        try {
            BigDecimal total = BigDecimal.ZERO;
            for (LigneFacture lf : lignesForm)
                total = total.add(lf.getPrixUnitaire().multiply(BigDecimal.valueOf(lf.getQuantite())));
            BigDecimal remisePct = BigDecimal.ZERO;
            try { remisePct = new BigDecimal(txtRemise.getText().trim().replace(",", ".")); }
            catch (NumberFormatException ignored) {}
            BigDecimal net = total.multiply(BigDecimal.ONE.subtract(remisePct.divide(BigDecimal.valueOf(100))));

            Facture f = new Facture();
            f.setNumeroFacture(modeModification && idEnEdition > 0 ? factureDAO.findById(idEnEdition).getNumeroFacture() : factureDAO.prochainNumero());
            f.setDateFacture(parseDateVersLocalDate(txtDate.getText().trim()));
            f.setNomClient(txtNomClient.getText().trim());
            f.setTelephoneClient(txtTelephone.getText().trim());
            f.setMontantTotal(total); f.setRemise(remisePct); f.setMontantNet(net);
            f.setStatut(Statut.Impayée);

            if (modeModification && idEnEdition > 0) {
                // Mise à jour de la facture existante
                f.setIdFacture(idEnEdition);
                factureDAO.update(f);
                // Supprimer les anciennes lignes et réinsérer
                ligneDAO.deleteByFacture(idEnEdition);
                for (LigneFacture lf : lignesForm) { lf.setIdFacture(idEnEdition); ligneDAO.insert(lf); }
                JOptionPane.showMessageDialog(this, "✓ Facture modifiée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                int idFacture = factureDAO.insert(f);
                if (idFacture <= 0) { JOptionPane.showMessageDialog(this, "Impossible de créer la facture.", "Erreur", JOptionPane.ERROR_MESSAGE); return; }
                for (LigneFacture lf : lignesForm) { lf.setIdFacture(idFacture); ligneDAO.insert(lf); }
                JOptionPane.showMessageDialog(this, "✓ Facture enregistrée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
            viderFormulaire(); pnlForm.setVisible(false);
            chargerTableau(null); mettreFiltreActif(tglToutes); tglToutes.setSelected(true);
            revalidate(); repaint();
        } catch (SQLException ex) { afficherErreurBD(ex); }
    }

    private boolean validerDateEnregistrement() {
        String s = txtDate.getText().trim();
        if (s.isEmpty()) { marquerErreur(txtDate, "La date est obligatoire"); return false; }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(false); sdf.parse(s); resetField(txtDate); return true;
        } catch (ParseException ex) {
            marquerErreur(txtDate, "Format invalide. Ex : 15/05/2025"); return false;
        }
    }

    // =========================================================================
    //  CHANGER STATUT / SUPPRIMER
    // =========================================================================
    private void changerStatut(Statut statut) {
        if (idEnEdition < 0) return;
        try {
            factureDAO.updateStatut(idEnEdition, statut);
            JOptionPane.showMessageDialog(this, "✓ Statut : " + statut.name(), "Succès", JOptionPane.INFORMATION_MESSAGE);
            chargerTableau(null); mettreFiltreActif(tglToutes); tglToutes.setSelected(true);
            idEnEdition = -1; btnModifierStatut.setEnabled(false); btnAnnulerFac.setEnabled(false);
            btnSupprimer.setEnabled(false); btnImprimer.setEnabled(false); btnModifier.setEnabled(false);
        } catch (SQLException ex) { afficherErreurBD(ex); }
    }

    private void supprimer() {
        if (idEnEdition < 0) return;
        int row = jTable1.getSelectedRow();
        String num = tableModel.getValueAt(row, 1).toString();
        int choix = JOptionPane.showConfirmDialog(this,
            "Supprimer la facture \"" + num + "\" ?\nIrréversible.",
            "Confirmer", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choix == JOptionPane.YES_OPTION) {
            try {
                factureDAO.delete(idEnEdition); chargerTableau(null);
                idEnEdition = -1; btnModifierStatut.setEnabled(false); btnAnnulerFac.setEnabled(false);
                btnSupprimer.setEnabled(false); btnImprimer.setEnabled(false); btnModifier.setEnabled(false);
            } catch (SQLException ex) { afficherErreurBD(ex); }
        }
    }

    // =========================================================================
    //  UTILITAIRES
    // =========================================================================
    private LocalDate parseDateVersLocalDate(String s) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            java.util.Date d = sdf.parse(s);
            return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException ex) { return LocalDate.now(); }
    }

    private String nomProduitById(int id) {
        return listeProduits.stream().filter(p -> p.getIdProduit() == id)
            .map(Produit::getNom).findFirst().orElse("Produit #" + id);
    }

    // =========================================================================
    //  MODIFICATION FACTURE
    // =========================================================================
    private void ouvrirModificationFacture() {
        if (idEnEdition < 0) return;
        int row = jTable1.getSelectedRow();
        if (row < 0) return;

        try {
            // Récupérer les données de la facture sélectionnée
            parfumerie.modeles.Facture f = factureDAO.findById(idEnEdition);
            if (f == null) {
                JOptionPane.showMessageDialog(this, "Facture introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            modeModification = true;
            if (lblFormTitre != null) lblFormTitre.setText("MODIFIER FACTURE — " + f.getNumeroFacture());

            // Remplir les champs
            txtNomClient.setText(f.getNomClient() != null ? f.getNomClient() : "");
            txtTelephone.setText(f.getTelephoneClient() != null ? f.getTelephoneClient() : "");
            if (f.getDateFacture() != null)
                txtDate.setText(f.getDateFacture().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
            txtRemise.setText(f.getRemise() != null ? f.getRemise().toPlainString() : "0");
            resetField(txtNomClient); resetField(txtDate); resetField(txtRemise);

            // Charger les lignes existantes
            lignesForm.clear();
            tableLignesModel.setRowCount(0);
            List<LigneFacture> lignes = ligneDAO.findByFacture(idEnEdition);
            for (LigneFacture lf : lignes) {
                lignesForm.add(lf);
                // Trouver le nom du produit
                String nomP = listeProduits.stream()
                    .filter(p -> p.getIdProduit() == lf.getIdProduit())
                    .map(Produit::getNom).findFirst().orElse("Produit #" + lf.getIdProduit());
                BigDecimal st = lf.getPrixUnitaire().multiply(BigDecimal.valueOf(lf.getQuantite()));
                tableLignesModel.addRow(new Object[]{nomP, lf.getQuantite(), lf.getPrixUnitaire(), st});
            }
            recalculerTotal();

            pnlForm.setVisible(true);
            txtNomClient.requestFocusInWindow();
            revalidate(); repaint();

        } catch (SQLException ex) {
            afficherErreurBD(ex);
        }
    }

    private void viderFormulaire() {
        modeModification = false;
        idEnEdition = -1;
        if (lblFormTitre != null) lblFormTitre.setText("NOUVELLE FACTURE");
        txtNomClient.setText(""); txtTelephone.setText("");
        txtDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        txtRemise.setText("0");
        resetField(txtNomClient); resetField(txtDate); resetField(txtRemise);
        lignesForm.clear(); tableLignesModel.setRowCount(0);
        lblTotal.setText("TOTAL NET : 0 FCFA");
    }

    private void afficherErreurBD(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Erreur BD :\n" + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private JButton makeBtn(String txt, Color bg, Color fg, int w) {
        JButton b = new JButton(txt);
        b.setBackground(bg); b.setForeground(fg); b.setFont(FONT_BTN);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(w, 36));
        return b;
    }

    private JToggleButton creerFiltreBtn(String texte, boolean selected, ButtonGroup bg, JPanel panel) {
        JToggleButton btn = new JToggleButton(texte, selected);
        btn.setFont(FONT_LABEL);
        btn.setBackground(selected ? GOLD : new Color(40, 36, 28));
        btn.setForeground(selected ? BG_DARK : TEXT_MUTED);
        btn.setBorder(BorderFactory.createLineBorder(BORDER));
        btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 28));
        bg.add(btn); panel.add(btn);
        return btn;
    }

    private void addLabel(JPanel p, String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(FONT_LABEL); l.setForeground(TEXT_MUTED); l.setBackground(BG_PANEL);
        p.add(l);
    }

    private JTextField styleField(JTextField f) {
        f.setFont(FONT_FIELD); f.setBackground(BG_FIELD); f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(GOLD);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return f;
    }


    // ── Crée une icône depuis un caractère unicode (fallback emojis) ──────
    private javax.swing.ImageIcon creerIconeTexte(String emoji, int taille) {
        java.awt.image.BufferedImage img =
            new java.awt.image.BufferedImage(taille + 6, taille + 6,
                java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Font fEmoji = new Font("Segoe UI Emoji", Font.PLAIN, taille);
        g.setFont(fEmoji);
        g.setColor(Color.WHITE);
        g.drawString(emoji, 2, taille);
        g.dispose();
        return new javax.swing.ImageIcon(img);
    }

    // ── Variables declaration ──────────────────────
    private JButton       btnNouveau, btnEnregistrer, btnAnnulerForm;
    private JButton       btnModifierStatut, btnAnnulerFac, btnSupprimer, btnImprimer, btnModifier;
    private boolean       modeModification = false;
    private JButton       btnAjouterLigne, btnSupprimerLigne, btnCalendrier;
    private JToggleButton tglToutes, tglImpaye, tglPaye, tglAnnuler;
    private JComboBox<String> cmbProduit;
    private JSpinner      spnQteLigne;
    private JTable        jTable1, tableLignes;
    private DefaultTableModel tableLignesModel;
    private JLabel        jLabel2, lblTotal, lblFormTitre;
    private JPanel        pnlForm;
    private JTextField    jTextField1;
    private JTextField    txtNomClient, txtTelephone, txtDate, txtRemise;
}
