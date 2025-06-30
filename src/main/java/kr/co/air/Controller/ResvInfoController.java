package kr.co.air.Controller;

import java.util.ArrayList;
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

import kr.co.air.Service.ResvInfoService;
import kr.co.air.dtos.FlightResvInfoDto;
import kr.co.air.dtos.ResvGroupDto;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/resvinfo")
@RequiredArgsConstructor
public class ResvInfoController {
	
	private final ResvInfoService rservice;
	
	@GetMapping("/settings")
	public String SettingPage(
			@RequestParam(value = "Keyword", required = false)String keyword, 
			@RequestParam(value = "configStatus", defaultValue = "unconfigured") String configStatus,
			Model m){
		
		List<ResvGroupDto> settings;
		
        if (keyword != null && !keyword.trim().isEmpty()) {
            settings = rservice.getSettings(keyword, configStatus);
        } else {
            // 검색어가 없으면 빈 리스트를 생성하여 화면에 아무 데이터도 표시되지 않도록 합니다.
            settings = new ArrayList<>();
        }
        
        long unconfiguredCount = rservice.getUnconfiguredCount();
        long configuredCount = rservice.getConfiguredCount();

		m.addAttribute("settings", settings);
		m.addAttribute("Keyword", keyword);
        m.addAttribute("configStatus", configStatus);
        m.addAttribute("unconfiguredCount", unconfiguredCount);
        m.addAttribute("configuredCount", configuredCount);
		
		return "seat_list";
	}
	
	@PostMapping("/save")
	@ResponseBody
	public ResponseEntity<String> saveResvInfo(@RequestBody List<FlightResvInfoDto> dtos){
		try {
			rservice.saveResvInfo(dtos);
			return ResponseEntity.ok("예약 설정이 등록되었습니다.");
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("등록 오류입니다." + e.getMessage());
		}
	}
}
