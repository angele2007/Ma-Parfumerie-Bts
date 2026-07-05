package parfumerie.dao;

import parfumerie.modeles.SortieStock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la table `sortie_stock`.
 */
public class SortieStockDAO {

    private final Connection conn;

    public SortieStockDAO() throws SQLException {
       this.conn = DBConnection.getInstance();
    }

    // ─── SELECT ALL ──────────────────────────────────────────────────────────

    public List<SortieStock> findAll() throws SQLException {
        List<SortieStock> liste = new ArrayList<>();
        String sql = "SELECT id_sortie, id_produit, quantite, date_sortie, motif, prix_vente "
                   + "FROM sortie_stock ORDER BY date_sortie DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    // ─── SELECT BY ID ────────────────────────────────────────────────────────

    public SortieStock findById(int id) throws SQLException {
        String sql = "SELECT id_sortie, id_produit, quantite, date_sortie, motif, prix_vente "
                   + "FROM sortie_stock WHERE id_sortie = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapper(rs);
            }
        }
        return null;
    }

    // ─── INSERT ──────────────────────────────────────────────────────────────

    /**
     * Insère une sortie de stock.
     * Le trigger `maj_stock_sortie` décrémente quantite_stock automatiquement.
     * @return id généré (> 0) ou -1
     */
    public int insert(SortieStock ss) throws SQLException {
        String sql = "INSERT INTO sortie_stock (id_produit, quantite, date_sortie, motif, prix_vente) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt       (1, ss.getIdProduit());
            ps.setInt       (2, ss.getQuantite());
            ps.setDate      (3, ss.getDateSortie() != null
                    ? Date.valueOf(ss.getDateSortie()) : Date.valueOf(java.time.LocalDate.now()));
            ps.setString    (4, ss.getMotif());
            ps.setBigDecimal(5, ss.getPrixVente());
            int rows = ps.executeUpdate();
            if (rows == 0) return -1;
            try (ResultSet rk = ps.getGeneratedKeys()) {
                if (rk.next()) return rk.getInt(1);
            }
        }
        return -1;
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    public boolean update(SortieStock ss) throws SQLException {
        String sql = "UPDATE sortie_stock SET id_produit=?, quantite=?, date_sortie=?, "
                   + "motif=?, prix_vente=? WHERE id_sortie=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt       (1, ss.getIdProduit());
            ps.setInt       (2, ss.getQuantite());
            ps.setDate      (3, ss.getDateSortie() != null
                    ? Date.valueOf(ss.getDateSortie()) : Date.valueOf(java.time.LocalDate.now()));
            ps.setString    (4, ss.getMotif());
            ps.setBigDecimal(5, ss.getPrixVente());
            ps.setInt       (6, ss.getIdSortie());
            return ps.executeUpdate() > 0;
        }
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM sortie_stock WHERE id_sortie = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ─── Mapper ──────────────────────────────────────────────────────────────

    private SortieStock mapper(ResultSet rs) throws SQLException {
        SortieStock ss = new SortieStock();
        ss.setIdSortie  (rs.getInt       ("id_sortie"));
        ss.setIdProduit (rs.getInt       ("id_produit"));
        ss.setQuantite  (rs.getInt       ("quantite"));
        Date d = rs.getDate("date_sortie");
        ss.setDateSortie(d != null ? d.toLocalDate() : null);
        ss.setMotif     (rs.getString    ("motif"));
        ss.setPrixVente (rs.getBigDecimal("prix_vente"));
        return ss;
    }
}