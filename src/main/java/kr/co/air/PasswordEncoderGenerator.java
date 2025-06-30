package kr.co.air;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // 여기에 암호화하고 싶은 원본 비밀번호를 입력하세요.
        String rawPassword = "a1234"; 
        
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("원본 비밀번호: " + rawPassword);
        System.out.println("암호화된 비밀번호: " + encodedPassword);
    }
}