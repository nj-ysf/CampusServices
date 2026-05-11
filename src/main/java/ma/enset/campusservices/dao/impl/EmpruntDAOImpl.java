package ma.enset.campusservices.dao.impl;

import ma.enset.campusservices.dao.IEmpruntDAO;
import ma.enset.campusservices.database.DatabaseConfig;
import ma.enset.campusservices.model.Emprunt;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpruntDAOImpl implements IEmpruntDAO {

    private final DatabaseConfig   db       = DatabaseConfig.getInstance();
    private final EtudiantDAOImpl  etudiantDao = new EtudiantDAOImpl();
    private final RessourceDAOImpl ressourceDao = new RessourceDAOImpl();

    private Emprunt mapper(ResultSet rs) throws SQLException {
        int etudiantId  = rs.getInt("etudiant_id");
        int ressourceId = rs.getInt("ressource_id");

        var etudiant  = etudiantDao.findById(etudiantId).orElseThrow();
        var ressource = ressourceDao.findById(ressourceId).orElseThrow();

        Emprunt e = new Emprunt(
                rs.getInt("id"),
                etudiant,
                ressource,
                rs.getDate("date_emprunt").toLocalDate()
        );
        Date retour = rs.getDate("date_retour");
        if (retour != null) e.setDateRetour(retour.toLocalDate());
        return e;
    }

    @Override
    public List<Emprunt> findAll() {
        List<Emprunt> list = new ArrayList<>();
        String sql = "SELECT * FROM emprunts ORDER BY date_emprunt DESC";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapper(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<Emprunt> findByEtudiant(int etudiantId) {
        List<Emprunt> list = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE etudiant_id = ? ORDER BY date_emprunt DESC";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<Emprunt> findActifsByEtudiant(int etudiantId) {
        List<Emprunt> list = new ArrayList<>();
        String sql = "SELECT * FROM emprunts WHERE etudiant_id = ? AND date_retour IS NULL ORDER BY date_emprunt DESC";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Optional<Emprunt> findById(int id) {
        String sql = "SELECT * FROM emprunts WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Emprunt save(Emprunt emprunt) {
        String sql = """
                INSERT INTO emprunts (etudiant_id, ressource_id, date_emprunt)
                VALUES (?, ?, ?)
                RETURNING id
                """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, emprunt.getEtudiant().getId());
            ps.setInt(2, emprunt.getRessource().getId());
            ps.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) emprunt.setId(rs.getInt(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return emprunt;
    }

    @Override
    public void retourner(int id, LocalDate dateRetour) {
        String sql = "UPDATE emprunts SET date_retour = ? WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dateRetour));
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
