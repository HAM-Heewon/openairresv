package kr.co.air.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.air.Service.TicketService;
import kr.co.air.dtos.ResvDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ticket")
@Slf4j
public class TicketController {

	private final TicketService tservice;
	
    @GetMapping("/list")
    public String ticketing_list(Model m,
    		@RequestParam(value="searchType", required = false) String searchType,
    		@RequestParam(value="searchKeyword", required = false)String searchKeyword) {

        List<ResvDto> reservations = tservice.findReservation(searchType, searchKeyword);
        m.addAttribute("reservations", reservations); // Thymeleaf로 예약 목록 전달
    	m.addAttribute("searchType", searchType);
    	m.addAttribute("searchKeyword",searchKeyword);
    	
    	return "ticketing_list";
    }
    
    @GetMapping("/search-api") // JavaScript의 fetch URL과 일치시켜야 합니다.
    @ResponseBody // 이 메서드는 JSON 데이터를 반환합니다.
    public ResponseEntity<List<ResvDto>> searchReservationsApi(
            @RequestParam(value="searchType", required = false) String searchType,
            @RequestParam(value="searchKeyword", required = false) String searchKeyword) {

        try {

            List<ResvDto> datalist = tservice.findReservation(searchType, searchKeyword);
            
            if (datalist != null && !datalist.isEmpty()) {
                ResvDto firstItem = datalist.get(0);
            }

            return ResponseEntity.ok(datalist);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<String> cancelReservation(@RequestBody List<Long> resvId){
    	try {
    		tservice.cancelReservation(resvId);
    		return ResponseEntity.ok("예매가 취소되었습니다.");
    	}catch (Exception e) {
    		e.printStackTrace();
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예매취소 중 오류 발생 :"+e.getMessage());
		}
    }
}
