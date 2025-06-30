// src/main/java/kr/co/air/config/FtpConfigProperties.java (예시 경로)
package kr.co.air.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sftp") // application.properties의 "ftp." 접두사와 매핑
public class SftpConfigProperties {

    private String host;
    private int port = 22;
    private String username;
    private String password;
    private String baseDirectory;
    private String privateKeyPath;
    private String privateKeyPassphrase;
    private int connectTimeout = 15000;
    private int sessionTimeout = 30000;
    private String webUrl;

    // Getters and Setters
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getPrivateKeyPassphrase() {
        return privateKeyPassphrase;
    }

    public void setPrivateKeyPassphrase(String privateKeyPassphrase) {
        this.privateKeyPassphrase = privateKeyPassphrase;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
    
    @Override
    public String toString() {
        return "FtpConfigProperties{" +
               "host='" + host + '\'' +
               ", port=" + port +
               ", username='" + username + '\'' +
               ", password='[PROTECTED]'" + // 보안을 위해 비밀번호는 로그에 출력하지 않는 것이 좋습니다.
               ", baseDirectory='" + baseDirectory + '\'' +
               '}';
    }
}