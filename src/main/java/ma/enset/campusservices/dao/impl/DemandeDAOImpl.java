package ma.enset.campusservices.dao.impl;

import ma.enset.campusservices.dao.IDemandeDAO;
import ma.enset.campusservices.database.DatabaseConfig;
import ma.enset.campusservices.model.DemandeAdministrative;
import ma.enset.campusservices.model.enums.StatutDemande;
import ma.enset.campusservices.model.enums.TypeDemande;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DemandeDAOImpl implements IDemandeDAO {

    private final DatabaseConfig  db          = DatabaseConfig.getInstance();
    private final EtudiantDAOImpl etudiantDao = new EtudiantDAOImpl();

    private DemandeAdministrative mapper(ResultSet rs) throws SQLException {
        var etudiant = etudiantDao.findById(rs.getInt("etudiant_id")).orElseThrow();

        DemandeAdministrative d = new DemandeAdministrative(
                rs.getInt("id"),
                etudiant,
                TypeDemande.valueOf(rs.getString("type_demande")),
                rs.getString("description")
        );
        d.setStatut(StatutDemande.valueOf(rs.getString("statut")));
        d.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        d.setCommentaireAdmin(rs.getString("commentaire_admin"));
        return d;
    }

    @Override
    public List<DemandeAdministrative> findAll() {
        List<DemandeAdministrative> list = new ArrayList<>();
        String sql = "SELECT * FROM demandes_administratives ORDER BY date_creation DESC";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapper(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<DemandeAdministrative> findByEtudiant(int etudiantId) {
        List<DemandeAdministrative> list = new ArrayList<>();
        String sql = "SELECT * FROM demandes_administratives WHERE etudiant_id = ? ORDER BY date_creation DESC";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Optional<DemandeAdministrative> findById(int id) {
        String sql = "SELECT * FROM demandes_administratives WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public DemandeAdministrative save(DemandeAdministrative d) {
        String sql = """
                INSERT INTO demandes_administratives
                    (etudiant_id, type_demande, description, statut, date_creation)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id
                """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, d.getEtudiant().getId());
            ps.setString(2, d.getType().name());
            ps.setString(3, d.getDescription());
            ps.setString(4, d.getStatut().name());
            ps.setTimestamp(5, Timestamp.valueOf(d.getDateCreation()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) d.setId(rs.getInt(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return d;
    }

    @Override
    public void updateStatut(int id, StatutDemande statut, String commentaire) {
        String sql = "UPDATE demandes_administratives SET statut = ?, commentaire_admin = ? WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, statut.name());
            ps.setString(2, commentaire);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
