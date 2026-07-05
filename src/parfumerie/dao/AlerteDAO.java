package parfumerie.dao;

import parfumerie.modeles.Alerte;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des alertes de stock.
 *
 * Cette classe génère les alertes dynamiquement à partir de la table `produit`
 * (produits dont quantite_stock <= seuil_alerte) sans avoir besoin d'une table
 * dédiée en base — sauf si vous en avez créé une.
 */
public class AlerteDAO {

    private final Connection conn;

    public AlerteDAO() throws SQLException {
        this.conn = DBConnection.getInstance();
    }

    /**
     * Retourne les alertes non lues = produits dont le stock ≤ seuil_alerte.
     * Le "niveau" est "CRITIQUE" si stock == 0, sinon "AVERTISSEMENT".
     */
    public List<Alerte> findNonLues() throws SQLException {
        List<Alerte> liste = new ArrayList<>();
        String sql = "SELECT id_produit, nom, quantite_stock, seuil_alerte "
                   + "FROM produit "
                   + "WHERE quantite_stock <= seuil_alerte "
                   + "ORDER BY quantite_stock ASC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            int fakeId = 1;
            while (rs.next()) {
                int    idProduit = rs.getInt   ("id_produit");
                String nom       = rs.getString("nom");
                int    stock     = rs.getInt   ("quantite_stock");
                int    seuil     = rs.getInt   ("seuil_alerte");

                String niveau  = (stock == 0) ? "CRITIQUE" : "AVERTISSEMENT";
                String message = nom + " — stock : " + stock + " / seuil : " + seuil;

                Alerte a = new Alerte();
                a.setIdAlerte (fakeId++);
                a.setIdProduit(idProduit);
                a.setMessage  (message);
                a.setNiveau   (niveau);
                a.setDateAlerte(java.time.LocalDateTime.now());
                a.setLu       (false);

                liste.add(a);
            }
        }
        return liste;
    }

    /** Compte le nombre d'alertes actives. */
    public int countNonLues() throws SQLException {
        String sql = "SELECT COUNT(*) FROM produit WHERE quantite_stock <= seuil_alerte";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}