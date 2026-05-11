package ma.enset.campusservices.dao.impl;

import ma.enset.campusservices.dao.INotificationDAO;
import ma.enset.campusservices.database.DatabaseConfig;
import ma.enset.campusservices.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOImpl implements INotificationDAO {

    private final DatabaseConfig  db          = DatabaseConfig.getInstance();
    private final EtudiantDAOImpl etudiantDao = new EtudiantDAOImpl();

    private Notification mapper(ResultSet rs) throws SQLException {
        var etudiant = etudiantDao.findById(rs.getInt("etudiant_id")).orElseThrow();
        Notification n = new Notification(
                rs.getInt("id"),
                etudiant,
                rs.getString("message"),
                rs.getString("type")
        );
        n.setLu(rs.getBoolean("lu"));
        n.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return n;
    }

    @Override
    public List<Notification> findAll() {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications ORDER BY created_at DESC";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapper(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<Notification> findByEtudiant(int etudiantId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE etudiant_id = ? ORDER BY created_at DESC";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Notification save(Notification notification) {
        String sql = """
                INSERT INTO notifications (etudiant_id, message, type, lu, created_at)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id
                """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, notification.getEtudiant().getId());
            ps.setString(2, notification.getMessage());
            ps.setString(3, notification.getType());
            ps.setBoolean(4, notification.isLu());
            ps.setTimestamp(5, Timestamp.valueOf(notification.getCreatedAt()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) notification.setId(rs.getInt(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return notification;
    }

    @Override
    public void marquerLu(int id) {
        String sql = "UPDATE notifications SET lu = TRUE WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void marquerToutesLues(int etudiantId) {
        String sql = "UPDATE notifications SET lu = TRUE WHERE etudiant_id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
