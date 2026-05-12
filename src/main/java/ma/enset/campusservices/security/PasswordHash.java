package ma.enset.campusservices.security;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordHash {

    private static final int WORK_FACTOR = 12;

    private PasswordHash() {
    }

    public static String hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    public static String hashIfNeeded(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
        }
        return isBcryptHash(password) ? password : hash(password);
    }

    public static boolean matches(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null || storedPassword.isBlank()) {
            return false;
        }

        if (isBcryptHash(storedPassword)) {
            return BCrypt.checkpw(plainPassword, storedPassword);
        }

        return plainPassword.equals(storedPassword);
    }

    public static boolean needsHash(String storedPassword) {
        return storedPassword == null || !isBcryptHash(storedPassword);
    }

    private static boolean isBcryptHash(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }
}
