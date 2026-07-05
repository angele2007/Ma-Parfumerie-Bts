/*
 * Acceuil.java — Sidebar améliorée thème sombre luxe
 */
package parfumerie.vues;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.BorderFactory;
import parfumerie.controles.ProprieteDisigne;

/**
 * @author Angele
 */
public class Acceuil extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(Acceuil.class.getName());

    private final TableauBord tableau = new TableauBord();

    // Bouton actuellement actif
    private JButton btnActif = null;

    // ── Couleurs sidebar ──────────────────────────
    private static final Color SIDEBAR_BG     = new Color(15, 13, 11);
    private static final Color SIDEBAR_HEADER = new Color(18, 16, 14);
    private static final Color GOLD           = new Color(212, 175, 55);
    private static final Color GOLD_LIGHT     = new Color(240, 215, 140);
    private static final Color BTN_NORMAL_FG  = new Color(160, 148, 120);
    private static final Color BTN_ACTIVE_BG  = new Color(36, 30, 20);
    private static final Color BTN_HOVER_BG   = new Color(28, 24, 16);
    private static final Color BORDER_COLOR   = new Color(45, 38, 26);
    private static final Color TEXT_SUB       = new Color(100, 90, 70);

    // ── Icônes texte par item ─────────────────────
    private static final String[][] NAV_ITEMS = {
        {"📊", "Tableau de bord"},
        {"🧴", "Produits"},
        {"📦", "Entrée stock"},
        {"🛒", "Sortie stock"},
        {"🔔", "Alertes"},
        {"🧾", "Factures"},
        {"🏢", "Fournisseurs"},
        {"🖨", "Impression"},
    };

    public Acceuil() {
        initComponents();
        setLocationRelativeTo(null);
        disigne();
    }

    private void disigne() {
        // Bouton avatar arrondi
        ProprieteDisigne.boutonArrondi(jButton8,
            new Color(212, 175, 55), new Color(212, 175, 55), new Color(18, 16, 14), 50);

        // Alignement gauche de tous les boutons
        JButton[] btns = {btnTB, btnProd, btnEntrerStock,
                          btnSortieStock, btnAlerte, btnFacture, btnFournisseur, btnImpression};
        for (JButton b : btns) b.setHorizontalAlignment(SwingConstants.LEFT);

        // Actions
        btnTB         .addActionListener(e -> { chargerPage(Page.Tableau);     setActif(btnTB); tableau.miseAJour(); });
        btnProd       .addActionListener(e -> { chargerPage(Page.Produit);     setActif(btnProd); });
        btnEntrerStock.addActionListener(e -> { chargerPage(Page.EntrerStock); setActif(btnEntrerStock); });
        btnSortieStock.addActionListener(e -> { chargerPage(Page.SortieStock); setActif(btnSortieStock); });
        btnAlerte     .addActionListener(e -> { chargerPage(Page.Alerte);      setActif(btnAlerte); });
        btnFacture    .addActionListener(e -> { chargerPage(Page.Facture);     setActif(btnFacture); });
        btnFournisseur.addActionListener(e -> { chargerPage(Page.Fournisseur); setActif(btnFournisseur); });
        btnImpression .addActionListener(e -> { chargerPage(Page.Impression);  setActif(btnImpression); });

        // Hover effect
        for (JButton b : btns) ajouterHover(b);

        // Page de démarrage
        chargerPage(Page.Tableau);
        setActif(btnTB);
    }

    // ── Marque le bouton actif ────────────────────
    private void setActif(JButton btn) {
        JButton[] btns = {btnTB, btnProd, btnEntrerStock,
                          btnSortieStock, btnAlerte, btnFacture, btnFournisseur, btnImpression};
        for (JButton b : btns) {
            b.setBackground(SIDEBAR_BG);
            b.setForeground(BTN_NORMAL_FG);
            b.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
            b.setOpaque(true);
        }
        btn.setBackground(BTN_ACTIVE_BG);
        btn.setForeground(GOLD);
        // Barre gauche dorée via bordure
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, GOLD),
            BorderFactory.createEmptyBorder(0, 13, 0, 0)
        ));
        btnActif = btn;
    }

    // ── Effet hover ───────────────────────────────
    private void ajouterHover(JButton btn) {
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn != btnActif) {
                    btn.setBackground(BTN_HOVER_BG);
                    btn.setForeground(GOLD_LIGHT);
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn != btnActif) {
                    btn.setBackground(SIDEBAR_BG);
                    btn.setForeground(BTN_NORMAL_FG);
                }
            }
        });
    }

    // ── Enum pages ────────────────────────────────
    public enum Page {
        Tableau, Alerte, EntrerStock, SortieStock, Facture, Fournisseur, Produit, Impression
    }

    public void chargerPage(Page page) {
        AffichePanel.removeAll();
        JPanel contenu;
        switch (page) {
            case Tableau     -> contenu = tableau;
            case Alerte      -> contenu = new panelAlerte();
            case EntrerStock -> contenu = new panelEntrerStock();
            case SortieStock -> contenu = new panelSortieStock();
            case Facture     -> contenu = new panelFacture();
            case Fournisseur -> contenu = new panelFournisseur();
            case Produit     -> contenu = new panelproduit();
            case Impression  -> contenu = new panelImpression();
            default          -> contenu = tableau;
        }
        AffichePanel.setLayout(new BorderLayout());
        AffichePanel.add(contenu, BorderLayout.CENTER);
        AffichePanel.revalidate();
        AffichePanel.repaint();
    }

    // =========================================================================
    //  INIT COMPONENTS
    // =========================================================================
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1        = new JPanel();
        jPanel2        = new JPanel();
        jPanel4        = new JPanel();
        jLabel2        = new JLabel();
        jLabel3        = new JLabel();
        jPanel5        = new JPanel();
        jButton8       = new JButton();
        jPanel7        = new JPanel();
        jLabel1        = new JLabel();
        jLabel4        = new JLabel();
        jPanel6        = new JPanel();
        btnTB          = new JButton();
        btnImpression  = new JButton();
        btnProd        = new JButton();
        btnEntrerStock = new JButton();
        btnSortieStock = new JButton();
        btnAlerte      = new JButton();
        btnFacture     = new JButton();
        btnFournisseur = new JButton();
        AffichePanel   = new JPanel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("MA PARFUMERIE");
        setMinimumSize(new Dimension(900, 600));
        getContentPane().setLayout(new BorderLayout());

        // ── Layout principal ──────────────────────
        jPanel1.setLayout(new BorderLayout());
        jPanel1.setBackground(new Color(18, 16, 14));

        // ── SIDEBAR ───────────────────────────────
        jPanel2.setBackground(SIDEBAR_BG);
        jPanel2.setMinimumSize(new Dimension(220, 100));
        jPanel2.setPreferredSize(new Dimension(220, 600));
        jPanel2.setLayout(new BorderLayout());

        // ── Header sidebar : logo ──────────────────
        jPanel4.setBackground(SIDEBAR_HEADER);
        jPanel4.setPreferredSize(new Dimension(220, 82));
        jPanel4.setLayout(new BorderLayout());
        jPanel4.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JPanel pnlLogo = new JPanel();
        pnlLogo.setBackground(SIDEBAR_HEADER);
        pnlLogo.setLayout(new BoxLayout(pnlLogo, BoxLayout.Y_AXIS));
        pnlLogo.setBorder(BorderFactory.createEmptyBorder(18, 16, 14, 16));

        jLabel2.setFont(new Font("PMingLiU-ExtB", Font.BOLD, 20));
        jLabel2.setForeground(GOLD);
        jLabel2.setText("MA PARFUMERIE");
        jLabel2.setAlignmentX(Component.LEFT_ALIGNMENT);

        jLabel3.setFont(new Font("Baskerville Old Face", Font.PLAIN, 11));
        jLabel3.setForeground(TEXT_SUB);
        jLabel3.setText("Gestion de parfumerie");
        jLabel3.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlLogo.add(jLabel2);
        pnlLogo.add(Box.createVerticalStrut(4));
        pnlLogo.add(jLabel3);
        jPanel4.add(pnlLogo, BorderLayout.CENTER);

        // Ligne dorée en bas du logo
        JPanel ligne = new JPanel();
        ligne.setBackground(GOLD);
        ligne.setPreferredSize(new Dimension(220, 2));
        jPanel4.add(ligne, BorderLayout.SOUTH);

        jPanel2.add(jPanel4, BorderLayout.NORTH);

        // ── Nav buttons ───────────────────────────
        jPanel6.setBackground(SIDEBAR_BG);
        jPanel6.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        jPanel6.setLayout(new BoxLayout(jPanel6, BoxLayout.Y_AXIS));

        // Construire les boutons avec icône + label
        Object[][] btnDefs = {
            {btnTB,          NAV_ITEMS[0][0], NAV_ITEMS[0][1]},
            {btnProd,        NAV_ITEMS[1][0], NAV_ITEMS[1][1]},
            {btnEntrerStock, NAV_ITEMS[2][0], NAV_ITEMS[2][1]},
            {btnSortieStock, NAV_ITEMS[3][0], NAV_ITEMS[3][1]},
            {btnAlerte,      NAV_ITEMS[4][0], NAV_ITEMS[4][1]},
            {btnFacture,     NAV_ITEMS[5][0], NAV_ITEMS[5][1]},
            {btnFournisseur, NAV_ITEMS[6][0], NAV_ITEMS[6][1]},
            {btnImpression,  NAV_ITEMS[7][0], NAV_ITEMS[7][1]},
        };

        for (Object[] def : btnDefs) {
            JButton btn = (JButton) def[0];
            String  ico = (String)  def[1];
            String  lbl = (String)  def[2];

            btn.setText(ico + "   " + lbl);
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            btn.setBackground(SIDEBAR_BG);
            btn.setForeground(BTN_NORMAL_FG);
            btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(true);
            btn.setOpaque(true);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setMaximumSize(new Dimension(220, 46));
            btn.setPreferredSize(new Dimension(220, 46));
            btn.setMinimumSize(new Dimension(220, 46));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setIconTextGap(0);

            jPanel6.add(btn);

            // Séparateur très fin
            JPanel sep = new JPanel();
            sep.setBackground(BORDER_COLOR);
            sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            sep.setPreferredSize(new Dimension(220, 1));
            jPanel6.add(sep);
        }

        jPanel2.add(jPanel6, BorderLayout.CENTER);

        // ── Footer sidebar : profil ────────────────
        jPanel5.setBackground(new Color(12, 10, 8));
        jPanel5.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        jPanel5.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        jPanel5.setPreferredSize(new Dimension(220, 58));

        // Avatar cercle doré
        jButton8.setBackground(GOLD);
        jButton8.setForeground(new Color(18, 16, 14));
        jButton8.setFont(new Font("Baskerville Old Face", Font.BOLD, 15));
        jButton8.setText("A");
        jButton8.setPreferredSize(new Dimension(36, 36));
        jButton8.setBorderPainted(false);
        jButton8.setFocusPainted(false);
        jPanel5.add(jButton8);

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new BoxLayout(jPanel7, BoxLayout.Y_AXIS));
        jPanel7.setPreferredSize(new Dimension(140, 36));

        jLabel1.setFont(new Font("Baskerville Old Face", Font.BOLD, 13));
        jLabel1.setForeground(new Color(240, 230, 200));
        jLabel1.setText("Admin");

        jLabel4.setFont(new Font("Baskerville Old Face", Font.PLAIN, 11));
        jLabel4.setForeground(TEXT_SUB);
        jLabel4.setText("Administrateur");

        jPanel7.add(jLabel1);
        jPanel7.add(Box.createVerticalStrut(2));
        jPanel7.add(jLabel4);
        jPanel5.add(jPanel7);

        jPanel2.add(jPanel5, BorderLayout.SOUTH);

        jPanel1.add(jPanel2, BorderLayout.WEST);

        // ── Zone contenu ──────────────────────────
        AffichePanel.setBackground(new Color(18, 16, 14));
        AffichePanel.setLayout(new BorderLayout());
        jPanel1.add(AffichePanel, BorderLayout.CENTER);

        getContentPane().add(jPanel1);
        pack();
        setSize(1200, 750);
    }

    private void btnProdActionPerformed(java.awt.event.ActionEvent evt) {
        chargerPage(Page.Produit);
        setActif(btnProd);
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info :
                    javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException |
                 javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new Acceuil().setVisible(true));
    }

    // ── Variables declaration ──────────────────────
    private JPanel  AffichePanel;
    private JButton btnAlerte, btnEntrerStock, btnFacture, btnImpression;
    private JButton btnFournisseur, btnProd, btnSortieStock, btnTB;
    private JButton jButton8;
    private JLabel  jLabel1, jLabel2, jLabel3, jLabel4;
    private JPanel  jPanel1, jPanel2, jPanel4, jPanel5, jPanel6, jPanel7;
}
