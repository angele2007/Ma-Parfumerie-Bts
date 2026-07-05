package parfumerie.dao;

import parfumerie.modeles.Facture;
import parfumerie.modeles.Facture.Statut;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FactureDAO {

    private final Connection conn;

    public FactureDAO() throws SQLException {
        this.conn = DBConnection.getInstance();
    }

    // =========================================================================
    //  MAPPER
    // =========================================================================
    private Facture map(ResultSet rs) throws SQLException {
        Facture f = new Facture();
        f.setIdFacture(rs.getInt("id_facture"));
        f.setNumeroFacture(rs.getString("numero_facture"));
        Date d = rs.getDate("date_facture");
        if (d != null) f.setDateFacture(d.toLocalDate());
        f.setNomClient(rs.getString("nom_client"));
        f.setTelephoneClient(rs.getString("telephone_client"));
        f.setMontantTotal(rs.getBigDecimal("montant_total"));
        f.setRemise(rs.getBigDecimal("remise"));
        f.setMontantNet(rs.getBigDecimal("montant_net"));
        // CORRECTION : on appelle mapStatut() une seule fois, sans le valueOf() redondant
        f.setStatut(mapStatut(rs.getString("statut")));
        return f;
    }

    /** Convertit la String de la BD en enum Statut. */
    private Statut mapStatut(String s) {
        if (s == null) return null;
        switch (s) {
            case "Payée":   return Statut.Payée;
            case "Impayée": return Statut.Impayée;
            case "Annulée": return Statut.Annulée;
            default:        return null;
        }
    }

    // =========================================================================
    //  CRUD DE BASE
    // =========================================================================

    public List<Facture> findAll() throws SQLException {
        List<Facture> list = new ArrayList<>();
        String sql = "SELECT * FROM facture ORDER BY date_facture DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    

    public Facture findByNumero(String numero) throws SQLException {
        String sql = "SELECT * FROM facture WHERE numero_facture = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Facture> findByStatut(Statut statut) throws SQLException {
        List<Facture> list = new ArrayList<>();
        String sql = "SELECT * FROM facture WHERE statut = ? ORDER BY date_facture DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public int insert(Facture f) throws SQLException {
        String sql = "INSERT INTO facture "
                + "(numero_facture, date_facture, nom_client, telephone_client, "
                + "montant_total, remise, montant_net, statut) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getNumeroFacture());
            ps.setDate(2, f.getDateFacture() != null ? Date.valueOf(f.getDateFacture()) : null);
            ps.setString(3, f.getNomClient());
            ps.setString(4, f.getTelephoneClient());
            ps.setBigDecimal(5, f.getMontantTotal());
            ps.setBigDecimal(6, f.getRemise());
            ps.setBigDecimal(7, f.getMontantNet());
            ps.setString(8, f.getStatut() != null ? f.getStatut().name() : "Impayée");
            ps.executeUpdate();
            try (ResultSet rk = ps.getGeneratedKeys()) {
                return rk.next() ? rk.getInt(1) : -1;
            }
        }
    }

    public boolean updateStatut(int idFacture, Statut statut) throws SQLException {
        String sql = "UPDATE facture SET statut = ? WHERE id_facture = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            ps.setInt(2, idFacture);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM facture WHERE id_facture = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // =========================================================================
    //  MÉTHODES TABLEAU DE BORD
    // =========================================================================

    /** Compte les factures avec statut 'Impayée'. */
    public int countFacturesImpayees() throws SQLException {
        String sql = "SELECT COUNT(*) FROM facture WHERE statut = 'Impayée'";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Somme des montants nets des factures impayées. */
    public java.math.BigDecimal sommeFacturesImpayees() throws SQLException {
        String sql = "SELECT COALESCE(SUM(montant_net), 0) FROM facture WHERE statut = 'Impayée'";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getBigDecimal(1) : java.math.BigDecimal.ZERO;
        }
    }

    /** Chiffre d'affaires = somme des montants nets des factures payées. */
    public java.math.BigDecimal getChiffreAffaire() throws SQLException {
        String sql = "SELECT COALESCE(SUM(montant_net), 0) FROM facture WHERE statut = 'Payée'";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getBigDecimal(1) : java.math.BigDecimal.ZERO;
        }
    }
      // =========================================================================
//  REMPLACEZ la méthode prochainNumero() dans FactureDAO.java par celle-ci
// =========================================================================

    public String prochainNumero() throws SQLException {
        int annee = java.time.LocalDate.now().getYear();
        String sql = "SELECT COUNT(*) FROM facture WHERE numero_facture LIKE 'FAC-" + annee + "-%'";
        Statement st = null;
        java.sql.ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            int count = (rs.next()) ? rs.getInt(1) : 0;
            return String.format("FAC-%d-%04d", annee, count + 1);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (st != null) try { st.close(); } catch (SQLException ignored) {}
        }
    }
   
    public Facture findById(int id) throws SQLException {
    String sql = "SELECT * FROM facture WHERE id_facture = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? map(rs) : null;
        }
    }
}

    public boolean update(Facture f) throws SQLException {
            String sql = "UPDATE facture SET nom_client=?, telephone_client=?, date_facture=?, "
                       + "montant_total=?, remise=?, montant_net=?, statut=? WHERE id_facture=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, f.getNomClient());
                ps.setString(2, f.getTelephoneClient());
                ps.setDate(3, f.getDateFacture() != null ? java.sql.Date.valueOf(f.getDateFacture()) : null);
                ps.setBigDecimal(4, f.getMontantTotal());
                ps.setBigDecimal(5, f.getRemise());
                ps.setBigDecimal(6, f.getMontantNet());
                ps.setString(7, f.getStatut() != null ? f.getStatut().name() : null);
                ps.setInt(8, f.getIdFacture());
                return ps.executeUpdate() > 0;
    }
   }
}



