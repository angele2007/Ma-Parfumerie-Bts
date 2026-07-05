/*
 * panelImpression.java — Impression avec filtres
 * Thème sombre luxe cohérent avec le tableau de bord
 */
package parfumerie.vues;

import parfumerie.dao.EntreeStockDAO;
import parfumerie.dao.FournisseurDAO;
import parfumerie.dao.ProduitDAO;
import parfumerie.dao.SortieStockDAO;
import parfumerie.modele.Produit;
import parfumerie.modeles.EntreeStock;
import parfumerie.modeles.Fournisseur;
import parfumerie.modeles.SortieStock;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.print.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Panel Impression — filtre et imprime Produits, Entrées stock, Sorties stock, Fournisseurs.
 * @author Angele
 */
public class panelImpression extends javax.swing.JPanel {

    // ── Palette ───────────────────────────────────
    private static final Color BG_DARK      = new Color(18, 16, 14);
    private static final Color BG_PANEL     = new Color(26, 23, 19);
    private static final Color BG_ROW_ALT   = new Color(32, 28, 22);
    private static final Color BG_FIELD     = new Color(36, 32, 26);
    private static final Color BG_FILTER    = new Color(22, 18, 14);
    private static final Color GOLD         = new Color(212, 175, 55);
    private static final Color GOLD_LIGHT   = new Color(240, 215, 140);
    private static final Color TEXT_PRIMARY = new Color(240, 230, 200);
    private static final Color TEXT_MUTED   = new Color(160, 148, 120);
    private static final Color BORDER       = new Color(55, 48, 36);
    private static final Color SEL_BG       = new Color(180, 150, 80);
    private static final Color SEL_FG       = new Color(18, 16, 14);
    private static final Color OK_GREEN     = new Color(100, 200, 130);
    private static final Color ALERT_RED    = new Color(255, 110, 80);

    private static final Font FONT_TITRE    = new Font("Baskerville Old Face", Font.BOLD, 22);
    private static final Font FONT_LABEL    = new Font("Baskerville Old Face", Font.PLAIN, 12);
    private static final Font FONT_FIELD    = new Font("Baskerville Old Face", Font.PLAIN, 13);
    private static final Font FONT_TABLE    = new Font("Baskerville Old Face", Font.PLAIN, 13);
    private static final Font FONT_HEADER   = new Font("Baskerville Old Face", Font.BOLD, 12);
    private static final Font FONT_BTN      = new Font("Baskerville Old Face", Font.PLAIN, 13);
    private static final Font FONT_SECTION  = new Font("Baskerville Old Face", Font.BOLD, 11);

    private static final String DATE_FMT    = "dd/MM/yyyy";
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern(DATE_FMT);

    // ── Filtres ───────────────────────────────────
    private JComboBox<String> cmbType;
    private JTextField txtDateDu, txtDateAu;
    private JButton    btnCalDu, btnCalAu;
    private JComboBox<String> cmbFournisseur;
    private JComboBox<String> cmbCategorie;
    private JComboBox<String> cmbStatut;
    private JButton    btnFiltrer, btnReset, btnImprimer;
    private JLabel     lblCompteur;

    // ── Tableau ───────────────────────────────────
    private JTable            jTable;
    private DefaultTableModel tableModel;

    // ── Données en mémoire ────────────────────────
    private List<Fournisseur> listeFournisseurs = new ArrayList<>();
    private final java.util.Map<Integer, String> nomsProduits    = new HashMap<>();
    private final java.util.Map<Integer, String> nomsFournisseurs = new HashMap<>();

    // =========================================================================
    //  CONSTRUCTEUR
    // =========================================================================
    public panelImpression() {
        setBackground(BG_DARK);
        setLayout(new BorderLayout());
        chargerCaches();
        construireUI();
        appliquerFiltres();
    }

    // =========================================================================
    //  CACHES
    // =========================================================================
    private void chargerCaches() {
        try {
            for (Produit p : new ProduitDAO().findAll())
                nomsProduits.put(p.getIdProduit(), p.getNom());
            listeFournisseurs = new FournisseurDAO().findAll();
            for (Fournisseur f : listeFournisseurs)
                nomsFournisseurs.put(f.getIdFournisseur(), f.getNom());
        } catch (Exception ignored) {}
    }

