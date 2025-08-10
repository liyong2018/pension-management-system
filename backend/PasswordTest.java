import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // From database log
        String dbHash = "$2a$10$.dt3HNgGnUWTUw7G2FYn7.1DOlGa1/BAkEMWLTsaXtW7.pI/fXnxy";
        
        // Test passwords
        String[] passwords = {"123456", "admin123", "admin", "password"};
        
        for (String password : passwords) {
            boolean matches = encoder.matches(password, dbHash);
            System.out.println("Password '" + password + "' matches: " + matches);
        }
    }
}