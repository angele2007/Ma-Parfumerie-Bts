package parfumerie.vues;

import parfumerie.dao.AlerteDAO;
import parfumerie.dao.EntreeStockDAO;
import parfumerie.dao.FactureDAO;
import parfumerie.dao.ProduitDAO;
import parfumerie.dao.SortieStockDAO;
import parfumerie.modele.Produit;
import parfumerie.modeles.Alerte;
import parfumerie.modeles.EntreeStock;
import parfumerie.modeles.SortieStock;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * Tableau de bord – Design Luxe Parfumerie.
 * @author Angele
 */
public class TableauBord extends javax.swing.JPanel {

    // ── Palette ───────────────────────────────────
    private static final Color C_BG       = new Color(15, 12, 20);
    private static final Color C_SURFACE  = new Color(26, 22, 35);
    private static final Color C_SURFACE2 = new Color(35, 30, 48);
    private static final Color C_OR       = new Color(212, 175, 105);
    private static final Color C_OR_LIGHT = new Color(238, 210, 150);
    private static final Color C_TEXT     = new Color(240, 235, 225);
    private static final Color C_TEXT_DIM = new Color(150, 140, 130);
    private static final Color C_VERT     = new Color(80, 200, 140);
    private static final Color C_JAUNE    = new Color(255, 200, 80);
    private static final Color C_ROUGE    = new Color(220, 80, 80);
    private static final Color C_BORDER   = new Color(50, 44, 62);
    private static final Color C_FILTER_BG= new Color(22, 18, 30);

    private static final String DATE_FMT = "dd/MM/yyyy";

    // ── Composants ────────────────────────────────
    private JLabel lblNbProd, lblChiffreA, lblNbreProd, lblFacImpayer;
    private JLabel lblMontantFac, lblAugentationCA, lblProdRecent;
    private JPanel pnlAlertes, pnlMouvements;
    private JTable jTable1;
    private DefaultTableModel tableModel;
    private JTextField txtRecherche;

    // ── Filtres ───────────────────────────────────
    private JTextField txtDateDu, txtDateAu;
    private JButton    btnCalDu, btnCalAu;
    private JComboBox<String> cmbStatut;
    private JButton    btnFiltrer, btnReset;

    // Cache produits pour filtrage
    private List<Produit> cacheProduits = new java.util.ArrayList<>();

    // =========================================================================
    public TableauBord() {
        setBackground(C_BG);
        setLayout(new BorderLayout());
        construireUI();
        chargerTout();
    }

    public void miseAJour() { chargerTout(); }

    private void chargerTout() {
        chargerCacheNomsProduits();
        chargerStatistiques();
        chargerProduitsRecents();
        chargerAlertesStock();
        chargerMouvementsRecents();
    }

    private final java.util.Map<Integer, String> cacheNoms = new java.util.HashMap<>();

    private void chargerCacheNomsProduits() {
        try {
            cacheNoms.clear();
            cacheProduits = new ProduitDAO().findAll();
            for (Produit p : cacheProduits) cacheNoms.put(p.getIdProduit(), p.getNom());
        } catch (Exception ignored) {}
    }

    private String nomProduit(int id) { return cacheNoms.getOrDefault(id, "Produit #" + id); }

