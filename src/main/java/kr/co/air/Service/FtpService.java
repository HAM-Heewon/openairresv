package kr.co.air.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import kr.co.air.config.SftpConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FtpService {
	
	
    private final SftpConfigProperties sftpConfig;
    
    private ChannelSftp connectAndLogin() {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(sftpConfig.getUsername(), sftpConfig.getHost(), sftpConfig.getPort());
            
            if (sftpConfig.getPrivateKeyPath() != null) {
                log.info("설정된 SSH 키 경로: {}", sftpConfig.getPrivateKeyPath());
            }

            // SSH 키 파일이 있는 경우 (SSH 키 우선)
            if (sftpConfig.getPrivateKeyPath() != null && !sftpConfig.getPrivateKeyPath().isEmpty()) {
                // 경로 정규화 - 백슬래시를 슬래시로 변환하고 중복 슬래시 제거
                String normalizedPath = sftpConfig.getPrivateKeyPath()
                    .replace("\\", "/")  // 백슬래시를 슬래시로 변환
                    .replaceAll("/+", "/");  // 중복 슬래시 제거
              
                log.info("정규화된 SSH 키 파일 경로: {}", normalizedPath);
                
                // 파일 존재 여부 확인
                java.io.File keyFile = new java.io.File(normalizedPath);
                if (!keyFile.exists()) {
                    log.error("SSH 키 파일을 찾을 수 없습니다: {}", normalizedPath);
                    log.error("파일 존재 여부: {}", keyFile.exists());
                    log.error("파일 절대 경로: {}", keyFile.getAbsolutePath());
                    return null;
                }
                
                if (!keyFile.canRead()) {
                    log.error("SSH 키 파일을 읽을 수 없습니다 (권한 문제): {}", normalizedPath);
                    return null;
                }
                
                // 패스프레이즈가 있는 경우와 없는 경우 처리
                if (sftpConfig.getPrivateKeyPassphrase() != null && !sftpConfig.getPrivateKeyPassphrase().isEmpty()) {
                    jsch.addIdentity(normalizedPath, sftpConfig.getPrivateKeyPassphrase());
                } else {
                    jsch.addIdentity(normalizedPath);
                }
            }
            // SSH 키가 없을 때만 비밀번호 인증 사용
            else if (sftpConfig.getPassword() != null && !sftpConfig.getPassword().isEmpty()) {
                session.setPassword(sftpConfig.getPassword());
            }

            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(sftpConfig.getConnectTimeout());
            session.connect();

            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect(sftpConfig.getSessionTimeout());
            
            return channelSftp;
            
        } catch (Exception e) {
            log.error("SFTP 연결 또는 로그인 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * SFTP 서버에서 연결을 해제합니다.
     */
    private void disconnect(ChannelSftp channelSftp) {
        try {
            if (channelSftp != null && channelSftp.isConnected()) {
                Session session = channelSftp.getSession();
                channelSftp.disconnect();
                if (session != null && session.isConnected()) {
                    session.disconnect();
                }
            }
        } catch (Exception e) {
            log.error("SFTP 연결 해제 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    public boolean uploadFile(String localFilePath, String remoteRelativePath) {
        ChannelSftp channelSftp = connectAndLogin();
        if (channelSftp == null) {
            return false;
        }
        
        try {
            // SftpConfigProperties의 baseDirectory로 이동
            String baseDir = sftpConfig.getBaseDirectory();
            if (baseDir != null && !baseDir.isEmpty()) {
                try {
                    channelSftp.cd(baseDir);
                } catch (SftpException e) {
                    try {
                        createDirectoryRecursive(channelSftp, baseDir);
                        channelSftp.cd(baseDir);
                    } catch (SftpException ex) {
                        return false;
                    }
                }
            } else {
            }

            // remoteRelativePath에서 파일명을 제외한 디렉토리 부분 추출
            String targetDirectory = "";
            String fileNameToStore = "";
            int lastSlashIndex = remoteRelativePath.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                targetDirectory = remoteRelativePath.substring(0, lastSlashIndex);
                fileNameToStore = remoteRelativePath.substring(lastSlashIndex + 1);
            } else {
                fileNameToStore = remoteRelativePath;
            }

            // 대상 디렉토리로 이동하면서 없는 디렉토리 생성
            if (!targetDirectory.isEmpty()) {
                if (!changeAndMakeDirectoryRecursive(channelSftp, targetDirectory)) {
                     log.error("SFTP 파일 업로드: 원격 디렉토리 생성/이동 실패: {}", targetDirectory);
                     return false;
                }
            }

            try (InputStream inputStream = new FileInputStream(localFilePath)) {
                log.info("SFTP 파일 업로드 시도: 로컬 {} -> 원격 (현재 워킹 디렉토리 + {})", localFilePath, fileNameToStore);
                channelSftp.put(inputStream, fileNameToStore);
                return true;
            }
        } catch (Exception e) {
            log.error("SFTP 파일 업로드 중 오류 발생: {}", e.getMessage(), e);
            return false;
        } finally {
            disconnect(channelSftp);
        }
    }

    public boolean uploadFileFromStream(InputStream inputStream, String remoteRelativePath) {
        ChannelSftp channelSftp = connectAndLogin();
        if (channelSftp == null) {
            return false;
        }
        
        try {
            // SftpConfigProperties의 baseDirectory로 이동
            String baseDir = sftpConfig.getBaseDirectory();
            if (baseDir != null && !baseDir.isEmpty()) {
                try {
                    channelSftp.cd(baseDir);
                } catch (SftpException e) {
                    try {
                        createDirectoryRecursive(channelSftp, baseDir);
                        channelSftp.cd(baseDir);
                    } catch (SftpException ex) {
                        return false;
                    }
                }
            }

            // remoteRelativePath에서 파일명을 제외한 디렉토리 부분 추출
            String targetDirectory = "";
            String fileNameToStore = "";
            int lastSlashIndex = remoteRelativePath.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                targetDirectory = remoteRelativePath.substring(0, lastSlashIndex);
                fileNameToStore = remoteRelativePath.substring(lastSlashIndex + 1);
            } else {
                fileNameToStore = remoteRelativePath;
            }

            // 대상 디렉토리로 이동하면서 없는 디렉토리 생성
            if (!targetDirectory.isEmpty()) {
                if (!changeAndMakeDirectoryRecursive(channelSftp, targetDirectory)) {
                     log.error("SFTP 파일 업로드: 원격 디렉토리 생성/이동 실패: {}", targetDirectory);
                     return false;
                }
            }

            log.info("SFTP 파일 업로드 시도: 스트림 -> 원격 (현재 워킹 디렉토리 + {})", fileNameToStore);
            channelSftp.put(inputStream, fileNameToStore);
            return true;
            
        } catch (Exception e) {
            log.error("SFTP 파일 업로드 중 오류 발생: {}", e.getMessage(), e);
            return false;
        } finally {
            disconnect(channelSftp);
        }
    }
    
    public Resource downloadFileAsResource(String remoteFilePath) {
        ChannelSftp channelSftp = connectAndLogin();
        if (channelSftp == null) {
            return null;
        }
        
        InputStream inputStream = null;
        try {
            String baseDir = sftpConfig.getBaseDirectory();
            String relativeRemotePath;
            
            // 입력된 경로가 이미 상대 경로인 경우 그대로 사용
            if (!remoteFilePath.startsWith("/")) {
                relativeRemotePath = remoteFilePath;
                log.info("상대 경로로 입력됨: {}", relativeRemotePath);
            } else {
                // 전체 경로인 경우 상대 경로로 변환
                relativeRemotePath = getRelativePath(remoteFilePath, baseDir);
                if (relativeRemotePath == null) {
                    log.error("다운로드할 파일의 경로가 SFTP baseDirectory '{}' 내에 있지 않습니다: {}", baseDir, remoteFilePath);
                    return null;
                }
            }

            // base directory로 이동
            if (baseDir != null && !baseDir.isEmpty()) {
                try {
                    channelSftp.cd(baseDir);
                } catch (SftpException e) {
                    return null;
                }
            }

            inputStream = channelSftp.get(relativeRemotePath);

            if (inputStream == null) {
                log.error("SFTP 스트림 다운로드 실패: 파일 {}", relativeRemotePath);
                return null;
            }

            log.info("SFTP 파일 다운로드 시작 (메모리 로드): 원격 (baseDir + {})", relativeRemotePath);

            // 파일을 메모리로 모두 읽어옴
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] fileContent = outputStream.toByteArray();

            return new ByteArrayResource(fileContent);

        } catch (Exception e) {
            log.error("SFTP 파일 다운로드 중 오류 발생: {}", e.getMessage(), e);
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.error("InputStream 닫기 중 오류 발생: {}", e.getMessage(), e);
            }
            disconnect(channelSftp);
        }
    }

    public boolean deleteFile(String remoteFilePath) {
        ChannelSftp channelSftp = connectAndLogin();
        if (channelSftp == null) {
            return false;
        }
        
        try {
            String baseDir = sftpConfig.getBaseDirectory();
            String relativeRemotePath;
            
            // 1차: 입력된 경로가 이미 상대 경로인 경우
            if (!remoteFilePath.startsWith("/")) {
                relativeRemotePath = remoteFilePath;
            } 
            // 2차: 현재 baseDirectory로 시작하는 경우
            else if (baseDir != null && remoteFilePath.startsWith(baseDir)) {
                relativeRemotePath = remoteFilePath.substring(baseDir.length());
                if (relativeRemotePath.startsWith("/")) {
                    relativeRemotePath = relativeRemotePath.substring(1);
                }
            }
            // 3차: 기존 경로 패턴 (/home/hewon/ftp/...) 처리
            else if (remoteFilePath.startsWith("/home/hewon/ftp/")) {
                relativeRemotePath = remoteFilePath.substring("/home/hewon/ftp/".length());
            }
            // 4차: 절대 경로를 상대 경로로 변환
            else {
                relativeRemotePath = remoteFilePath.startsWith("/") ? remoteFilePath.substring(1) : remoteFilePath;
                log.info("절대 경로를 상대 경로로 변환: {} -> {}", remoteFilePath, relativeRemotePath);
            }

            // base directory로 이동
            if (baseDir != null && !baseDir.isEmpty()) {
                try {
                    channelSftp.cd(baseDir);
                } catch (SftpException e) {
                    return false;
                }
            }
            channelSftp.rm(relativeRemotePath);
            log.info("SFTP 파일 삭제 성공: {}", relativeRemotePath);
            return true;
        } catch (Exception e) {
            log.error("SFTP 파일 삭제 중 오류 발생: {}", e.getMessage(), e);
            return false;
        } finally {
            disconnect(channelSftp);
        }
    }
    
    private String getRelativePath(String fullPath, String baseDir) {
        if (fullPath == null || fullPath.isEmpty()) {
            return null;
        }

        String normalizedFullPath = Paths.get(fullPath).normalize().toString().replace("\\", "/");
        String normalizedBaseDir = (baseDir != null && !baseDir.isEmpty()) ?
                                    Paths.get(baseDir).normalize().toString().replace("\\", "/") : "/";

        if (!normalizedBaseDir.endsWith("/")) {
            normalizedBaseDir += "/";
        }

        if (normalizedFullPath.startsWith(normalizedBaseDir)) {
            String relativePath = normalizedFullPath.substring(normalizedBaseDir.length());
            if (relativePath.startsWith("/")) {
                relativePath = relativePath.substring(1);
            }
            return relativePath;
        } else if ("/".equals(normalizedBaseDir) && normalizedFullPath.startsWith("/")) {
            return normalizedFullPath.substring(1);
        }
        log.warn("SFTP 경로 불일치: fullPath '{}'가 baseDirectory '{}' 내에 있지 않습니다.", fullPath, baseDir);
        return null;
    }
    
    private boolean changeAndMakeDirectoryRecursive(ChannelSftp channelSftp, String path) throws SftpException {
        if (path == null || path.isEmpty()) {
            return true;
        }

        String[] pathParts = path.split("/");
        for (String part : pathParts) {
            if (part.isEmpty()) continue;

            try {
                channelSftp.cd(part);
            } catch (SftpException e) {
                try {
                    channelSftp.mkdir(part);
                    channelSftp.cd(part);
                } catch (SftpException ex) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void createDirectoryRecursive(ChannelSftp channelSftp, String path) throws SftpException {
        if (path == null || path.isEmpty()) {
            return;
        }

        String[] pathParts = path.split("/");
        for (String part : pathParts) {
            if (part.isEmpty()) continue;

            try {
                channelSftp.cd(part);
            } catch (SftpException e) {
                channelSftp.mkdir(part);
                channelSftp.cd(part);
            }
        }
    }
}
