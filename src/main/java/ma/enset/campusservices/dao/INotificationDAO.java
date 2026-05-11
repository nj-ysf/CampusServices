package ma.enset.campusservices.dao;

import ma.enset.campusservices.model.Notification;

import java.util.List;

public interface INotificationDAO {
    List<Notification> findAll();
    List<Notification> findByEtudiant(int etudiantId);
    Notification       save(Notification notification);
    void               marquerLu(int id);
    void               marquerToutesLues(int etudiantId);
}
