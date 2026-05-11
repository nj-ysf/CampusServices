package ma.enset.campusservices.dao.impl;

import ma.enset.campusservices.dao.ICatalogueDAO;
import ma.enset.campusservices.database.DatabaseConfig;
import ma.enset.campusservices.model.Catalogue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CatalogueDAOImpl implements ICatalogueDAO {

    private final DatabaseConfig db = DatabaseConfig.getInstance();

    private Catalogue mapper(ResultSet rs) throws SQLException {
        return new Catalogue(rs.getInt("id"), rs.getString("nom"));
    }

    @Override
    public List<Catalogue> findAll() {
        List<Catalogue> list = new ArrayList<>();
        String sql = "SELECT * FROM catalogues ORDER BY nom";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapper(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Optional<Catalogue> findById(int id) {
        String sql = "SELECT * FROM catalogues WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Catalogue save(Catalogue catalogue) {
        String sql = "INSERT INTO catalogues (nom) VALUES (?) RETURNING id";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, catalogue.getNom());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) catalogue.setId(rs.getInt(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return catalogue;
    }
}