    private String nomProduit(int id) { return nomsProduits.getOrDefault(id, "#" + id); }
    private String nomFournisseur(int id) { return nomsFournisseurs.getOrDefault(id, "#" + id); }

    // =========================================================================
    //  CONSTRUCTION UI
    // =========================================================================
    private void construireUI() {

        // ── HEADER ─────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        header.setBorder(new EmptyBorder(20, 24, 12, 24));

        JPanel pnlTitre = new JPanel();
        pnlTitre.setBackground(BG_DARK);
        pnlTitre.setLayout(new BoxLayout(pnlTitre, BoxLayout.Y_AXIS));
        JLabel lblTitre = new JLabel("Impression");
        lblTitre.setFont(FONT_TITRE); lblTitre.setForeground(TEXT_PRIMARY);
        JLabel lblSub = new JLabel("Filtrez, prévisualisez et imprimez vos données");
        lblSub.setFont(FONT_LABEL); lblSub.setForeground(TEXT_MUTED);
        pnlTitre.add(lblTitre); pnlTitre.add(Box.createVerticalStrut(3)); pnlTitre.add(lblSub);
        header.add(pnlTitre, BorderLayout.CENTER);

        // Bouton Imprimer dans le header
        btnImprimer = new JButton("🖨  Imprimer");
        btnImprimer.setBackground(new Color(30, 40, 65));
        btnImprimer.setForeground(new Color(140, 180, 255));
        btnImprimer.setFont(new Font("Baskerville Old Face", Font.BOLD, 14));
        btnImprimer.setBorderPainted(false); btnImprimer.setFocusPainted(false);
        btnImprimer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnImprimer.setPreferredSize(new Dimension(155, 40));
        btnImprimer.addActionListener(e -> imprimerDonnees());
        header.add(btnImprimer, BorderLayout.EAST);

        // Séparateur doré
        JPanel sep = new JPanel();
        sep.setBackground(GOLD); sep.setPreferredSize(new Dimension(1, 1));
        header.add(sep, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // ── CORPS ─────────────────────────────────
        JPanel corps = new JPanel(new BorderLayout(0, 12));
        corps.setBackground(BG_DARK);
        corps.setBorder(new EmptyBorder(12, 24, 24, 24));

        corps.add(creerBarreFiltres(), BorderLayout.NORTH);
        corps.add(creerZoneTableau(), BorderLayout.CENTER);
        add(corps, BorderLayout.CENTER);
    }

    // =========================================================================
    //  BARRE DE FILTRES
    // =========================================================================
    private JPanel creerBarreFiltres() {
        // Conteneur global
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setBackground(BG_DARK);

        // ── Ligne 1 : Type + Dates + Fournisseur ──
        JPanel ligne1 = creerLigneFiltre();

        // Type de données
        ajouterLabelFiltre(ligne1, "Type :");
        cmbType = new JComboBox<>(new String[]{
            "Produits", "Entrées stock", "Sorties stock", "Fournisseurs"});
        styleCombo(cmbType, 150);
        cmbType.addActionListener(e -> adapterFiltresSelonType());
        ligne1.add(cmbType);
        ligne1.add(sepV());

        // Dates
        ajouterLabelFiltre(ligne1, "Du :");
        txtDateDu = creerChampDate("01/01/2024");
        ligne1.add(txtDateDu);
        btnCalDu = creerBtnCal();
        btnCalDu.addActionListener(e -> afficherCalendrier(txtDateDu, btnCalDu));
        ligne1.add(btnCalDu);

        ajouterLabelFiltre(ligne1, "Au :");
        txtDateAu = creerChampDate(LocalDate.now().format(DTF));
        ligne1.add(txtDateAu);
        btnCalAu = creerBtnCal();
        btnCalAu.addActionListener(e -> afficherCalendrier(txtDateAu, btnCalAu));
        ligne1.add(btnCalAu);
        ligne1.add(sepV());

        // Fournisseur
        ajouterLabelFiltre(ligne1, "Fournisseur :");
        List<String> nomsFourListe = new ArrayList<>();
        nomsFourListe.add("Tous");
        for (Fournisseur f : listeFournisseurs) nomsFourListe.add(f.getNom());
        cmbFournisseur = new JComboBox<>(nomsFourListe.toArray(new String[0]));
        styleCombo(cmbFournisseur, 150);
        ligne1.add(cmbFournisseur);

        wrapper.add(ligne1, BorderLayout.NORTH);

        // ── Ligne 2 : Catégorie + Statut + Boutons ─
        JPanel ligne2 = creerLigneFiltre();

        ajouterLabelFiltre(ligne2, "Catégorie :");
        cmbCategorie = new JComboBox<>(new String[]{"Toutes","Homme","Femme","Mixte","Enfant"});
        styleCombo(cmbCategorie, 120);
        ligne2.add(cmbCategorie);
        ligne2.add(sepV());

        ajouterLabelFiltre(ligne2, "Statut :");
        cmbStatut = new JComboBox<>(new String[]{"Tous","✓ OK","⚠ Alerte","Vente","Offert","Perte","Retour"});
        styleCombo(cmbStatut, 120);
        ligne2.add(cmbStatut);
        ligne2.add(sepV());

        // Boutons
        btnFiltrer = styleBtnAction("Appliquer", GOLD, BG_DARK, 110);
        btnFiltrer.addActionListener(e -> appliquerFiltres());
        ligne2.add(btnFiltrer);

        btnReset = styleBtnAction("Réinitialiser", new Color(45, 38, 28), TEXT_MUTED, 120);
        btnReset.addActionListener(e -> reinitialiser());
        ligne2.add(btnReset);

        wrapper.add(ligne2, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel creerLigneFiltre() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_FILTER);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0,0,getWidth()-1,getHeight()-1,8,8));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        return p;
    }

    private void ajouterLabelFiltre(JPanel p, String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(FONT_LABEL); l.setForeground(TEXT_MUTED);
        p.add(l);
    }

    private void adapterFiltresSelonType() {
        String type = (String) cmbType.getSelectedItem();
        boolean isProduit     = "Produits".equals(type);
        boolean isEntree      = "Entrées stock".equals(type);
        boolean isSortie      = "Sorties stock".equals(type);
        boolean isFournisseur = "Fournisseurs".equals(type);

        cmbCategorie.setEnabled(isProduit);
        cmbStatut.setEnabled(isProduit || isSortie);
        cmbFournisseur.setEnabled(isEntree || isFournisseur);
        txtDateDu.setEnabled(!isFournisseur); txtDateAu.setEnabled(!isFournisseur);
        btnCalDu.setEnabled(!isFournisseur);  btnCalAu.setEnabled(!isFournisseur);

        // Adapter les options statut selon le type
        cmbStatut.removeAllItems();
        if (isProduit) {
            for (String s : new String[]{"Tous","✓ OK","⚠ Alerte"}) cmbStatut.addItem(s);
        } else if (isSortie) {
            for (String s : new String[]{"Tous","Vente","Offert","Perte","Retour"}) cmbStatut.addItem(s);
        } else {
            cmbStatut.addItem("Tous");
        }
        appliquerFiltres();
    }

    // =========================================================================
    //  ZONE TABLEAU
    // =========================================================================
    private JPanel creerZoneTableau() {
        JPanel zone = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_PANEL);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),10,10));
                g2.dispose();
            }
        };
        zone.setOpaque(false);
        zone.setBorder(new EmptyBorder(14, 16, 14, 16));

        // En-tête zone
        JPanel entete = new JPanel(new BorderLayout());
        entete.setOpaque(false);
        JLabel lblZone = new JLabel("APERÇU AVANT IMPRESSION");
        lblZone.setFont(FONT_SECTION); lblZone.setForeground(GOLD);
        entete.add(lblZone, BorderLayout.WEST);
        lblCompteur = new JLabel("");
        lblCompteur.setFont(FONT_LABEL); lblCompteur.setForeground(TEXT_MUTED);
        entete.add(lblCompteur, BorderLayout.EAST);
        zone.add(entete, BorderLayout.NORTH);

        // Tableau
        tableModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        jTable = new JTable(tableModel);
        jTable.setFont(FONT_TABLE); jTable.setRowHeight(42);
        jTable.setBackground(BG_PANEL); jTable.setForeground(TEXT_PRIMARY);
        jTable.setGridColor(BORDER);
        jTable.setSelectionBackground(SEL_BG); jTable.setSelectionForeground(SEL_FG);
        jTable.setShowVerticalLines(true); jTable.setShowHorizontalLines(true);
        jTable.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader th = jTable.getTableHeader();
        th.setFont(FONT_HEADER); th.setBackground(BG_DARK); th.setForeground(GOLD);
        th.setPreferredSize(new Dimension(th.getWidth(), 40));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GOLD));
        ((DefaultTableCellRenderer) th.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.LEFT);

        jTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(FONT_TABLE);
                if (sel) { setBackground(SEL_BG); setForeground(SEL_FG); }
                else {
                    setBackground(row % 2 == 0 ? BG_PANEL : BG_ROW_ALT);
                    setForeground(TEXT_PRIMARY);
                    // coloration contextuelle selon valeur
                    if (v != null) {
                        String s = v.toString();
                        if (s.equals("✓ OK"))    { setForeground(OK_GREEN); setFont(new Font("Baskerville Old Face", Font.BOLD, 12)); }
                        if (s.equals("⚠ Alerte")){ setForeground(ALERT_RED); setFont(new Font("Baskerville Old Face", Font.BOLD, 12)); }
                        if (s.startsWith("+"))   setForeground(OK_GREEN);
                        if (s.startsWith("-"))   setForeground(ALERT_RED);
                        if (s.contains("FCFA") || s.contains("F"))  setForeground(GOLD_LIGHT);
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(jTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scroll.getViewport().setBackground(BG_PANEL);
        zone.add(scroll, BorderLayout.CENTER);
        return zone;
    }

    // =========================================================================
    //  FILTRAGE
    // =========================================================================
    private void appliquerFiltres() {
        String type = (String) cmbType.getSelectedItem();
        switch (type) {
            case "Produits"      -> filtrerProduits();
            case "Entrées stock" -> filtrerEntrees();
            case "Sorties stock" -> filtrerSorties();
            case "Fournisseurs"  -> filtrerFournisseurs();
        }
    }

    private LocalDate parseDateDu() {
        try {
            return new SimpleDateFormat(DATE_FMT).parse(txtDateDu.getText().trim())
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) { return null; }
    }

    private LocalDate parseDateAu() {
        try {
            return new SimpleDateFormat(DATE_FMT).parse(txtDateAu.getText().trim())
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) { return null; }
    }

    private boolean dansIntervalle(LocalDate date, LocalDate du, LocalDate au) {
        if (date == null) return true;
        if (du != null && date.isBefore(du)) return false;
        if (au != null && date.isAfter(au)) return false;
        return true;
    }

    // ── Produits ───────────────────────────────────
    private void filtrerProduits() {
        tableModel.setColumnIdentifiers(new String[]{
            "NOM", "MARQUE", "CATÉGORIE", "CONTENANCE", "PRIX ACHAT", "PRIX VENTE", "STOCK", "SEUIL", "ÉTAT"});
        tableModel.setRowCount(0);

        String statut = (String) cmbStatut.getSelectedItem();
        String cat    = (String) cmbCategorie.getSelectedItem();

        try {
            for (Produit p : new ProduitDAO().findAll()) {
                String etat = p.getQuantiteStock() <= p.getSeuilAlerte() ? "⚠ Alerte" : "✓ OK";
                if (!"Tous".equals(statut) && !etat.equals(statut)) continue;
                if (!"Toutes".equals(cat) && (p.getCategorie() == null
                        || !p.getCategorie().name().equals(cat))) continue;

                tableModel.addRow(new Object[]{
                    p.getNom(), p.getMarque(),
                    p.getCategorie() != null ? p.getCategorie().name() : "",
                    p.getContenanceMl() != null ? p.getContenanceMl() + " ml" : "-",
                    p.getPrixAchat() + " FCFA",
                    p.getPrixVente() + " FCFA",
                    p.getQuantiteStock(), p.getSeuilAlerte(), etat
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        majCompteur("produit");
        ajusterColonnes(new int[]{160,110,90,85,95,95,55,55,75});
    }

    // ── Entrées stock ──────────────────────────────
    private void filtrerEntrees() {
        tableModel.setColumnIdentifiers(new String[]{
            "DATE", "PRODUIT", "FOURNISSEUR", "QUANTITÉ", "PRIX UNITAIRE"});
        tableModel.setRowCount(0);

        LocalDate du = parseDateDu(), au = parseDateAu();
        String four = (String) cmbFournisseur.getSelectedItem();

        try {
            for (EntreeStock es : new EntreeStockDAO().findAll()) {
                LocalDate date = es.getDateEntree() != null
                    ? es.getDateEntree().toLocalDate() : null;
                if (!dansIntervalle(date, du, au)) continue;

                String nomFour = nomFournisseur(es.getIdFournisseur());
                if (!"Tous".equals(four) && !nomFour.equals(four)) continue;

                tableModel.addRow(new Object[]{
                    date != null ? date.format(DTF) : "",
                    nomProduit(es.getIdProduit()),
                    nomFour,
                    "+" + es.getQuantite(),
                    es.getPrixUnitaire() + " FCFA"
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        majCompteur("entrée");
        ajusterColonnes(new int[]{90,180,150,70,110});
    }

    // ── Sorties stock ──────────────────────────────
    private void filtrerSorties() {
        tableModel.setColumnIdentifiers(new String[]{
            "DATE", "PRODUIT", "QUANTITÉ", "MOTIF", "PRIX VENTE"});
        tableModel.setRowCount(0);

        LocalDate du = parseDateDu(), au = parseDateAu();
        String motifFiltré = (String) cmbStatut.getSelectedItem();

        try {
            for (SortieStock ss : new SortieStockDAO().findAll()) {
                if (!dansIntervalle(ss.getDateSortie(), du, au)) continue;
                if (!"Tous".equals(motifFiltré) && !motifFiltré.equals(ss.getMotif())) continue;

                tableModel.addRow(new Object[]{
                    ss.getDateSortie() != null ? ss.getDateSortie().format(DTF) : "",
                    nomProduit(ss.getIdProduit()),
                    "-" + ss.getQuantite(),
                    ss.getMotif(),
                    ss.getPrixVente() + " FCFA"
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        majCompteur("sortie");
        ajusterColonnes(new int[]{90,190,70,90,110});
    }

    // ── Fournisseurs ───────────────────────────────
    private void filtrerFournisseurs() {
        tableModel.setColumnIdentifiers(new String[]{
            "NOM", "TÉLÉPHONE", "EMAIL", "ADRESSE"});
        tableModel.setRowCount(0);

        String filtreFour = (String) cmbFournisseur.getSelectedItem();
        try {
            for (Fournisseur f : new FournisseurDAO().findAll()) {
                if (!"Tous".equals(filtreFour) && !f.getNom().equals(filtreFour)) continue;
                tableModel.addRow(new Object[]{
                    f.getNom(),
                    f.getTelephone() != null ? f.getTelephone() : "",
                    f.getEmail()     != null ? f.getEmail()     : "",
                    f.getAdresse()   != null ? f.getAdresse()   : ""
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        majCompteur("fournisseur");
        ajusterColonnes(new int[]{160,120,180,200});
    }

    private void majCompteur(String mot) {
        int n = tableModel.getRowCount();
        lblCompteur.setText(n + " " + mot + (n > 1 ? "s" : "") + " — prêt à imprimer");
    }

    private void ajusterColonnes(int[] widths) {
        if (jTable.getColumnCount() != widths.length) return;
        for (int i = 0; i < widths.length; i++)
            jTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    private void reinitialiser() {
        cmbType.setSelectedIndex(0);
        txtDateDu.setText("01/01/2024");
        txtDateAu.setText(LocalDate.now().format(DTF));
        cmbFournisseur.setSelectedIndex(0);
        cmbCategorie.setSelectedIndex(0);
        cmbStatut.setSelectedIndex(0);
        adapterFiltresSelonType();
    }

    // =========================================================================
    //  IMPRESSION
    // =========================================================================
    private void imprimerDonnees() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Aucune donnée à imprimer.\nAppliquez un filtre d'abord.",
                "Impression", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String type    = (String) cmbType.getSelectedItem();
        String dateRef = "Période : " + txtDateDu.getText() + " → " + txtDateAu.getText();

        // Snapshot du tableau
        final int nbCols = tableModel.getColumnCount();
        final int nbRows = tableModel.getRowCount();
        final String[] colNames = new String[nbCols];
        for (int c = 0; c < nbCols; c++) colNames[c] = tableModel.getColumnName(c);
        final String[][] data = new String[nbRows][nbCols];
        for (int r = 0; r < nbRows; r++)
            for (int c = 0; c < nbCols; c++) {
                Object v = tableModel.getValueAt(r, c);
                data[r][c] = v != null ? v.toString() : "";
            }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Parfumerie — " + type);

        PageFormat pf = job.defaultPage();
        pf.setOrientation(nbCols >= 6 ? PageFormat.LANDSCAPE : PageFormat.PORTRAIT);

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2 = (Graphics2D) graphics;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            int pw = (int) pageFormat.getImageableWidth();
            int x = 0, y = 0;

            // ── En-tête société ──
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Serif", Font.BOLD, 18));
            g2.drawString("MA PARFUMERIE", x, y + 18); y += 26;
            g2.setFont(new Font("Serif", Font.PLAIN, 9));
            g2.setColor(new Color(100, 100, 100));
            g2.drawString("Gestion de parfumerie", x, y + 8); y += 14;
            g2.drawLine(x, y, x + pw, y); y += 14;

            // ── Titre rapport ──
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Serif", Font.BOLD, 13));
            g2.drawString("RAPPORT : " + type.toUpperCase(), x, y + 12); y += 18;
            g2.setFont(new Font("Serif", Font.PLAIN, 9));
            g2.setColor(new Color(120, 120, 120));
            g2.drawString(dateRef + "   |   " + nbRows + " enregistrement(s)   |   "
                + "Imprimé le " + LocalDate.now().format(DTF), x, y + 9); y += 18;
            g2.setColor(Color.BLACK);

            // ── En-tête colonnes ──
            int colW = pw / nbCols;
            g2.setColor(new Color(230, 230, 230));
            g2.fillRect(x, y, pw, 18);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Serif", Font.BOLD, 9));
            for (int c = 0; c < nbCols; c++)
                g2.drawString(colNames[c], x + c * colW + 3, y + 13);
            y += 20;

            // ── Lignes ──
            g2.setFont(new Font("Serif", Font.PLAIN, 9));
            boolean alt = false;
            for (String[] row : data) {
                if (alt) {
                    g2.setColor(new Color(248, 248, 248));
                    g2.fillRect(x, y, pw, 16);
                }
                g2.setColor(Color.BLACK);
                for (int c = 0; c < nbCols; c++) {
                    String val = row[c];
                    if (val.length() > 25) val = val.substring(0, 23) + "..";
                    g2.drawString(val, x + c * colW + 3, y + 12);
                }
                y += 16; alt = !alt;
                if (y > pageFormat.getImageableHeight() - 30) break;
            }

            // ── Pied ──
            g2.setFont(new Font("Serif", Font.ITALIC, 8));
            g2.setColor(new Color(150, 150, 150));
            int yPied = (int) pageFormat.getImageableHeight() - 8;
            g2.drawLine(x, yPied - 8, x + pw, yPied - 8);
            g2.drawString("MA PARFUMERIE  --  Document généré automatiquement", x, yPied);

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
    //  CALENDRIER POPUP
    // =========================================================================
    private void afficherCalendrier(JTextField cible, JButton ancre) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dlg.setUndecorated(true);

        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat(DATE_FMT).parse(cible.getText().trim()));
        } catch (Exception ignored) {}

        final int[] an = {cal.get(Calendar.YEAR)};
        final int[] mo = {cal.get(Calendar.MONTH)};
        String[] MOIS  = {"Janv","Févr","Mars","Avr","Mai","Juin",
                          "Juil","Août","Sept","Oct","Nov","Déc"};
        String[] JOURS = {"Lu","Ma","Me","Je","Ve","Sa","Di"};

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_PANEL);
        root.setBorder(BorderFactory.createLineBorder(GOLD, 1));

        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(BG_DARK); nav.setBorder(new EmptyBorder(6,8,6,8));
        JButton prev = calNavBtn("◀"); JButton next = calNavBtn("▶");
        JLabel lblM = new JLabel("", SwingConstants.CENTER);
        lblM.setFont(new Font("Baskerville Old Face", Font.BOLD, 11));
        lblM.setForeground(GOLD);
        nav.add(prev, BorderLayout.WEST); nav.add(lblM, BorderLayout.CENTER); nav.add(next, BorderLayout.EAST);
        root.add(nav, BorderLayout.NORTH);

        JPanel grille = new JPanel(new GridLayout(0, 7, 2, 2));
        grille.setBackground(BG_PANEL); grille.setBorder(new EmptyBorder(4,6,6,6));
        root.add(grille, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        footer.setBackground(BG_DARK);
        JButton btnAuj = calNavBtn("Auj."); JButton btnFermer = calNavBtn("✕");
        btnFermer.setForeground(ALERT_RED);
        footer.add(btnAuj); footer.add(btnFermer);
        root.add(footer, BorderLayout.SOUTH);

        Runnable[] rf = {null};
        rf[0] = () -> {
            grille.removeAll();
            lblM.setText(MOIS[mo[0]] + " " + an[0]);
            for (String j : JOURS) {
                JLabel lj = new JLabel(j, SwingConstants.CENTER);
                lj.setFont(new Font("Baskerville Old Face", Font.BOLD, 9));
                lj.setForeground(GOLD); grille.add(lj);
            }
            Calendar tmp = Calendar.getInstance(); tmp.set(an[0], mo[0], 1);
            int prem = (tmp.get(Calendar.DAY_OF_WEEK) + 5) % 7;
            for (int i=0;i<prem;i++) grille.add(new JLabel());
            int nbJ = tmp.getActualMaximum(Calendar.DAY_OF_MONTH);
            Calendar auj = Calendar.getInstance();
            for (int d=1; d<=nbJ; d++) {
                final int jour=d;
                JButton bj = new JButton(String.valueOf(d));
                bj.setFont(new Font("Baskerville Old Face",Font.PLAIN,9));
                bj.setBorderPainted(false); bj.setFocusPainted(false);
                bj.setCursor(new Cursor(Cursor.HAND_CURSOR));
                boolean isAuj = (d==auj.get(Calendar.DAY_OF_MONTH)
                    && mo[0]==auj.get(Calendar.MONTH) && an[0]==auj.get(Calendar.YEAR));
                bj.setBackground(isAuj ? GOLD : BG_FIELD);
                bj.setForeground(isAuj ? BG_DARK : TEXT_PRIMARY);
                bj.addActionListener(ev -> {
                    cible.setText(String.format("%02d/%02d/%04d", jour, mo[0]+1, an[0]));
                    dlg.dispose(); appliquerFiltres();
                });
                grille.add(bj);
            }
            grille.revalidate(); grille.repaint();
        };

        prev.addActionListener(e -> {mo[0]--; if(mo[0]<0){mo[0]=11;an[0]--;} rf[0].run();});
        next.addActionListener(e -> {mo[0]++; if(mo[0]>11){mo[0]=0;an[0]++;} rf[0].run();});
        btnAuj.addActionListener(e -> {Calendar n=Calendar.getInstance(); an[0]=n.get(Calendar.YEAR); mo[0]=n.get(Calendar.MONTH); rf[0].run();});
        btnFermer.addActionListener(e -> dlg.dispose());

        rf[0].run();
        dlg.setContentPane(root); dlg.setSize(220, 240);
        Point loc = ancre.getLocationOnScreen();
        dlg.setLocation(loc.x, loc.y + ancre.getHeight() + 2);
        dlg.setVisible(true);
    }

    private JButton calNavBtn(String t) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
        b.setBackground(BG_DARK); b.setForeground(TEXT_MUTED);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(30, 22));
        return b;
    }

    // =========================================================================
    //  HELPERS UI
    // =========================================================================
    private void styleCombo(JComboBox<String> c, int w) {
        c.setFont(FONT_FIELD); c.setBackground(BG_FIELD);
        c.setForeground(TEXT_PRIMARY); c.setPreferredSize(new Dimension(w, 28));
    }

    private JTextField creerChampDate(String val) {
        JTextField f = new JTextField(val);
        f.setFont(FONT_FIELD); f.setBackground(BG_FIELD); f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(GOLD);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(3, 7, 3, 7)));
        f.setPreferredSize(new Dimension(90, 28));
        return f;
    }

    private JButton creerBtnCal() {
        JButton b = new JButton("📅");
        b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        b.setBackground(BG_FIELD); b.setForeground(GOLD);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(28, 28));
        return b;
    }

    private JButton styleBtnAction(String txt, Color bg, Color fg, int w) {
        JButton b = new JButton(txt);
        b.setBackground(bg); b.setForeground(fg); b.setFont(FONT_BTN);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(w, 28));
        return b;
    }

    private JPanel sepV() {
        JPanel s = new JPanel(); s.setBackground(BORDER);
        s.setPreferredSize(new Dimension(1, 20)); return s;
    }
}
