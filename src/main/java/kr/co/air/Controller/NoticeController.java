package kr.co.air.Controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.air.Service.FtpService;
import kr.co.air.Service.NoticeService;
import kr.co.air.dtos.DatalistDto;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NoticeController {
	
	private final NoticeService service;
	private final FtpService ftpService;
	
	private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);
	
    //notice 전체 출력
    @GetMapping("/notice")
    public String notice(Model m) {
    	
    	List<DatalistDto> noticeList = service.getAllNotice();
    	m.addAttribute("noticeList",noticeList);
    	return "notice_list";
    }
    
    //공지사항 입력
    @GetMapping("/notice_write")
    public String notice_write(Model m, Authentication auth) {
    	
    	DatalistDto dto = new DatalistDto();        
        dto.setEname("관리자"); 
    	m.addAttribute("newNotice",dto);
    	
    	return "notice_write";
    }
    @PostMapping("/notice_save")
    public String saveNoticeProcess(DatalistDto dto,
    		@RequestParam(value = "mFile", required = false) MultipartFile mFile,
    		Authentication auth, RedirectAttributes redir) {
        try {
            service.saveNotice(dto, mFile, auth);
            redir.addFlashAttribute("successMessage", "새로운 공지사항이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            redir.addFlashAttribute("errorMessage", "등록 중 오류가 발생했습니다.");
            logger.error(e.getMessage(), e);
        }
        return "redirect:/notice";
    }
    
    //공지사항 상세 페이지
    @GetMapping("/notice/{eno}")
    public String noticeDetailPage(@PathVariable("eno") int eno, Model model) {
    	DatalistDto notice = service.getNoticeById(eno);
        model.addAttribute("notice", notice);
        return "notice_view";
    }
    
    //공지사항 수정-기존 데이터 불러오기
    @GetMapping("/notice_update/{eno}")
    public String noticeUpdatePage(@PathVariable("eno") int eno, Model model) {
    	DatalistDto notice = service.getNoticeForUpdate(eno);
        model.addAttribute("notice", notice);
        return "notice_update";
    }
    
    //공지사항 수정
    @PostMapping("/notice_update")
    public String noticeUpdateProcess(DatalistDto dto, 
    		@RequestParam(value = "mFile", required = false) MultipartFile mFile,
    		RedirectAttributes redir) {
        try {
            service.updateNotice(dto, mFile);
            redir.addFlashAttribute("successMessage", "공지사항이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
        	logger.error("사이트 정보 수정 실패: {}", e.getMessage(), e);
            redir.addFlashAttribute("errorMessage", "수정 중 오류가 발생했습니다.");
        }
        return "redirect:/notice/" + dto.getEno(); 
    }
    
  //공지사항 파일 다운로드
    @GetMapping("/notice/download/{eno}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("eno") int eno) {
        DatalistDto notice = service.getNoticeForUpdate(eno); // 상세 정보 조회 (조회수 증가시키지 않는 메서드)
        if (notice == null || notice.getFileSavename() == null || notice.getFileSavename().isEmpty()) {
            return ResponseEntity.notFound().build(); // 파일 정보 없음
        }

        String remoteFilePath = notice.getFilePath(); // FTP 서버의 전체 경로
        String originalFileName = notice.getFileName(); // 원본 파일명
        if (remoteFilePath == null || remoteFilePath.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // 파일 경로 정보가 없음
        }

        try {
            // FtpService에서 직접 스트리밍 가능한 Resource를 받아옴
            Resource resource = ftpService.downloadFileAsResource(remoteFilePath);
            if (resource == null || !resource.exists()) {
                logger.error("SFTP 서버로부터 파일을 다운로드할 수 없습니다. ENO: {}, Path: {}", eno, remoteFilePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            
            // 파일명 URL 인코딩
            String encodedFileName = URLEncoder.encode(originalFileName, "UTF-8").replaceAll("\\+", "%20");

            // 파일 다운로드 헤더 설정
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                    .body(resource);
        } catch (IOException e) {
            logger.error("파일 다운로드 중 IO 오류 발생 (ENO: {}): {}", eno, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            logger.error("파일 다운로드 중 예상치 못한 오류 발생 (ENO: {}): {}", eno, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    //공지사항 단일 삭제
    @DeleteMapping("/notice/delete/{eno}")
    @ResponseBody
    public ResponseEntity<String> deleteNotice(@PathVariable("eno") int eno) {
        try {
            // 단일 ID를 리스트에 담아 기존 서비스 메서드 재활용
            service.deleteNoticesByIds(List.of(eno));
            // 삭제 성공 시 HTTP 200 OK와 함께 성공 메시지를 응답 본문에 포함
            return ResponseEntity.ok(eno + "번 공지사항이 삭제되었습니다."); // 변경된 부분
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("공지사항 삭제 중 오류가 발생했습니다.");
        }
    }
    
    //공지사항 삭제
    @DeleteMapping("/notice/delete")
    @ResponseBody
    public ResponseEntity<String> deleteSelectedNotices(@RequestBody List<Integer> idsToDelete) {
        try {
            if (idsToDelete == null || idsToDelete.isEmpty()) {
                return ResponseEntity.badRequest().body("삭제할 항목을 선택해주세요.");
            }
            service.deleteNoticesByIds(idsToDelete);
            return ResponseEntity.ok(idsToDelete.size() + "개의 공지사항이 삭제되었습니다.");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(500).body("삭제 중 오류가 발생했습니다.");
        }
    }

}
