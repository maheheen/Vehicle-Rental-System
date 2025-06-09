public class TestHash {
    public static void main(String[] args) {
        String rawPassword = "Rafiq20";
        String hash = VRS.PasswordHasher.hashPassword(rawPassword);
        System.out.println("Hashed password: " + hash);
    }
}
