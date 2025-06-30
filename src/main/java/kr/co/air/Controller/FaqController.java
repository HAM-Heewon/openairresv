package kr.co.air.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.air.Service.FaqService;
import kr.co.air.dtos.FaqDto;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/air-faq")
public class FaqController {
	
	private final FaqService fservice;
	

	@GetMapping("/list")
	public String showAllList(Model m,
			@RequestParam(value = "searchKeyword", required = false) String keyword) {
		
		List<FaqDto> datalist = fservice.getAllFAQ(keyword);
		m.addAttribute("alldata",datalist);
		m.addAttribute("searchKeyword", keyword);
		return "faq_list";
	}
	
    @GetMapping("/write")
    public String faq_write(Model m) {
    	m.addAttribute("faqForm", new FaqDto());
    	return "faq_write";
    }
	
    @PostMapping("/add")
    public String createFaq(FaqDto dto, RedirectAttributes redirectAttributes) {
        try {
            fservice.insertNewFAQ(dto);
            redirectAttributes.addFlashAttribute("successMessage", "새로운 FAQ가 등록되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "FAQ 등록 중 오류가 발생했습니다.");
        }
        return "redirect:/air-faq/list";
    }
    
	@GetMapping("/edit/{fIdx}")
	public String updateFAQform(@PathVariable("fIdx") int fIdx, Model m) {
		FaqDto dto = fservice.getFAQById(fIdx);
		m.addAttribute("faqDto",dto);
		return "faq_update";
	}
	
	@PostMapping("/update")
	@ResponseBody
	public ResponseEntity<String> updateFAQ(@RequestBody FaqDto dto) {
	    try {
	        fservice.updateFAQ(dto);
	        return ResponseEntity.ok("FAQ가 수정되었습니다.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 중 에러 발생: " + e.getMessage());
	    }
	}
	
    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteFaq(@RequestBody List<Integer> fIdx){
    	try {
    		fservice.deleteFAQ(fIdx);
    		return ResponseEntity.ok(fIdx.size() + "개의 FAQ 정보가 삭제되었습니다.");
    	}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 중 오류 발생");
		}
    }
   
}
