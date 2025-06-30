package kr.co.air.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.air.Service.ReservationService;
import kr.co.air.dtos.ReservationRequestDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resv")
public class ReservationApiController {

	private final ReservationService rservice;
	
	@GetMapping("/arrival")
	public ResponseEntity<List<String>> getArrival(@RequestParam("depart") String depart){
		return ResponseEntity.ok(rservice.getAvailableArrivals(depart));
	}
	
	@GetMapping("/seat-type")
	public ResponseEntity<List<String>> getSeat(@RequestParam("depart")String depart, @RequestParam("arrival")String arrival){
		return ResponseEntity.ok(rservice.getAvailableSeatTypes(depart, arrival));
	}
	
	@PostMapping("/create")
	public ResponseEntity<String> createReservation(@RequestBody ReservationRequestDto req){
		try {
			rservice.createReservation(req);
			return ResponseEntity.ok("항공편 예약이 완료되었습니다.");
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("예약 중 오류 발생");
		}
	}
}
