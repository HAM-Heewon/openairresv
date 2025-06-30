package kr.co.air.Controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kr.co.air.Service.ReservationService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReservationController {
	
	private final ReservationService rservice;
	
    @GetMapping("/index")
    public String index(Model m) {
    	
    	List<String> depart = rservice.getAvailableDepartures();
    	
    	m.addAttribute("depart", depart);

    	return "index";
    }
	
}
