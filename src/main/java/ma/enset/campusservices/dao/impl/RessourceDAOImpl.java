package ma.enset.campusservices.dao.impl;

import ma.enset.campusservices.dao.IRessourceDAO;
import ma.enset.campusservices.database.DatabaseConfig;
import ma.enset.campusservices.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RessourceDAOImpl implements IRessourceDAO {

    private final DatabaseConfig  db          = DatabaseConfig.getInstance();
    private final CatalogueDAOImpl catDao      = new CatalogueDAOImpl();

    private Ressource mapper(ResultSet rs) throws SQLException {
        int catalogueId = rs.getInt("catalogue_id");
        Catalogue catalogue = catalogueId != 0
                ? catDao.findById(catalogueId).orElse(null)
                : null;

        String type = rs.getString("type");
        if ("LIVRE".equals(type)) {
            return new Livre(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("auteur"),
                    rs.getBoolean("disponible"),
                    catalogue,
                    rs.getString("isbn"),
                    rs.getInt("nb_pages"),
                    rs.getString("editeur")
            );
        } else {
            return new Memoire(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("auteur"),
                    rs.getBoolean("disponible"),
                    catalogue,
                    rs.getString("specialite"),
                    rs.getInt("annee"),
                    rs.getString("encadrant")
            );
        }
    }

    @Override
    public List<Ressource> findAll() {
        List<Ressource> list = new ArrayList<>();
        String sql = "SELECT * FROM ressources ORDER BY titre";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapper(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<Ressource> findByCatalogue(int catalogueId) {
        List<Ressource> list = new ArrayList<>();
        String sql = "SELECT * FROM ressources WHERE catalogue_id = ? ORDER BY titre";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, catalogueId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Optional<Ressource> findById(int id) {
        String sql = "SELECT * FROM ressources WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Ressource save(Ressource ressource) {
        String sql = """
                INSERT INTO ressources
                    (titre, auteur, disponible, catalogue_id, type,
                     isbn, nb_pages, editeur,
                     specialite, annee, encadrant)
                VALUES (?,?,?,?,?,?,?,?,?,?,?)
                RETURNING id
                """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, ressource.getTitre());
            ps.setString(2, ressource.getAuteur());
            ps.setBoolean(3, ressource.isDisponible());
            if (ressource.getCatalogue() != null)
                ps.setInt(4, ressource.getCatalogue().getId());
            else ps.setNull(4, Types.INTEGER);
            ps.setString(5, ressource.getType().toUpperCase());

            if (ressource instanceof Livre l) {
                ps.setString(6, l.getIsbn());
                ps.setInt(7, l.getNbPages());
                ps.setString(8, l.getEditeur());
                ps.setNull(9, Types.VARCHAR);
                ps.setNull(10, Types.INTEGER);
                ps.setNull(11, Types.VARCHAR);
            } else if (ressource instanceof Memoire m) {
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.INTEGER);
                ps.setNull(8, Types.VARCHAR);
                ps.setString(9, m.getSpecialite());
                ps.setInt(10, m.getAnnee());
                ps.setString(11, m.getEncadrant());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) ressource.setId(rs.getInt(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return ressource;
    }

    @Override
    public void updateDisponibilite(int id, boolean disponible) {
        String sql = "UPDATE ressources SET disponible = ? WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setBoolean(1, disponible);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM ressources WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
