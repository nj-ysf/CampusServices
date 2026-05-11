package ma.enset.campusservices.model;

import ma.enset.campusservices.model.enums.Role;

import java.util.Objects;

public class Admin {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private Role role;

    public Admin(int id, String nom, String prenom,
                 String email, String motDePasse, Role role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getNomComplet() { return prenom + " " + nom; }
    public String getEmail() { return email; }
    public String getMotDePasse() { return motDePasse; }
    public Role getRole() { return role; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Admin a)) return false;
        return id == a.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return getNomComplet() + " [" + role.getLibelle() + "]"; }
}
