package kr.co.air.Controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.air.Service.RegisterService;
import kr.co.air.dtos.UsersDto;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RegisterController {
	
	private final RegisterService regService;
	private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
	
	@GetMapping("/admin/check_id")
	public ResponseEntity<Map<String, Boolean>> check_Id(@RequestParam("adminId") String adminId){
		if(adminId == null || adminId.trim().isEmpty()) {
			return ResponseEntity.ok(Collections.singletonMap("checkId", false));
		}
		boolean checkId = regService.checkId(adminId);
		Map<String, Boolean> resp = Collections.singletonMap("checkId", checkId);
		return ResponseEntity.ok(resp);
	}
	
	@PostMapping("/admin_req")
	@ResponseBody
	public ResponseEntity<Map<String, String>> registerform(UsersDto dto) {
	    Map<String, String> response = new HashMap<>();
	    try {
	        regService.registerNew(dto);
	        
	        // 성공 시 보낼 데이터
	        response.put("status", "success");
	        response.put("message", "관리자 등록 신청이 완료되었습니다.");
	        return ResponseEntity.ok(response);
	        
	    } catch (Exception e) {

	    	logger.error("Admin registration failed: {}", e.getMessage(), e); // 에러 로그를 자세히 출력
	        response.put("status", "error");
	        response.put("message", "등록 중 오류가 발생했습니다. 입력 내용을 다시 확인해주세요.");
	        // 서버 내부 오류(500) 상태 코드와 함께 응답
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
}
