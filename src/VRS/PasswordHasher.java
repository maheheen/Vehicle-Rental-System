package VRS;
import java.security.MessageDigest;

public class PasswordHasher {

    // Hashes a raw password
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b)); // byte to hexadecimal
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Verifies a raw password against a stored hash
    public static boolean verifyPassword(String rawPassword, String storedHash) {
        String hashedInput = hashPassword(rawPassword);
        return hashedInput != null && hashedInput.equals(storedHash);
    }
}
