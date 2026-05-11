package ma.enset.campusservices.service;

import java.util.List;
import java.util.Optional;

/**
 * Interface générique pour les opérations CRUD de base.
 * @param <T> le type d'entité géré
 */
public interface IGestionable<T> {

    T creer(T entite);

    Optional<T> trouverParId(int id);

    List<T> trouverTous();

    void supprimer(int id);
}
