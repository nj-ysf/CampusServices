package ma.enset.campusservices.dao.impl;

import ma.enset.campusservices.dao.IReservationDAO;
import ma.enset.campusservices.database.DatabaseConfig;
import ma.enset.campusservices.model.ReservationSalle;
import ma.enset.campusservices.model.enums.StatutReservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationDAOImpl implements IReservationDAO {

    private final DatabaseConfig  db          = DatabaseConfig.getInstance();
    private final EtudiantDAOImpl etudiantDao = new EtudiantDAOImpl();
    private final SalleDAOImpl    salleDao    = new SalleDAOImpl();

    private ReservationSalle mapper(ResultSet rs) throws SQLException {
        var etudiant = etudiantDao.findById(rs.getInt("etudiant_id")).orElseThrow();
        var salle    = salleDao.findById(rs.getInt("salle_id")).orElseThrow();

        ReservationSalle r = new ReservationSalle(
                rs.getInt("id"),
                etudiant,
                salle,
                rs.getTimestamp("date_debut").toLocalDateTime(),
                rs.getTimestamp("date_fin").toLocalDateTime()
        );
        r.setStatut(StatutReservation.valueOf(rs.getString("statut")));
        return r;
    }

    @Override
    public List<ReservationSalle> findAll() {
        List<ReservationSalle> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations_salles ORDER BY date_debut DESC";
        try (Statement st = db.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapper(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<ReservationSalle> findByEtudiant(int etudiantId) {
        List<ReservationSalle> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations_salles WHERE etudiant_id = ? ORDER BY date_debut DESC";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<ReservationSalle> findBySalle(int salleId) {
        List<ReservationSalle> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations_salles WHERE salle_id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, salleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Optional<ReservationSalle> findById(int id) {
        String sql = "SELECT * FROM reservations_salles WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapper(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public ReservationSalle save(ReservationSalle reservation) {
        String sql = """
                INSERT INTO reservations_salles (etudiant_id, salle_id, date_debut, date_fin, statut)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id
                """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, reservation.getEtudiant().getId());
            ps.setInt(2, reservation.getSalle().getId());
            ps.setTimestamp(3, Timestamp.valueOf(reservation.getDateDebut()));
            ps.setTimestamp(4, Timestamp.valueOf(reservation.getDateFin()));
            ps.setString(5, reservation.getStatut().name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) reservation.setId(rs.getInt(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return reservation;
    }

    @Override
    public void updateStatut(int id, StatutReservation statut) {
        String sql = "UPDATE reservations_salles SET statut = ? WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, statut.name());
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
