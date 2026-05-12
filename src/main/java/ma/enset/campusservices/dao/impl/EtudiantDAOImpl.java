package ma.enset.campusservices.dao.impl;

import ma.enset.campusservices.dao.IEtudiantDAO;
import ma.enset.campusservices.database.DatabaseConfig;
import ma.enset.campusservices.model.Etudiant;
import ma.enset.campusservices.model.enums.StatutEtudiant;
import ma.enset.campusservices.security.PasswordHash;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EtudiantDAOImpl implements IEtudiantDAO {

    private final DatabaseConfig db = DatabaseConfig.getInstance();

    private Etudiant mapper(ResultSet rs) throws SQLException {
        return new Etudiant(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("mot_de_passe"),
                rs.getString("filiere"),
                StatutEtudiant.valueOf(rs.getString("statut"))
        );
    }

    @Override
    public Optional<Etudiant> findByEmail(String email) {
        String sql = "SELECT * FROM etudiants WHERE LOWER(email) = LOWER(?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Optional<Etudiant> findById(int id) {
        String sql = "SELECT * FROM etudiants WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<Etudiant> findAll() {
        List<Etudiant> list = new ArrayList<>();
        String sql = "SELECT * FROM etudiants ORDER BY nom, prenom";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapper(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Etudiant save(Etudiant etudiant) {
        String sql = """
                INSERT INTO etudiants (nom, prenom, email, mot_de_passe, filiere, statut)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, etudiant.getNom());
            ps.setString(2, etudiant.getPrenom());
            ps.setString(3, etudiant.getEmail());
            ps.setString(4, PasswordHash.hashIfNeeded(etudiant.getMotDePasse()));
            ps.setString(5, etudiant.getFiliere());
            ps.setString(6, etudiant.getStatut().name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) etudiant.setId(rs.getInt(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return etudiant;
    }

    @Override
    public void updateMotDePasse(int id, String motDePasseHash) {
        String sql = "UPDATE etudiants SET mot_de_passe = ? WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, PasswordHash.hashIfNeeded(motDePasseHash));
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void updateStatut(int id, StatutEtudiant statut) {
        String sql = "UPDATE etudiants SET statut = ? WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, statut.name());
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM etudiants WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
