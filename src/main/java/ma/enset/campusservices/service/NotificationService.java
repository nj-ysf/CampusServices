package ma.enset.campusservices.service;

import ma.enset.campusservices.dao.INotificationDAO;
import ma.enset.campusservices.dao.impl.NotificationDAOImpl;
import ma.enset.campusservices.model.*;

import java.util.List;

/**
 * Service de gestion des notifications.
 * Utilise INotificationDAO (PostgreSQL).
 */
public class NotificationService {

    private static NotificationService instance;
    private final INotificationDAO notificationDAO;

    private NotificationService() {
        this.notificationDAO = new NotificationDAOImpl();
    }

    public static NotificationService getInstance() {
        if (instance == null) instance = new NotificationService();
        return instance;
    }

    public List<Notification> getNotificationsEtudiant(Etudiant etudiant) {
        return notificationDAO.findByEtudiant(etudiant.getId());
    }

    public long compterNonLues(Etudiant etudiant) {
        return getNotificationsEtudiant(etudiant).stream()
                .filter(n -> !n.isLu())
                .count();
    }

    public void marquerCommeLue(Notification notification) {
        notificationDAO.marquerLu(notification.getId());
        notification.marquerCommeLu();
    }

    public void marquerToutesCommeLues(Etudiant etudiant) {
        notificationDAO.marquerToutesLues(etudiant.getId());
        getNotificationsEtudiant(etudiant).forEach(Notification::marquerCommeLu);
    }

    public Notification creerNotification(Etudiant etudiant, String message, String type) {
        Notification notification = new Notification(0, etudiant, message, type);
        notificationDAO.save(notification);
        return notification;
    }

    public List<Notification> findAll() {
        return notificationDAO.findAll();
    }
}
