package ma.enset.campusservices.dao.impl;

import ma.enset.campusservices.dao.ISalleDAO;
import ma.enset.campusservices.database.DatabaseConfig;
import ma.enset.campusservices.model.Salle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SalleDAOImpl implements ISalleDAO {

    private final DatabaseConfig db = DatabaseConfig.getInstance();

    private Salle mapper(ResultSet rs) throws SQLException {
        return new Salle(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getInt("capacite")
        );
    }

    @Override
    public List<Salle> findAll() {
        List<Salle> list = new ArrayList<>();
        String sql = "SELECT * FROM salles ORDER BY nom";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapper(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Optional<Salle> findById(int id) {
        String sql = "SELECT * FROM salles WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }
}
