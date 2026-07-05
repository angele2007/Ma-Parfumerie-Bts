package parfumerie.dao;

import parfumerie.modeles.Fournisseur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDAO {

    private Connection conn;

    public FournisseurDAO() throws SQLException {
        this.conn = DBConnection.getInstance();
    }

    private Fournisseur map(ResultSet rs) throws SQLException {
        return new Fournisseur(
            rs.getInt("id_fournisseur"),
            rs.getString("nom"),
            rs.getString("telephone"),
            rs.getString("email"),
            rs.getString("adresse")
        );
    }

    public List<Fournisseur> findAll() throws SQLException {
        List<Fournisseur> list = new ArrayList<>();
        String sql = "SELECT * FROM fournisseur ORDER BY nom";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
    
    // =========================================================================
//  AJOUTE CETTE MÉTHODE dans FournisseurDAO.java
//  (après findAll() par exemple)
// =========================================================================

    public Fournisseur findById(int id) throws java.sql.SQLException {
        String sql = "SELECT * FROM fournisseur WHERE id_fournisseur = ?";
        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Fournisseur f = new Fournisseur();
                    f.setIdFournisseur(rs.getInt("id_fournisseur"));
                    f.setNom(rs.getString("nom"));
                    f.setTelephone(rs.getString("telephone"));
                    f.setEmail(rs.getString("email"));
                    f.setAdresse(rs.getString("adresse"));
                    return f;
                }
            }
        }
        return null;
    }

    

    public int insert(Fournisseur f) throws SQLException {
        String sql = "INSERT INTO fournisseur (nom, telephone, email, adresse) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getNom());
            ps.setString(2, f.getTelephone());
            ps.setString(3, f.getEmail());
            ps.setString(4, f.getAdresse());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public boolean update(Fournisseur f) throws SQLException {
        String sql = "UPDATE fournisseur SET nom=?, telephone=?, email=?, adresse=? WHERE id_fournisseur=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getNom());
            ps.setString(2, f.getTelephone());
            ps.setString(3, f.getEmail());
            ps.setString(4, f.getAdresse());
            ps.setInt(5, f.getIdFournisseur());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM fournisseur WHERE id_fournisseur = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}