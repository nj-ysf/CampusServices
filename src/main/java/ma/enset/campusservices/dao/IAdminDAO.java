package ma.enset.campusservices.dao;

import ma.enset.campusservices.model.Admin;

import java.util.List;
import java.util.Optional;

public interface IAdminDAO {
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findById(int id);
    List<Admin>     findAll();
}
