package parfumerie.dao;

import parfumerie.modeles.LigneFacture;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LigneFactureDAO {

    private Connection conn;

    public LigneFactureDAO() throws SQLException {
        this.conn = DBConnection.getInstance();
    }

    private LigneFacture map(ResultSet rs) throws SQLException {
        LigneFacture l = new LigneFacture();
        l.setIdLigne(rs.getInt("id_ligne"));
        l.setIdFacture(rs.getInt("id_facture"));
        l.setIdProduit(rs.getInt("id_produit"));
        l.setQuantite(rs.getInt("quantite"));
        l.setPrixUnitaire(rs.getBigDecimal("prix_unitaire"));
        l.setSousTotal(rs.getBigDecimal("sous_total"));
        return l;
    }

    public List<LigneFacture> findByFacture(int idFacture) throws SQLException {
        List<LigneFacture> list = new ArrayList<>();
        String sql = "SELECT * FROM ligne_facture WHERE id_facture = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idFacture);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public int insert(LigneFacture l) throws SQLException {
        String sql = "INSERT INTO ligne_facture (id_facture, id_produit, quantite, prix_unitaire) "
                   + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, l.getIdFacture());
            ps.setInt(2, l.getIdProduit());
            ps.setInt(3, l.getQuantite());
            ps.setBigDecimal(4, l.getPrixUnitaire());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public boolean delete(int idLigne) throws SQLException {
        String sql = "DELETE FROM ligne_facture WHERE id_ligne = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLigne);
            return ps.executeUpdate() > 0;
        }
    }

   
    
    public void deleteByFacture(int idFacture) throws SQLException {
    String sql = "DELETE FROM ligne_facture WHERE id_facture = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, idFacture);
        ps.executeUpdate();
        }
    }
}