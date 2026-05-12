package ma.enset.campusservices.dao.impl;

import ma.enset.campusservices.dao.IAdminDAO;
import ma.enset.campusservices.database.DatabaseConfig;
import ma.enset.campusservices.model.Admin;
import ma.enset.campusservices.model.enums.Role;
import ma.enset.campusservices.security.PasswordHash;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminDAOImpl implements IAdminDAO {

    private final DatabaseConfig db = DatabaseConfig.getInstance();

    private Admin mapper(ResultSet rs) throws SQLException {
        return new Admin(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("mot_de_passe"),
                Role.valueOf(rs.getString("role"))
        );
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        String sql = "SELECT * FROM admins WHERE LOWER(email) = LOWER(?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Optional<Admin> findById(int id) {
        String sql = "SELECT * FROM admins WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public void updateMotDePasse(int id, String motDePasseHash) {
        String sql = "UPDATE admins SET mot_de_passe = ? WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, PasswordHash.hashIfNeeded(motDePasseHash));
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Admin> findAll() {
        List<Admin> list = new ArrayList<>();
        String sql = "SELECT * FROM admins ORDER BY nom, prenom";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapper(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}
