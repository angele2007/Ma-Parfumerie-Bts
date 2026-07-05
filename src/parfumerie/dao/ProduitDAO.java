package parfumerie.dao;

import parfumerie.modele.Produit;
import parfumerie.modele.Produit.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    private final Connection conn;

    public ProduitDAO() throws SQLException {
        this.conn = DBConnection.getInstance();
    }

    // =========================================================================
    //  MAPPER UNIQUE — utilisé par toutes les méthodes
    // =========================================================================
    private Produit map(ResultSet rs) throws SQLException {
        Produit p = new Produit();
        p.setIdProduit(rs.getInt("id_produit"));
        p.setNom(rs.getString("nom"));
        p.setMarque(rs.getString("marque"));

        String cat = rs.getString("categorie");
        if (cat != null) {
            try { p.setCategorie(Categorie.valueOf(cat)); }
            catch (IllegalArgumentException ignored) {}
        }

        // contenance_ml est nullable en base
        Object cml = rs.getObject("contenance_ml");
        p.setContenanceMl(cml != null ? ((Number) cml).intValue() : null);

        p.setPrixAchat(rs.getBigDecimal("prix_achat"));
        p.setPrixVente(rs.getBigDecimal("prix_vente"));
        p.setQuantiteStock(rs.getInt("quantite_stock"));
        p.setSeuilAlerte(rs.getInt("seuil_alerte"));
        return p;
    }

    // =========================================================================
    //  CRUD DE BASE
    // =========================================================================

    public List<Produit> findAll() throws SQLException {
        List<Produit> list = new ArrayList<>();
        String sql = "SELECT * FROM produit ORDER BY nom";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Produit findById(int id) throws SQLException {
        String sql = "SELECT * FROM produit WHERE id_produit = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Produit> findByCategorie(Categorie categorie) throws SQLException {
        List<Produit> list = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE categorie = ? ORDER BY nom";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categorie.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    /** Produits dont le stock est inférieur ou égal au seuil d'alerte. */
    public List<Produit> findSousSeuilAlerte() throws SQLException {
        List<Produit> list = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE quantite_stock <= seuil_alerte ORDER BY quantite_stock";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public int insert(Produit p) throws SQLException {
        String sql = "INSERT INTO produit "
                + "(nom, marque, categorie, contenance_ml, prix_achat, prix_vente, quantite_stock, seuil_alerte) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getMarque());
            ps.setString(3, p.getCategorie() != null ? p.getCategorie().name() : null);
            ps.setObject(4, p.getContenanceMl());
            ps.setBigDecimal(5, p.getPrixAchat());
            ps.setBigDecimal(6, p.getPrixVente());
            ps.setInt(7, p.getQuantiteStock());
            ps.setInt(8, p.getSeuilAlerte());
            ps.executeUpdate();
            try (ResultSet rk = ps.getGeneratedKeys()) {
                return rk.next() ? rk.getInt(1) : -1;
            }
        }
    }

    public boolean update(Produit p) throws SQLException {
        String sql = "UPDATE produit SET nom=?, marque=?, categorie=?, contenance_ml=?, "
                + "prix_achat=?, prix_vente=?, quantite_stock=?, seuil_alerte=? "
                + "WHERE id_produit=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getMarque());
            ps.setString(3, p.getCategorie() != null ? p.getCategorie().name() : null);
            ps.setObject(4, p.getContenanceMl());
            ps.setBigDecimal(5, p.getPrixAchat());
            ps.setBigDecimal(6, p.getPrixVente());
            ps.setInt(7, p.getQuantiteStock());
            ps.setInt(8, p.getSeuilAlerte());
            ps.setInt(9, p.getIdProduit());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM produit WHERE id_produit = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    //  MÉTHODES TABLEAU DE BORD
    // =========================================================================

    /** Compte le nombre total de produits. */
    public int countProduits() throws SQLException {
        String sql = "SELECT COUNT(*) FROM produit";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Compte les produits dont le stock est <= seuil_alerte. */
    public int countStockAlerte() throws SQLException {
        String sql = "SELECT COUNT(*) FROM produit WHERE quantite_stock <= seuil_alerte";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Retourne les N produits les plus récents (par id décroissant). */
    public List<Produit> findRecentProduits(int limit) throws SQLException {
        List<Produit> liste = new ArrayList<>();
        String sql = "SELECT * FROM produit ORDER BY id_produit DESC LIMIT ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) liste.add(map(rs));   // CORRECTION : map() et non mapper()
            }
        }
        return liste;
    }
}