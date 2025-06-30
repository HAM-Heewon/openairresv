package kr.co.air.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import kr.co.air.config.FtpConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FtpService {
	
	
    private final FtpConfigProperties ftpConfig;
    
	private boolean connectAndLogin(FTPClient ftpClient) {
        try {
            ftpClient.connect(ftpConfig.getHost(), ftpConfig.getPort());
            log.info("FTP 서버 연결 시도: {}:{}", ftpConfig.getHost(), ftpConfig.getPort());

            if (ftpClient.login(ftpConfig.getUsername(), ftpConfig.getPassword())) {
                ftpClient.enterLocalPassiveMode(); // 패시브 모드 설정 (방화벽 문제 해결에 도움)
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // 바이너리 파일 전송 모드
                log.info("FTP 서버 로그인 성공: 사용자 {}", ftpConfig.getUsername());
                return true;
            } else {
                log.error("FTP 서버 로그인 실패: 사용자 {}, 비밀번호 불일치 또는 권한 없음", ftpConfig.getUsername());
                ftpClient.disconnect();
                return false;
            }
        } catch (IOException e) {
            log.error("FTP 연결 또는 로그인 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * FTP 서버에서 연결을 해제합니다.
     * @param ftpClient FTPClient 인스턴스
     */
    private void disconnect(FTPClient ftpClient) {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
                log.info("FTP 서버 연결 해제 완료.");
            }
        } catch (IOException e) {
            log.error("FTP 연결 해제 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    public boolean uploadFile(String localFilePath, String remoteRelativePath) {
        FTPClient ftpClient = new FTPClient();
        try {
            if (!connectAndLogin(ftpClient)) {
                return false;
            }

            // FTPConfigProperties의 baseDirectory로 이동
            String baseDir = ftpConfig.getBaseDirectory();
            if (baseDir != null && !baseDir.isEmpty()) {
                // baseDir로 이동 또는 생성
                if (!ftpClient.changeWorkingDirectory(baseDir)) {
                    log.warn("FTP base directory '{}' not found or inaccessible. Attempting to create it.", baseDir);
                    if (!ftpClient.makeDirectory(baseDir)) {
                        log.error("Failed to create base directory '{}'. Reply: {}", baseDir, ftpClient.getReplyString());
                        return false;
                    }
                    if (!ftpClient.changeWorkingDirectory(baseDir)) {
                        log.error("Failed to change working directory to created base '{}'. Reply: {}", baseDir, ftpClient.getReplyString());
                        return false;
                    }
                }
                log.info("Changed working directory to base directory: {}", ftpClient.printWorkingDirectory());
            } else {
                log.info("FTP baseDirectory is not configured. Starting from FTP root.");
            }

            // remoteRelativePath에서 파일명을 제외한 디렉토리 부분 추출
            String targetDirectory = "";
            String fileNameToStore = "";
            int lastSlashIndex = remoteRelativePath.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                targetDirectory = remoteRelativePath.substring(0, lastSlashIndex);
                fileNameToStore = remoteRelativePath.substring(lastSlashIndex + 1);
            } else {
                // remoteRelativePath가 디렉토리 없이 파일명만 포함하는 경우
                fileNameToStore = remoteRelativePath;
            }

            // 대상 디렉토리 (상대 경로)로 이동하면서 없는 디렉토리 생성
            if (!targetDirectory.isEmpty()) { // targetDirectory가 있을 때만 이동/생성 시도
                if (!changeAndMakeDirectoryRecursive(ftpClient, targetDirectory)) {
                     log.error("FTP 파일 업로드: 원격 디렉토리 생성/이동 실패: {}", targetDirectory);
                     return false;
                }
            }


            try (InputStream inputStream = new FileInputStream(localFilePath)) {
                log.info("FTP 파일 업로드 시도: 로컬 {} -> 원격 (현재 워킹 디렉토리 + {})", localFilePath, fileNameToStore);
                boolean success = ftpClient.storeFile(fileNameToStore, inputStream); // <--- !!! 이 부분이 핵심 변경 !!!
                if (success) {
                    log.info("FTP 파일 업로드 성공: {}", fileNameToStore);
                } else {
                	log.error("FTP 파일 업로드 실패: {} (응답 코드: {}), 서버 응답: {}", fileNameToStore, ftpClient.getReplyCode(), ftpClient.getReplyString());
                }
                return success;
            }
        } catch (IOException e) {
            log.error("FTP 파일 업로드 중 오류 발생: {}", e.getMessage(), e);
            return false;
        } finally {
            disconnect(ftpClient);
        }
    }

    public Resource downloadFileAsResource(String remoteFilePath) {
        FTPClient ftpClient = new FTPClient(); 
        InputStream inputStream = null;
        try {
            if (!connectAndLogin(ftpClient)) {
                return null;
            }

            String baseDir = ftpConfig.getBaseDirectory();
            String relativeRemotePath = getRelativePath(remoteFilePath, baseDir); // 헬퍼 메서드 사용

            if (relativeRemotePath == null) {
                log.error("다운로드할 파일의 경로가 FTP baseDirectory '{}' 내에 있지 않습니다: {}", baseDir, remoteFilePath);
                return null; // finally에서 disconnect 처리
            }

            // retrieveFileStream은 데이터를 실제로 읽을 때까지 연결을 유지함
            inputStream = ftpClient.retrieveFileStream(relativeRemotePath);

            if (inputStream == null) {
                // retrieveFileStream이 null을 반환하면 파일이 없거나 오류 발생
                // completePendingCommand를 호출하여 서버 응답을 처리해야 함
                if (!ftpClient.completePendingCommand()) { // 이 명령이 중요
                    log.error("FTP 스트림 다운로드 실패: 파일 {} (응답 코드: {}). {}", relativeRemotePath, ftpClient.getReplyCode(), ftpClient.getReplyString());
                } else {
                    log.error("FTP 스트림 다운로드 실패: 파일 {} (InputStream이 null이지만 completePendingCommand 성공).", relativeRemotePath);
                }
                return null; // finally에서 disconnect 처리
            }

            log.info("FTP 파일 다운로드 시작 (메모리 로드): 원격 (baseDir + {})", relativeRemotePath);

            // 파일을 메모리로 모두 읽어옴
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096]; // 4KB 버퍼
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] fileContent = outputStream.toByteArray();
            
            // 스트림을 모두 읽은 후 completePendingCommand 호출
            if (!ftpClient.completePendingCommand()) {
                log.error("Error completing pending command after stream read for {}. Reply: {}", relativeRemotePath, ftpClient.getReplyString());
            }

            // ByteArrayResource 반환 (가장 간편)
            return new ByteArrayResource(fileContent);

        } catch (IOException e) {
            log.error("FTP 파일 다운로드 중 오류 발생: {}", e.getMessage(), e);
            return null;
        } finally {
            // InputStream이 열려있다면 닫기
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.error("InputStream 닫기 중 오류 발생: {}", e.getMessage(), e);
            }
            // FTP 클라이언트 연결 해제
            disconnect(ftpClient);
        }
    }


    public boolean deleteFile(String remoteFilePath) {
        FTPClient ftpClient = new FTPClient();
        try {
            if (!connectAndLogin(ftpClient)) {
                return false;
            }

            String baseRemoteDir = ftpConfig.getBaseDirectory();
            String relativeRemotePath = remoteFilePath;
            if (remoteFilePath.startsWith(baseRemoteDir)) {
                relativeRemotePath = remoteFilePath.substring(baseRemoteDir.length());
            }
            if (relativeRemotePath.startsWith("/")) {
                relativeRemotePath = relativeRemotePath.substring(1);
            }

            log.info("FTP 파일 삭제 시도: {}", relativeRemotePath);
            boolean success = ftpClient.deleteFile(relativeRemotePath);
            if (success) {
                log.info("FTP 파일 삭제 성공: {}", relativeRemotePath);
            } else {
                log.error("FTP 파일 삭제 실패: {} (응답 코드: {})", relativeRemotePath, ftpClient.getReplyCode());
            }
            return success;
        } catch (IOException e) {
            log.error("FTP 파일 삭제 중 오류 발생: {}", e.getMessage(), e);
            return false;
        } finally {
            disconnect(ftpClient);
        }
    }
    
    private String getRelativePath(String fullPath, String baseDir) {
        if (fullPath == null || fullPath.isEmpty()) {
            return null;
        }

        // 경로 구분자를 '/'로 통일하고 정규화
        String normalizedFullPath = Paths.get(fullPath).normalize().toString().replace("\\", "/");
        String normalizedBaseDir = (baseDir != null && !baseDir.isEmpty()) ?
                                    Paths.get(baseDir).normalize().toString().replace("\\", "/") : "/";

        // baseDir이 '/'로 끝나지 않으면 추가
        if (!normalizedBaseDir.endsWith("/")) {
            normalizedBaseDir += "/";
        }

        if (normalizedFullPath.startsWith(normalizedBaseDir)) {
            String relativePath = normalizedFullPath.substring(normalizedBaseDir.length());
            // 상대 경로가 '/'로 시작하면 제거 (예: "/notice_files/..." -> "notice_files/...")
            if (relativePath.startsWith("/")) {
                relativePath = relativePath.substring(1);
            }
            return relativePath;
        } else if ("/".equals(normalizedBaseDir) && normalizedFullPath.startsWith("/")) {
            // baseDirectory가 루트인 경우, 전체 경로에서 선행 슬래시만 제거
            return normalizedFullPath.substring(1);
        }
        log.warn("FTP 경로 불일치: fullPath '{}'가 baseDirectory '{}' 내에 있지 않습니다.", fullPath, baseDir);
        return null;
    }
    
    
    private boolean changeAndMakeDirectoryRecursive(FTPClient ftpClient, String path) throws IOException {
        if (path == null || path.isEmpty()) {
            return true; // 경로가 없으면 성공으로 간주
        }

        String[] pathParts = path.split("/");
        for (String part : pathParts) {
            if (part.isEmpty()) continue; // 빈 문자열 건너뛰기 (예: //a/b)

            // 현재 디렉토리가 존재하는지 확인하고, 없으면 생성
            if (!ftpClient.changeWorkingDirectory(part)) { // 현재 디렉토리 기준으로 part로 이동 시도
                log.info("Attempting to create directory: {}", ftpClient.printWorkingDirectory() + "/" + part);
                if (ftpClient.makeDirectory(part)) { // 현재 디렉토리 아래에 part 생성
                    log.info("Directory created: {}", part);
                    if (!ftpClient.changeWorkingDirectory(part)) { // 생성 후 해당 디렉토리로 이동
                        log.error("Failed to change working directory after creation: {}. Reply: {}", part, ftpClient.getReplyString());
                        return false;
                    }
                } else {
                    log.error("Failed to create directory: {} (Reply Code: {})", part, ftpClient.getReplyCode());
                    return false;
                }
            } else {
                log.info("Changed working directory to: {}", ftpClient.printWorkingDirectory());
            }
        }
        return true;
    }
}
