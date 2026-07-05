package parfumerie.dao;

import parfumerie.modeles.EntreeStock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntreeStockDAO {

    private Connection conn;

    public EntreeStockDAO() throws SQLException {
        this.conn = DBConnection.getInstance();
    }

    private EntreeStock map(ResultSet rs) throws SQLException {
        EntreeStock e = new EntreeStock();
        e.setIdEntree(rs.getInt("id_entree"));
        e.setIdProduit(rs.getInt("id_produit"));
        e.setIdFournisseur(rs.getInt("id_fournisseur"));
        e.setQuantite(rs.getInt("quantite"));
        Timestamp ts = rs.getTimestamp("date_entree");
        if (ts != null) e.setDateEntree(ts.toLocalDateTime());
        e.setPrixUnitaire(rs.getBigDecimal("prix_unitaire"));
        return e;
    }

    public List<EntreeStock> findAll() throws SQLException {
        List<EntreeStock> list = new ArrayList<>();
        String sql = "SELECT * FROM entree_stock ORDER BY date_entree DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<EntreeStock> findByProduit(int idProduit) throws SQLException {
        List<EntreeStock> list = new ArrayList<>();
        String sql = "SELECT * FROM entree_stock WHERE id_produit = ? ORDER BY date_entree DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProduit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<EntreeStock> findByFournisseur(int idFournisseur) throws SQLException {
        List<EntreeStock> list = new ArrayList<>();
        String sql = "SELECT * FROM entree_stock WHERE id_fournisseur = ? ORDER BY date_entree DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idFournisseur);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public int insert(EntreeStock e) throws SQLException {
        String sql = "INSERT INTO entree_stock (id_produit, id_fournisseur, quantite, prix_unitaire) "
                   + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getIdProduit());
            ps.setInt(2, e.getIdFournisseur());
            ps.setInt(3, e.getQuantite());
            ps.setBigDecimal(4, e.getPrixUnitaire());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }
    // ============================================================
//  À AJOUTER dans parfumerie/dao/EntreeStockDAO.java
//  Collez ces deux méthodes AVANT la dernière accolade }
// ============================================================

    /**
     * Supprime une entrée de stock par son id.
     * Utilisé par panelEntrerStock pour la suppression et la modification
     * (delete + insert, car il n'y a pas d'UPDATE dans ce DAO).
     */
    public boolean delete(int idEntree) throws SQLException {
        String sql = "DELETE FROM entree_stock WHERE id_entree = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEntree);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Met à jour une entrée de stock existante.
     * Optionnel : si vous préférez un vrai UPDATE plutôt que delete+insert.
     */
    public boolean update(EntreeStock e) throws SQLException {
        String sql = "UPDATE entree_stock "
                   + "SET id_produit=?, id_fournisseur=?, quantite=?, prix_unitaire=?, date_entree=? "
                   + "WHERE id_entree=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getIdProduit());
            ps.setInt(2, e.getIdFournisseur());
            ps.setInt(3, e.getQuantite());
            ps.setBigDecimal(4, e.getPrixUnitaire());
            ps.setTimestamp(5, e.getDateEntree() != null
                ? java.sql.Timestamp.valueOf(e.getDateEntree()) : null);
            ps.setInt(6, e.getIdEntree());
            return ps.executeUpdate() > 0;
        }
    }
}