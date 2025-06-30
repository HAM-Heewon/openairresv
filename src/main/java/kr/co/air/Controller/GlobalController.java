package kr.co.air.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import kr.co.air.Service.CompanyService;
import kr.co.air.dtos.CompanyInfoDto;
import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalController {
	
	private final CompanyService service;
	
	@ModelAttribute
	public void globalCompanyData(Model m) {
		CompanyInfoDto dto = service.getInfo();
		
		if(dto != null) {
			m.addAttribute("globaldata",dto);
		}
	}
}