    // =========================================================================
    //  CONSTRUCTION UI
    // =========================================================================
    private void construireUI() {

        // ── HEADER ─────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C_BG);
        header.setBorder(new EmptyBorder(20, 28, 10, 28));
        header.add(creerLabelTracked("TABLEAU DE BORD", 11), BorderLayout.WEST);

        txtRecherche = creerChampRecherche();
        txtRecherche.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String t = txtRecherche.getText().trim().toLowerCase();
                if (!t.equals("rechercher...")) appliquerFiltres();
            }
        });
        txtRecherche.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (txtRecherche.getText().equals("Rechercher...")) {
                    txtRecherche.setText(""); txtRecherche.setForeground(C_TEXT);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (txtRecherche.getText().isEmpty()) {
                    txtRecherche.setText("Rechercher..."); txtRecherche.setForeground(C_TEXT_DIM);
                    appliquerFiltres();
                }
            }
        });
        header.add(txtRecherche, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── CORPS ─────────────────────────────────
        JPanel corps = new JPanel(new BorderLayout(0, 14));
        corps.setBackground(C_BG);
        corps.setBorder(new EmptyBorder(0, 24, 24, 24));

        // Cartes stats
        corps.add(creerZoneStats(), BorderLayout.NORTH);

        // Zone filtre + tableau + droite
        JPanel zoneCentrale = new JPanel(new BorderLayout(0, 10));
        zoneCentrale.setBackground(C_BG);

        // ── BARRE DE FILTRES ───────────────────────
        zoneCentrale.add(creerBarreFiltres(), BorderLayout.NORTH);

        JPanel centre = new JPanel(new BorderLayout(16, 0));
        centre.setBackground(C_BG);
        centre.add(creerZoneTableau(), BorderLayout.CENTER);
        centre.add(creerPanneauDroit(), BorderLayout.EAST);
        zoneCentrale.add(centre, BorderLayout.CENTER);

        corps.add(zoneCentrale, BorderLayout.CENTER);
        add(corps, BorderLayout.CENTER);
    }

    // =========================================================================
    //  BARRE DE FILTRES
    // =========================================================================
    private JPanel creerBarreFiltres() {
        JPanel barre = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_FILTER_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(C_OR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 10, 10));
                g2.dispose();
            }
        };
        barre.setOpaque(false);
        barre.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));
        barre.setBorder(new EmptyBorder(2, 6, 2, 6));

        // Label
        JLabel lblFiltre = new JLabel("🔍  FILTRES");
        lblFiltre.setFont(new Font("Baskerville Old Face", Font.BOLD, 11));
        lblFiltre.setForeground(C_OR);
        barre.add(lblFiltre);

        // Séparateur vertical
        barre.add(creerSepV());

        // ── Date Du ────────────────────────────────
        JLabel lDu = new JLabel("Du :");
        lDu.setFont(new Font("Baskerville Old Face", Font.PLAIN, 11));
        lDu.setForeground(C_TEXT_DIM);
        barre.add(lDu);

        txtDateDu = creerChampDate("01/01/2024");
        barre.add(txtDateDu);

        btnCalDu = creerBtnCal();
        btnCalDu.addActionListener(e -> afficherCalendrier(txtDateDu, btnCalDu));
        barre.add(btnCalDu);

        // ── Date Au ────────────────────────────────
        JLabel lAu = new JLabel("Au :");
        lAu.setFont(new Font("Baskerville Old Face", Font.PLAIN, 11));
        lAu.setForeground(C_TEXT_DIM);
        barre.add(lAu);

        txtDateAu = creerChampDate(LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern(DATE_FMT)));
        barre.add(txtDateAu);

        btnCalAu = creerBtnCal();
        btnCalAu.addActionListener(e -> afficherCalendrier(txtDateAu, btnCalAu));
        barre.add(btnCalAu);

        // Séparateur
        barre.add(creerSepV());

        // ── Statut ─────────────────────────────────
        JLabel lSt = new JLabel("Statut :");
        lSt.setFont(new Font("Baskerville Old Face", Font.PLAIN, 11));
        lSt.setForeground(C_TEXT_DIM);
        barre.add(lSt);

        cmbStatut = new JComboBox<>(new String[]{"Tous", "✓ OK", "⚠ ALERTE"});
        cmbStatut.setFont(new Font("Baskerville Old Face", Font.PLAIN, 12));
        cmbStatut.setBackground(C_SURFACE);
        cmbStatut.setForeground(C_TEXT);
        cmbStatut.setPreferredSize(new Dimension(120, 28));
        cmbStatut.addActionListener(e -> appliquerFiltres());
        barre.add(cmbStatut);

        // Séparateur
        barre.add(creerSepV());

        // ── Bouton Filtrer ─────────────────────────
        btnFiltrer = new JButton("Appliquer");
        styleBtnFiltre(btnFiltrer, C_OR, new Color(15, 12, 20));
        btnFiltrer.addActionListener(e -> appliquerFiltres());
        barre.add(btnFiltrer);

        // ── Bouton Reset ────────────────────────────
        btnReset = new JButton("Réinitialiser");
        styleBtnFiltre(btnReset, new Color(45, 38, 55), C_TEXT_DIM);
        btnReset.addActionListener(e -> reinitialiserFiltres());
        barre.add(btnReset);

        return barre;
    }

    private JTextField creerChampDate(String valDef) {
        JTextField f = new JTextField(valDef);
        f.setFont(new Font("Baskerville Old Face", Font.PLAIN, 12));
        f.setBackground(C_SURFACE);
        f.setForeground(C_TEXT);
        f.setCaretColor(C_OR);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_BORDER),
            BorderFactory.createEmptyBorder(3, 7, 3, 7)));
        f.setPreferredSize(new Dimension(92, 28));
        // filtre : chiffres + /
        ((javax.swing.text.AbstractDocument) f.getDocument())
            .setDocumentFilter(new javax.swing.text.DocumentFilter() {
                @Override public void insertString(FilterBypass fb, int o, String t,
                        javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                    if (t != null && t.matches("[0-9/]*") && fb.getDocument().getLength() + t.length() <= 10)
                        super.insertString(fb, o, t, a);
                }
                @Override public void replace(FilterBypass fb, int o, int l, String t,
                        javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                    int nl = fb.getDocument().getLength() - l + (t != null ? t.length() : 0);
                    if (t != null && t.matches("[0-9/]*") && nl <= 10)
                        super.replace(fb, o, l, t, a);
                }
            });
        return f;
    }

    private JButton creerBtnCal() {
        JButton b = new JButton("📅");
        b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        b.setBackground(C_SURFACE);
        b.setForeground(C_OR);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(30, 28));
        return b;
    }

    private void styleBtnFiltre(JButton b, Color bg, Color fg) {
        b.setBackground(bg); b.setForeground(fg);
        b.setFont(new Font("Baskerville Old Face", Font.PLAIN, 11));
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(100, 28));
    }

    private JPanel creerSepV() {
        JPanel s = new JPanel();
        s.setBackground(C_BORDER);
        s.setPreferredSize(new Dimension(1, 22));
        return s;
    }

    // =========================================================================
    //  CALENDRIER POPUP
    // =========================================================================
    private void afficherCalendrier(JTextField cible, JButton ancre) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        dlg.setUndecorated(true);

        Calendar cal = Calendar.getInstance();
        try {
            Date d = new SimpleDateFormat(DATE_FMT).parse(cible.getText().trim());
            cal.setTime(d);
        } catch (Exception ignored) {}

        final int[] an = {cal.get(Calendar.YEAR)};
        final int[] mo = {cal.get(Calendar.MONTH)};

        String[] MOIS = {"Janv","Févr","Mars","Avr","Mai","Juin",
                         "Juil","Août","Sept","Oct","Nov","Déc"};
        String[] JOURS = {"Lu","Ma","Me","Je","Ve","Sa","Di"};

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_SURFACE);
        root.setBorder(BorderFactory.createLineBorder(C_OR, 1));

        // Nav
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(new Color(15,12,20));
        nav.setBorder(new EmptyBorder(6, 8, 6, 8));
        JButton prev = calNavBtn("◀"); JButton next = calNavBtn("▶");
        JLabel lblM = new JLabel("", SwingConstants.CENTER);
        lblM.setFont(new Font("Baskerville Old Face", Font.BOLD, 12));
        lblM.setForeground(C_OR);
        nav.add(prev, BorderLayout.WEST);
        nav.add(lblM, BorderLayout.CENTER);
        nav.add(next, BorderLayout.EAST);
        root.add(nav, BorderLayout.NORTH);

        JPanel grille = new JPanel(new GridLayout(0, 7, 2, 2));
        grille.setBackground(C_SURFACE);
        grille.setBorder(new EmptyBorder(4, 6, 6, 6));
        root.add(grille, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        footer.setBackground(new Color(15,12,20));
        JButton btnAuj = new JButton("Aujourd'hui");
        btnAuj.setFont(new Font("Baskerville Old Face", Font.PLAIN, 10));
        btnAuj.setBackground(C_SURFACE); btnAuj.setForeground(C_TEXT_DIM);
        btnAuj.setBorderPainted(false); btnAuj.setFocusPainted(false);
        JButton btnFermer = calNavBtn("✕");
        btnFermer.setForeground(C_ROUGE);
        footer.add(btnAuj); footer.add(btnFermer);
        root.add(footer, BorderLayout.SOUTH);

        Runnable[] remplir = {null};
        remplir[0] = () -> {
            grille.removeAll();
            lblM.setText(MOIS[mo[0]] + " " + an[0]);
            for (String j : JOURS) {
                JLabel lj = new JLabel(j, SwingConstants.CENTER);
                lj.setFont(new Font("Baskerville Old Face", Font.BOLD, 10));
                lj.setForeground(C_OR); grille.add(lj);
            }
            Calendar tmp = Calendar.getInstance();
            tmp.set(an[0], mo[0], 1);
            int premier = (tmp.get(Calendar.DAY_OF_WEEK) + 5) % 7;
            for (int i = 0; i < premier; i++) grille.add(new JLabel());
            int nbJ = tmp.getActualMaximum(Calendar.DAY_OF_MONTH);
            Calendar auj = Calendar.getInstance();
            for (int d = 1; d <= nbJ; d++) {
                final int jour = d;
                JButton bj = new JButton(String.valueOf(d));
                bj.setFont(new Font("Baskerville Old Face", Font.PLAIN, 10));
                bj.setBorderPainted(false); bj.setFocusPainted(false);
                bj.setCursor(new Cursor(Cursor.HAND_CURSOR));
                boolean estAuj = (d == auj.get(Calendar.DAY_OF_MONTH)
                    && mo[0] == auj.get(Calendar.MONTH)
                    && an[0] == auj.get(Calendar.YEAR));
                bj.setBackground(estAuj ? C_OR : C_SURFACE2);
                bj.setForeground(estAuj ? new Color(15,12,20) : C_TEXT);
                bj.addActionListener(ev -> {
                    cible.setText(String.format("%02d/%02d/%04d", jour, mo[0]+1, an[0]));
                    dlg.dispose();
                    appliquerFiltres();
                });
                grille.add(bj);
            }
            grille.revalidate(); grille.repaint();
        };

        prev.addActionListener(e -> { mo[0]--; if (mo[0]<0){mo[0]=11;an[0]--;} remplir[0].run(); });
        next.addActionListener(e -> { mo[0]++; if (mo[0]>11){mo[0]=0;an[0]++;} remplir[0].run(); });
        btnAuj.addActionListener(e -> { Calendar n=Calendar.getInstance(); an[0]=n.get(Calendar.YEAR); mo[0]=n.get(Calendar.MONTH); remplir[0].run(); });
        btnFermer.addActionListener(e -> dlg.dispose());

        remplir[0].run();
        dlg.setContentPane(root);
        dlg.setSize(230, 250);
        Point loc = ancre.getLocationOnScreen();
        dlg.setLocation(loc.x, loc.y + ancre.getHeight() + 2);
        dlg.setVisible(true);
    }

    private JButton calNavBtn(String t) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        b.setBackground(new Color(15,12,20)); b.setForeground(C_TEXT_DIM);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(26, 22));
        return b;
    }

    // =========================================================================
    //  FILTRAGE COMBINÉ
    // =========================================================================
    private void appliquerFiltres() {
        // 1. Parser les dates
        LocalDate dateMin = null, dateMax = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FMT);
        sdf.setLenient(false);
        try {
            String du = txtDateDu.getText().trim();
            if (!du.isEmpty()) dateMin = sdf.parse(du).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException ignored) {}
        try {
            String au = txtDateAu.getText().trim();
            if (!au.isEmpty()) dateMax = sdf.parse(au).toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException ignored) {}

        // 2. Terme de recherche
        String terme = txtRecherche.getText().trim().toLowerCase();
        if (terme.equals("rechercher...")) terme = "";

        // 3. Statut
        String statut = (String) cmbStatut.getSelectedItem();

        // 4. Recharger le cache puis filtrer
        try {
            List<Produit> liste = new ProduitDAO().findAll();
            tableModel.setRowCount(0);
            int count = 0;

            for (Produit p : liste) {
                String etat = p.getQuantiteStock() <= p.getSeuilAlerte() ? "ALERTE" : "OK";

                // Filtre statut
                if (!"Tous".equals(statut)) {
                    if ("✓ OK".equals(statut) && !"OK".equals(etat)) continue;
                    if ("⚠ ALERTE".equals(statut) && !"ALERTE".equals(etat)) continue;
                }

                // Filtre recherche texte
                if (!terme.isEmpty()) {
                    String nom = p.getNom() != null ? p.getNom().toLowerCase() : "";
                    String cat = p.getCategorie() != null ? p.getCategorie().toString().toLowerCase() : "";
                    if (!nom.contains(terme) && !cat.contains(terme)) continue;
                }

                // Filtre date (sur dateCreation si disponible, sinon on passe)
                // Note: Produit n'a pas de date, on filtre les mouvements plus bas
                // Ici on garde le produit et on vérifie via les entrées
                if (dateMin != null || dateMax != null) {
                    boolean trouveDansIntervalle = false;
                    for (Produit pp : cacheProduits) {
                        if (pp.getIdProduit() == p.getIdProduit()) {
                            // Accepter si dans cacheProduits (date non dispo sur Produit)
                            trouveDansIntervalle = true;
                            break;
                        }
                    }
                    // Filtre date sur les entrées stock liées à ce produit
                    try {
                        List<EntreeStock> entrees = new EntreeStockDAO().findAll();
                        boolean dansDate = false;
                        for (EntreeStock es : entrees) {
                            if (es.getIdProduit() != p.getIdProduit()) continue;
                            if (es.getDateEntree() == null) continue;
                            LocalDate d = es.getDateEntree().toLocalDate();
                            boolean apresMin = (dateMin == null || !d.isBefore(dateMin));
                            boolean avantMax = (dateMax == null || !d.isAfter(dateMax));
                            if (apresMin && avantMax) { dansDate = true; break; }
                        }
                        // Aussi vérifier les sorties
                        if (!dansDate) {
                            List<SortieStock> sorties = new SortieStockDAO().findAll();
                            for (SortieStock ss : sorties) {
                                if (ss.getIdProduit() != p.getIdProduit()) continue;
                                if (ss.getDateSortie() == null) continue;
                                LocalDate d = ss.getDateSortie();
                                boolean apresMin = (dateMin == null || !d.isBefore(dateMin));
                                boolean avantMax = (dateMax == null || !d.isAfter(dateMax));
                                if (apresMin && avantMax) { dansDate = true; break; }
                            }
                        }
                        if (!dansDate) continue;
                    } catch (Exception ignored) {}
                }

                tableModel.addRow(new Object[]{
                    p.getNom(),
                    p.getCategorie() != null ? p.getCategorie().toString() : "",
                    p.getQuantiteStock(),
                    String.format("%,.0f F", p.getPrixVente().doubleValue()),
                    etat
                });
                count++;
            }
            lblProdRecent.setText(count + " produits");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void reinitialiserFiltres() {
        txtDateDu.setText("01/01/2024");
        txtDateAu.setText(LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern(DATE_FMT)));
        cmbStatut.setSelectedIndex(0);
        txtRecherche.setText("Rechercher...");
        txtRecherche.setForeground(C_TEXT_DIM);
        chargerProduitsRecents();
    }

    // ── Cartes statistiques ────────────────────────────────────────────────────
    private JPanel creerZoneStats() {
        JPanel zone = new JPanel(new GridLayout(1, 4, 14, 0));
        zone.setBackground(C_BG);
        zone.setPreferredSize(new Dimension(0, 110));

        JPanel c1 = creerCarte();
        lblNbProd = creerLabelGrand("--");
        c1.add(creerLabelTracked("PRODUITS EN STOCK", 9), "label");
        c1.add(lblNbProd, "valeur");
        JLabel sub1 = new JLabel("↑ catalogue actif");
        sub1.setFont(new Font("Baskerville Old Face", Font.PLAIN, 10));
        sub1.setForeground(C_OR); c1.add(sub1, "sub");
        zone.add(c1);

        JPanel c2 = creerCarte();
        lblChiffreA = creerLabelGrand("--");
        c2.add(creerLabelTracked("CHIFFRE D'AFFAIRES", 9), "label");
        c2.add(lblChiffreA, "valeur");
        lblAugentationCA = new JLabel("Ventes validées");
        lblAugentationCA.setFont(new Font("Baskerville Old Face", Font.PLAIN, 10));
        lblAugentationCA.setForeground(C_VERT); c2.add(lblAugentationCA, "sub");
        zone.add(c2);

        JPanel c3 = creerCarte();
        lblNbreProd = creerLabelGrand("--");
        c3.add(creerLabelTracked("ALERTES STOCK", 9), "label");
        c3.add(lblNbreProd, "valeur");
        JLabel sub3 = new JLabel("Réappro urgent");
        sub3.setFont(new Font("Baskerville Old Face", Font.PLAIN, 10));
        sub3.setForeground(C_ROUGE); c3.add(sub3, "sub");
        zone.add(c3);

        JPanel c4 = creerCarte();
        lblFacImpayer = creerLabelGrand("--");
        c4.add(creerLabelTracked("FACTURES IMPAYÉES", 9), "label");
        c4.add(lblFacImpayer, "valeur");
        lblMontantFac = new JLabel("-- FCFA");
        lblMontantFac.setFont(new Font("Baskerville Old Face", Font.PLAIN, 10));
        lblMontantFac.setForeground(C_JAUNE); c4.add(lblMontantFac, "sub");
        zone.add(c4);

        return zone;
    }

    private JPanel creerCarte() {
        JPanel carte = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_SURFACE);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),12,12));
                g2.setColor(C_OR); g2.setStroke(new BasicStroke(2f));
                g2.drawLine(16, 0, 60, 0);
                g2.dispose();
            }
        };
        carte.setOpaque(false);
        carte.setLayout(new BoxLayout(carte, BoxLayout.Y_AXIS));
        carte.setBorder(new EmptyBorder(16,18,14,18));
        return carte;
    }

    private JPanel creerZoneTableau() {
        JPanel zone = new JPanel(new BorderLayout(0,8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_SURFACE);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),12,12));
                g2.dispose();
            }
        };
        zone.setOpaque(false);
        zone.setBorder(new EmptyBorder(16,18,16,18));

        JPanel entete = new JPanel(new BorderLayout());
        entete.setOpaque(false);
        entete.add(creerLabelTracked("PRODUITS RÉCENTS", 9), BorderLayout.WEST);
        lblProdRecent = new JLabel("-- produits");
        lblProdRecent.setFont(new Font("Baskerville Old Face", Font.PLAIN, 11));
        lblProdRecent.setForeground(C_OR);
        entete.add(lblProdRecent, BorderLayout.EAST);
        zone.add(entete, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
            new String[]{"PRODUIT","CATÉGORIE","STOCK","PRIX","ÉTAT"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        jTable1 = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row%2==0 ? C_SURFACE : C_SURFACE2);
                c.setForeground(C_TEXT);
                if (isRowSelected(row)) c.setBackground(new Color(212,175,105,60));
                return c;
            }
        };
        styleTableau(jTable1);
        jTable1.getColumnModel().getColumn(2).setCellRenderer(new StockBarRenderer());
        jTable1.getColumnModel().getColumn(4).setCellRenderer(new EtatRenderer());

        JScrollPane scroll = new JScrollPane(jTable1);
        scroll.setBorder(null);
        scroll.setBackground(C_SURFACE);
        scroll.getViewport().setBackground(C_SURFACE);
        zone.add(scroll, BorderLayout.CENTER);
        return zone;
    }

    private JPanel creerPanneauDroit() {
        JPanel panneau = new JPanel();
        panneau.setLayout(new BoxLayout(panneau, BoxLayout.Y_AXIS));
        panneau.setOpaque(false);
        panneau.setPreferredSize(new Dimension(300, 0));

        JPanel secAlertes = creerSection("🔔 ALERTES STOCK");
        pnlAlertes = new JPanel();
        pnlAlertes.setLayout(new BoxLayout(pnlAlertes, BoxLayout.Y_AXIS));
        pnlAlertes.setBackground(C_SURFACE);
        JScrollPane scrollA = new JScrollPane(pnlAlertes);
        scrollA.setBorder(null); scrollA.getViewport().setBackground(C_SURFACE);
        scrollA.setPreferredSize(new Dimension(280, 180));
        secAlertes.add(scrollA, BorderLayout.CENTER);
        panneau.add(secAlertes);
        panneau.add(Box.createVerticalStrut(14));

        JPanel secMouv = creerSection("↕ MOUVEMENTS RÉCENTS");
        pnlMouvements = new JPanel();
        pnlMouvements.setLayout(new BoxLayout(pnlMouvements, BoxLayout.Y_AXIS));
        pnlMouvements.setBackground(C_SURFACE);
        JScrollPane scrollM = new JScrollPane(pnlMouvements);
        scrollM.setBorder(null); scrollM.getViewport().setBackground(C_SURFACE);
        secMouv.add(scrollM, BorderLayout.CENTER);
        panneau.add(secMouv);
        return panneau;
    }

    private JPanel creerSection(String titre) {
        JPanel sec = new JPanel(new BorderLayout(0,8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_SURFACE);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),12,12));
                g2.dispose();
            }
        };
        sec.setOpaque(false);
        sec.setBorder(new EmptyBorder(14,14,14,14));
        sec.add(creerLabelTracked(titre, 8), BorderLayout.NORTH);
        return sec;
    }

    // =========================================================================
    //  CHARGEMENT DONNÉES
    // =========================================================================
    private void chargerStatistiques() {
        try {
            ProduitDAO produitDAO = new ProduitDAO();
            FactureDAO factureDAO = new FactureDAO();
            lblNbProd.setText(produitDAO.countProduits() + " réf.");
            lblNbreProd.setText(produitDAO.countStockAlerte() + " produits");
            lblFacImpayer.setText(factureDAO.countFacturesImpayees() + " factures");
            lblMontantFac.setText(factureDAO.sommeFacturesImpayees() + " FCFA");
            lblChiffreA.setText(factureDAO.getChiffreAffaire() + " FCFA");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void chargerProduitsRecents() {
        try {
            List<Produit> produits = new ProduitDAO().findRecentProduits(10);
            tableModel.setRowCount(0);
            for (Produit p : produits) {
                String etat = p.getQuantiteStock() <= p.getSeuilAlerte() ? "ALERTE" : "OK";
                tableModel.addRow(new Object[]{
                    p.getNom(),
                    p.getCategorie() != null ? p.getCategorie().toString() : "",
                    p.getQuantiteStock(),
                    String.format("%,.0f F", p.getPrixVente().doubleValue()),
                    etat
                });
            }
            lblProdRecent.setText(produits.size() + " produits");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void chargerAlertesStock() {
        pnlAlertes.removeAll();
        try {
            List<Alerte> alertes = new AlerteDAO().findNonLues();
            if (alertes.isEmpty()) {
                pnlAlertes.add(creerLigneVide("✓ Aucune alerte active", C_VERT));
            } else {
                for (Alerte a : alertes) {
                    boolean critique = "CRITIQUE".equals(a.getNiveau());
                    pnlAlertes.add(creerLigneAlerte(
                        a.getMessage(),
                        critique ? "Rupture imminente" : "Stock faible",
                        critique ? C_ROUGE : C_JAUNE));
                }
            }
        } catch (Exception e) {
            pnlAlertes.add(creerLigneVide("Erreur chargement", C_ROUGE));
        }
        pnlAlertes.revalidate(); pnlAlertes.repaint();
    }

    private void chargerMouvementsRecents() {
        pnlMouvements.removeAll();
        try {
            List<EntreeStock> entrees = new EntreeStockDAO().findAll();
            List<SortieStock> sorties = new SortieStockDAO().findAll();
            int maxE = Math.min(entrees.size(), 5);
            int maxS = Math.min(sorties.size(), 3);
            for (int i=0; i<maxE; i++) {
                EntreeStock e = entrees.get(i);
                String date = e.getDateEntree()!=null ? e.getDateEntree().toLocalDate().toString() : "";
                pnlMouvements.add(creerLigneMouvement(
                    nomProduit(e.getIdProduit()), "Entrée Stock", date, "+"+e.getQuantite(), C_VERT));
            }
            for (int i=0; i<maxS; i++) {
                SortieStock s = sorties.get(i);
                String date = s.getDateSortie()!=null ? s.getDateSortie().toString() : "";
                pnlMouvements.add(creerLigneMouvement(
                    nomProduit(s.getIdProduit()), s.getMotif()!=null?s.getMotif():"Sortie",
                    date, "-"+s.getQuantite(), C_ROUGE));
            }
        } catch (Exception e) { e.printStackTrace(); }
        pnlMouvements.revalidate(); pnlMouvements.repaint();
    }

    // =========================================================================
    //  COMPOSANTS UI
    // =========================================================================
    private JLabel creerLabelGrand(String texte) {
        JLabel lbl = new JLabel(texte);
        lbl.setFont(new Font("Baskerville Old Face", Font.PLAIN, 26));
        lbl.setForeground(C_OR_LIGHT); lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel creerLabelTracked(String texte, int size) {
        JLabel lbl = new JLabel("<html><span style='letter-spacing:2px'>"+texte+"</span></html>");
        lbl.setFont(new Font("Baskerville Old Face", Font.PLAIN, size));
        lbl.setForeground(C_TEXT_DIM); lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField creerChampRecherche() {
        JTextField txt = new JTextField("Rechercher...") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_SURFACE);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),20,20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txt.setFont(new Font("Baskerville Old Face", Font.PLAIN, 11));
        txt.setForeground(C_TEXT_DIM); txt.setBackground(new Color(0,0,0,0));
        txt.setCaretColor(C_OR); txt.setBorder(new EmptyBorder(6,14,6,14));
        txt.setOpaque(false); txt.setPreferredSize(new Dimension(200, 30));
        return txt;
    }

    private JPanel creerLigneAlerte(String titre, String sous, Color couleur) {
        JPanel row = new JPanel(new BorderLayout(8,0));
        row.setBackground(C_SURFACE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,C_BORDER),
            new EmptyBorder(8,10,8,10)));
        JLabel point = new JLabel("●");
        point.setFont(new Font("Dialog",Font.PLAIN,10));
        point.setForeground(couleur); point.setPreferredSize(new Dimension(16,16));
        row.add(point, BorderLayout.WEST);
        JPanel tx = new JPanel(new GridLayout(2,1,0,1)); tx.setOpaque(false);
        JLabel t1=new JLabel(titre); t1.setFont(new Font("Baskerville Old Face",Font.BOLD,12)); t1.setForeground(C_TEXT);
        JLabel t2=new JLabel(sous);  t2.setFont(new Font("Baskerville Old Face",Font.PLAIN,10)); t2.setForeground(couleur);
        tx.add(t1); tx.add(t2); row.add(tx,BorderLayout.CENTER);
        return row;
    }

    private JPanel creerLigneMouvement(String titre,String motif,String date,String qte,Color couleur){
        JPanel row = new JPanel(new BorderLayout(8,0));
        row.setBackground(C_SURFACE); row.setMaximumSize(new Dimension(Integer.MAX_VALUE,52));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,C_BORDER), new EmptyBorder(7,10,7,10)));
        JLabel ico = new JLabel(couleur==C_VERT?"↓":"↑");
        ico.setFont(new Font("Segoe UI Emoji",Font.BOLD,14)); ico.setForeground(couleur);
        ico.setPreferredSize(new Dimension(22,22)); ico.setHorizontalAlignment(SwingConstants.CENTER);
        row.add(ico,BorderLayout.WEST);
        JPanel info=new JPanel(new GridLayout(2,1,0,1)); info.setOpaque(false);
        JLabel t1=new JLabel(titre); t1.setFont(new Font("Baskerville Old Face",Font.PLAIN,11)); t1.setForeground(C_TEXT);
        JLabel t2=new JLabel(motif+"  •  "+date); t2.setFont(new Font("Baskerville Old Face",Font.PLAIN,9)); t2.setForeground(C_TEXT_DIM);
        info.add(t1); info.add(t2); row.add(info,BorderLayout.CENTER);
        JLabel lQ=new JLabel(qte); lQ.setFont(new Font("Baskerville Old Face",Font.BOLD,13)); lQ.setForeground(couleur);
        row.add(lQ,BorderLayout.EAST);
        return row;
    }

    private JPanel creerLigneVide(String msg, Color couleur) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setBackground(C_SURFACE);
        JLabel lbl=new JLabel(msg); lbl.setFont(new Font("Baskerville Old Face",Font.ITALIC,11)); lbl.setForeground(couleur);
        row.add(lbl); return row;
    }

    private void styleTableau(JTable table) {
        table.setBackground(C_SURFACE); table.setForeground(C_TEXT);
        table.setFont(new Font("Baskerville Old Face",Font.PLAIN,12));
        table.setRowHeight(36); table.setGridColor(C_BORDER);
        table.setSelectionBackground(new Color(212,175,105,50));
        table.setSelectionForeground(C_OR_LIGHT);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0,1));
        table.setFocusable(false);
        JTableHeader header = table.getTableHeader();
        header.setBackground(C_SURFACE2); header.setForeground(C_TEXT_DIM);
        header.setFont(new Font("Baskerville Old Face",Font.PLAIN,10));
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0,C_OR));
        header.setPreferredSize(new Dimension(0,32));
        DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
        cr.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(cr);
        table.getColumnModel().getColumn(3).setCellRenderer(cr);
    }

    // =========================================================================
    //  RENDERERS
    // =========================================================================
    private class StockBarRenderer extends JPanel implements TableCellRenderer {
        private int stock=0; private Color barColor=C_VERT;
        StockBarRenderer(){setOpaque(true);}
        @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
            stock=v!=null?Integer.parseInt(v.toString()):0;
            barColor=stock<=5?C_ROUGE:stock<=10?C_JAUNE:C_VERT;
            setBackground(sel?new Color(212,175,105,50):(row%2==0?C_SURFACE:C_SURFACE2));
            return this;
        }
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth()-20,h=8,y=(getHeight()-h)/2;
            g2.setColor(C_BORDER); g2.fill(new RoundRectangle2D.Float(10,y,w,h,h,h));
            int bW=(int)(w*Math.min(stock,30)/30.0);
            if(bW>0){g2.setColor(barColor);g2.fill(new RoundRectangle2D.Float(10,y,bW,h,h,h));}
            g2.setColor(C_TEXT); g2.setFont(new Font("Baskerville Old Face",Font.PLAIN,10));
            FontMetrics fm=g2.getFontMetrics(); String s=String.valueOf(stock);
            g2.drawString(s,10+bW+4,y+h/2+fm.getAscent()/2-1);
            g2.dispose();
        }
    }

    private class EtatRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int row,int col){
            JLabel lbl=(JLabel)super.getTableCellRendererComponent(t,v,sel,foc,row,col);
            String val=v!=null?v.toString():"";
            if("ALERTE".equals(val)){lbl.setForeground(C_ROUGE);lbl.setText("● ALERTE");}
            else{lbl.setForeground(C_VERT);lbl.setText("● OK");}
            lbl.setFont(new Font("Baskerville Old Face",Font.PLAIN,11));
            lbl.setBackground(sel?new Color(212,175,105,50):(row%2==0?C_SURFACE:C_SURFACE2));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            return lbl;
        }
    }
}
