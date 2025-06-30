package kr.co.air.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.air.Service.CompanyService;
import kr.co.air.dtos.CompanyInfoDto;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CompanyController {
	
	private final CompanyService service;
	
	private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);
	
	@GetMapping("/siteinfo")
	public String infoPage(Model m, Authentication auth) {
		
		CompanyInfoDto sitedata = service.getInfo();
		m.addAttribute("sitedata",sitedata);
		
		
		boolean adminLevel = false;
		if(auth != null) {
			adminLevel =auth.getAuthorities().stream()
					.anyMatch(GrantedAuthority -> GrantedAuthority.getAuthority().equals("ROLE_관리자"));
		}
		m.addAttribute("adminlevel",adminLevel);
		return "siteinfo";
	}
	
	@PostMapping("/siteinfo")
	@PreAuthorize("hasRole('ROLE_관리자')")
	public String updateInfo(@ModelAttribute("sitedata") CompanyInfoDto dto, RedirectAttributes redirectAttributes) {
		
		try {
			service.updateInfo(dto);
			redirectAttributes.addFlashAttribute("successMessage", "사이트 정보가 성공적으로 수정되었습니다.");
		} catch (Exception e) {
			logger.error("사이트 정보 수정 실패: {}", e.getMessage(), e);
			redirectAttributes.addFlashAttribute("errorMessage", "정보 수정 중 오류가 발생했습니다.");
		}
		
		return "redirect:/siteinfo";
	}
}
