package kr.co.air.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.co.air.Service.AgreeMangeService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("admin")
public class AdminListController {
	
	private final AgreeMangeService agreeMangeService;
	
	@GetMapping("/Yadmin/{id}")
	public String Yadmin(@PathVariable("id") Long id) {
		agreeMangeService.Yadmin(id);
		return "redirect:/admin_list";
	}
	
	@GetMapping("/Nadmin/{id}")
	public String Nadmin(@PathVariable("id") Long id) {
		agreeMangeService.Nadmin(id);
		return "redirect:/admin_list";
	}
}
