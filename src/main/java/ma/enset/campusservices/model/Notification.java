package ma.enset.campusservices.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Notification {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private Etudiant etudiant;
    private String message;
    private boolean lu;
    private LocalDateTime createdAt;
    private String type;

    public Notification(int id, Etudiant etudiant, String message, String type) {
        this.id = id;
        this.etudiant = etudiant;
        this.message = message;
        this.type = type;
        this.lu = false;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Etudiant getEtudiant() { return etudiant; }
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCreatedAtFormatee() {
        return createdAt != null ? createdAt.format(FORMATTER) : "-";
    }

    public void marquerCommeLu() { this.lu = true; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification n)) return false;
        return id == n.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return message; }
}
