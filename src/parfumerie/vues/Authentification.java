/*
 * Authentification.java — Login activé admin/1234
 */
package parfumerie.vues;

import java.awt.Color;
import javax.swing.JOptionPane;
import parfumerie.controles.ProprieteDisigne;

/**
 * @author Angele
 */
public class Authentification extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(Authentification.class.getName());

    // ── Identifiants codés en dur ──────────────────
    private static final String LOGIN_VALIDE    = "admin";
    private static final String PASSWORD_VALIDE = "1234";

    public Authentification() {
        initComponents();
        setLocationRelativeTo(null);
        disigne();
        // Entrée = se connecter
        txtidentifiant.addActionListener(e -> txtpassword.requestFocus());
        txtpassword.addActionListener(e -> seConnecter());
    }

    private void disigne() {
        ProprieteDisigne.arrondirPanel(jPanel4, new Color(35, 30, 45), 20);
        ProprieteDisigne.arrondirPanel(jPanel6, new Color(35, 30, 45), 20);
        setPlaceholders();
    }

    // ── Placeholders ──────────────────────────────
    private void setPlaceholders() {
        txtidentifiant.setText("Entrez l'identifiant..");
        txtidentifiant.setForeground(new Color(153, 153, 153));
        txtpassword.setText("Mot de passe");
        txtpassword.setForeground(new Color(153, 153, 153));
        txtpassword.setEchoChar((char) 0); // Afficher le placeholder en clair
    }

    // ── Connexion ─────────────────────────────────
    private void seConnecter() {
        String login    = txtidentifiant.getText().trim();
        String password = new String(txtpassword.getPassword()).trim();

        // Ignorer les placeholders
        if (login.equals("Entrez l'identifiant..")) login = "";
        if (password.equals("Mot de passe"))        password = "";

        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez remplir tous les champs !",
                "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (LOGIN_VALIDE.equals(login) && PASSWORD_VALIDE.equals(password)) {
            // ✓ Connexion réussie
            new Acceuil().setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Identifiant ou mot de passe incorrect !",
                "Échec de connexion", JOptionPane.ERROR_MESSAGE);
            txtpassword.setText("");
            txtpassword.setEchoChar('•');
            txtpassword.requestFocus();
        }
    }

    // =========================================================================
    //  INIT COMPONENTS
    // =========================================================================
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1        = new javax.swing.JPanel();
        jPanel2        = new javax.swing.JPanel();
        jLabel1        = new javax.swing.JLabel();
        jLabel2        = new javax.swing.JLabel();
        jLabel3        = new javax.swing.JLabel();
        jPanel3        = new javax.swing.JPanel();
        jLabel4        = new javax.swing.JLabel();
        jPanel4        = new javax.swing.JPanel();
        txtidentifiant = new javax.swing.JTextField();
        jPanel5        = new javax.swing.JPanel();
        jLabel5        = new javax.swing.JLabel();
        jPanel6        = new javax.swing.JPanel();
        txtpassword    = new javax.swing.JPasswordField();
        btnseconnecter = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Authentification");
        setMaximumSize(new java.awt.Dimension(400, 300));
        setMinimumSize(new java.awt.Dimension(400, 300));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(26, 21, 35));

        jPanel2.setOpaque(false);

        jLabel1.setIcon(new javax.swing.ImageIcon(
            getClass().getResource("/parfumerie/image/flower-line.png")));

        jLabel2.setBackground(new java.awt.Color(215, 185, 105));
        jLabel2.setFont(new java.awt.Font("PMingLiU-ExtB", 1, 24));
        jLabel2.setForeground(new java.awt.Color(215, 185, 105));
        jLabel2.setText("MA PARFUMERIE");

        jLabel3.setForeground(new java.awt.Color(105, 115, 165));
        jLabel3.setText("Systeme de gestion de stock");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(65,65,65)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(152,152,152)
                        .addComponent(jLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22,22,22)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
        );

        // ── Identifiant ────────────────────────────
        jPanel3.setOpaque(false);
        jLabel4.setForeground(new java.awt.Color(105, 115, 165));
        jLabel4.setText("IDENTIFIANT");

        jPanel4.setBackground(new java.awt.Color(35, 30, 45));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));

        txtidentifiant.setBackground(new java.awt.Color(35, 30, 45));
        txtidentifiant.setForeground(new java.awt.Color(255, 255, 255));
        txtidentifiant.setText("Entrez l'identifiant..");
        txtidentifiant.setBorder(null);
        txtidentifiant.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtidentifiant.getText().equals("Entrez l'identifiant..")) {
                    txtidentifiant.setText("");
                    txtidentifiant.setForeground(new Color(255, 255, 255));
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtidentifiant.getText().trim().isEmpty()) {
                    txtidentifiant.setText("Entrez l'identifiant..");
                    txtidentifiant.setForeground(new Color(153, 153, 153));
                }
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtidentifiant, javax.swing.GroupLayout.PREFERRED_SIZE, 307,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtidentifiant, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 92,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0,0,Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        // ── Mot de passe ───────────────────────────
        jPanel5.setOpaque(false);
        jLabel5.setForeground(new java.awt.Color(105, 115, 165));
        jLabel5.setText("MOT DE PASSE");

        jPanel6.setBackground(new java.awt.Color(35, 30, 45));
        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1,1,1,1));

        txtpassword.setBackground(new java.awt.Color(35, 30, 45));
        txtpassword.setForeground(new java.awt.Color(255, 255, 255));
        txtpassword.setText("Mot de passe");
        txtpassword.setBorder(null);
        txtpassword.setEchoChar((char) 0); // placeholder visible
        txtpassword.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (new String(txtpassword.getPassword()).equals("Mot de passe")) {
                    txtpassword.setText("");
                    txtpassword.setEchoChar('•'); // masquer la saisie
                    txtpassword.setForeground(new Color(255, 255, 255));
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (new String(txtpassword.getPassword()).trim().isEmpty()) {
                    txtpassword.setText("Mot de passe");
                    txtpassword.setEchoChar((char) 0); // réafficher placeholder
                    txtpassword.setForeground(new Color(153, 153, 153));
                }
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtpassword)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtpassword, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 92,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0,0,Short.MAX_VALUE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        // ── Bouton SE CONNECTER ────────────────────
        btnseconnecter.setBackground(new java.awt.Color(210, 180, 85));
        btnseconnecter.setForeground(new java.awt.Color(26, 21, 35));
        btnseconnecter.setFont(new java.awt.Font("Baskerville Old Face", java.awt.Font.BOLD, 14));
        btnseconnecter.setText("SE CONNECTER");
        btnseconnecter.setBorder(null);
        btnseconnecter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnseconnecter.setFocusPainted(false);
        btnseconnecter.addActionListener(e -> seConnecter());

        // ── Layout global ─────────────────────────
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnseconnecter, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18,18,18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26,26,26)
                .addComponent(btnseconnecter, javax.swing.GroupLayout.PREFERRED_SIZE, 42,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 90, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
        pack();
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
        java.awt.EventQueue.invokeLater(() -> new Authentification().setVisible(true));
    }

    // ── Variables declaration ──────────────────────
    private javax.swing.JButton        btnseconnecter;
    private javax.swing.JLabel         jLabel1, jLabel2, jLabel3, jLabel4, jLabel5;
    private javax.swing.JPanel         jPanel1, jPanel2, jPanel3, jPanel4, jPanel5, jPanel6;
    private javax.swing.JTextField     txtidentifiant;
    private javax.swing.JPasswordField txtpassword;
}
