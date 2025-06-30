package kr.co.air.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AirController {

	
    @GetMapping("/")
    public String home() {
    	return "redirect:/Login";
    }
	
}
