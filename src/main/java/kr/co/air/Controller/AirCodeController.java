package kr.co.air.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.air.Service.AirCodeService;
import kr.co.air.dtos.AirCodeDto;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AirCodeController {
	
	private final AirCodeService aservice;
	

    @GetMapping("/air_newcode")
    public String air_newcode() {
    	return "air_newcode";
    }

    //항공편 코드 중복확인
    @GetMapping("/air-codes/check")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkDuplicate(@RequestParam(value = "flightCode") String flightCode) {
        if (flightCode == null || flightCode.trim().isEmpty()) {
            // 요청이 잘못되었을 경우 400 Bad Request 응답
            return ResponseEntity.badRequest().build();
        }
        boolean isDuplicate = aservice.isFlightCodeDuplicate(flightCode);
        return ResponseEntity.ok(Map.of("isDuplicate", isDuplicate));
    }

    // 3. 항공 코드 생성(등록) API
    @PostMapping("/air-codes")
    @ResponseBody
    public ResponseEntity<String> createAirCode(@RequestBody AirCodeDto dto) {
        try {
            // 서버 측 유효성 검사 (필요시 추가)
            if (aservice.isFlightCodeDuplicate(dto.getFlightCode())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                     .body("이미 존재하는 항공편코드입니다.");
            }
            
            aservice.createAirCode(dto);
            return ResponseEntity.ok("항공 코드가 성공적으로 생성되었습니다.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("항공 코드 생성 중 오류가 발생했습니다.");
        }
    }
    
    @GetMapping("/code_list")
    public String showCodeList(Model m,
    		@RequestParam(value = "searchType", required = false) String stype,
    		@RequestParam(value = "searchKeyword", required = false) String keyword) {
        List<AirCodeDto> airCodeList = aservice.getAllCodes(stype, keyword);
        m.addAttribute("airCodeList", airCodeList);
        // 검색어를 화면에 유지하기 위해 모델에 추가
        m.addAttribute("searchKeyword", keyword); 
        return "code_list";
    }
    
    @GetMapping("/code/edit/{airIdx}")
    public String editAirCodeForm(@PathVariable("airIdx") int airIdx, Model model) {
        // 1. 서비스에서 기존 항공 코드 정보를 ID로 조회합니다.
        AirCodeDto airCodeData = aservice.getAirCodeById(airIdx);
        // 2. 모델에 "airCode" 라는 이름으로 조회한 데이터를 담습니다.
        model.addAttribute("airCode", airCodeData);
        // 3. 수정 전용 View 페이지를 반환합니다.
        return "code_update";
    }
    
    @PostMapping("/code/update")
    @ResponseBody
    public ResponseEntity<String> updateAirCode(@RequestBody AirCodeDto dto) {
        try {
            String useStatusValue = "사용함".equals(dto.getUseStatus()) ? "Y" : "N";
            dto.setUseStatus(useStatusValue);
            aservice.updateAirCode(dto);
            return ResponseEntity.ok("항공 코드가 수정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 중 오류가 발생했습니다.");
            
        }

    }
    
    
    @DeleteMapping("/air-codes/delete")
    @ResponseBody
    public ResponseEntity<String> deleteAirCodes(@RequestBody List<Integer> idsToDelete) {
        try {
            aservice.deleteAirCodes(idsToDelete);
            return ResponseEntity.ok(idsToDelete.size() + "개의 항공 코드가 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("삭제 중 오류가 발생했습니다.");
        }
    }
}
