package kr.co.air.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.co.air.Service.FtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/sftp-test")
@RequiredArgsConstructor
@Slf4j
public class SftpTestController {

    private final FtpService ftpService;

    /**
     * SFTP 테스트 페이지
     */
    @GetMapping
    public String sftpTestPage(Model model) {
        return "sftp_test";
    }

    /**
     * SFTP 연결 테스트
     */
    @PostMapping("/connection")
    @ResponseBody
    public String testConnection() {
        try {
            // 간단한 텍스트 파일을 SFTP로 업로드해서 연결 테스트
            String testContent = "SFTP 연결 테스트 - " + java.time.LocalDateTime.now();
            String testFileName = "test/connection_test_" + System.currentTimeMillis() + ".txt";
            
            ByteArrayInputStream testStream = new ByteArrayInputStream(testContent.getBytes(StandardCharsets.UTF_8));
            boolean success = ftpService.uploadFileFromStream(testStream, testFileName);
            
            if (success) {
                return "✅ SFTP 연결 성공! 테스트 파일 업로드 완료: " + testFileName;
            } else {
                //log.error("SFTP 연결 테스트 실패");
                return "❌ SFTP 연결 실패! 로그를 확인하세요.";
            }
        } catch (Exception e) {
            //log.error("SFTP 연결 테스트 중 오류: {}", e.getMessage(), e);
            return "❌ SFTP 연결 오류: " + e.getMessage();
        }
    }

    /**
     * 파일 업로드 테스트
     */
    @PostMapping("/upload")
    @ResponseBody
    public String testUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "❌ 파일을 선택해주세요.";
        }

        try {
            String originalFileName = file.getOriginalFilename();
            String testFileName = "test/upload_" + System.currentTimeMillis() + "_" + originalFileName;
            
            boolean success = ftpService.uploadFileFromStream(file.getInputStream(), testFileName);
            
            if (success) {
                //log.info("SFTP 파일 업로드 테스트 성공: {}", testFileName);
                return "✅ 파일 업로드 성공! " + testFileName + " (크기: " + file.getSize() + " bytes)";
            } else {
                //log.error("SFTP 파일 업로드 테스트 실패: {}", originalFileName);
                return "❌ 파일 업로드 실패! 로그를 확인하세요.";
            }
        } catch (IOException e) {
            //log.error("파일 업로드 테스트 중 오류: {}", e.getMessage(), e);
            return "❌ 파일 업로드 오류: " + e.getMessage();
        }
    }

    /**
     * 파일 다운로드 테스트
     */
    @PostMapping("/download")
    @ResponseBody
    public ResponseEntity<Resource> testDownload(@RequestParam("filePath") String filePath) {
        try {
            Resource resource = ftpService.downloadFileAsResource(filePath);
            
            if (resource != null && resource.exists()) {
                //log.info("SFTP 파일 다운로드 테스트 성공: {}", filePath);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"downloaded_" + System.currentTimeMillis() + ".txt\"")
                        .body(resource);
            } else {
                //log.error("SFTP 파일 다운로드 테스트 실패: 파일 없음 - {}", filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            //log.error("파일 다운로드 테스트 중 오류: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 파일 삭제 테스트
     */
    @PostMapping("/delete")
    @ResponseBody
    public String testDelete(@RequestParam("filePath") String filePath) {
        try {
            boolean success = ftpService.deleteFile(filePath);
            
            if (success) {
                //log.info("SFTP 파일 삭제 테스트 성공: {}", filePath);
                return "✅ 파일 삭제 성공! " + filePath;
            } else {
                //log.error("SFTP 파일 삭제 테스트 실패: {}", filePath);
                return "❌ 파일 삭제 실패! 파일이 존재하지 않거나 권한이 없습니다.";
            }
        } catch (Exception e) {
            //log.error("파일 삭제 테스트 중 오류: {}", e.getMessage(), e);
            return "❌ 파일 삭제 오류: " + e.getMessage();
        }
    }

    /**
     * SFTP 서버 디렉토리 목록 확인 (추가 기능)
     */
    @PostMapping("/list-files")
    @ResponseBody
    public String listFiles(@RequestParam(value = "directory", defaultValue = "test") String directory) {
        // 이 기능은 FtpService에 추가 메서드가 필요하므로 생략
        return "📁 디렉토리 목록 기능은 추후 구현 예정";
    }
}