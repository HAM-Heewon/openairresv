package kr.co.air.Controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.air.Service.AdminListService;
import kr.co.air.dtos.UsersDto;
import kr.co.air.mapper.UserMapper;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final AdminListService adminListService;
    private final UserMapper usermapper;
    
    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, Model model) {
        // 세션에서 에러 메시지 확인
        HttpSession session = request.getSession(false);
        if (session != null) {
            String errorMessage = (String) session.getAttribute("loginError");
            if (errorMessage != null) {
                model.addAttribute("errorMessage", errorMessage);
                model.addAttribute("hasError", true);
                session.removeAttribute("loginError"); // 사용 후 제거
            }
        }
        return "Login";
    }

    @GetMapping("/admin_list")
    public String adminListPage(Model m, Authentication auth) {
    	Map<String, Object> alldata = adminListService.getAdminPageData(auth);
    	String username = auth.getName();
    	UsersDto userdata = usermapper.findByUsername(username);
    	m.addAttribute("userdata",userdata);
    	
        m.addAttribute("adminList", alldata.get("adminList"));
        m.addAttribute("isTopLevelAdmin", alldata.get("isTopLevelAdmin"));
    	
        return "admin_list";
    }

    // 3. '신규 관리자 등록요청' 링크를 위한 페이지
    @GetMapping("/add_master")
    public String addMasterPage() {
        return "add_master";
    }
    
    @GetMapping("/myInfo")
    public String getMyInfo(Principal principal, Model model) {

        String adminId = principal.getName(); 

        UsersDto adminInfo = adminListService.getMyInfo(adminId);
        if (adminInfo == null) {
            return "redirect:/admin_list";
        }
        model.addAttribute("adminInfo", adminInfo);
        return "Update_user"; 
    }

    // POST 요청: 개인정보 수정 폼 제출을 처리
    @PostMapping("/myUpdate")
    public String updateMyInfo(@ModelAttribute UsersDto usersDto, RedirectAttributes redirectAttributes) {
        // DTO 객체는 폼에서 제출된 데이터로 자동 바인딩됩니다.
        // adminId는 폼의 hidden 필드나, Principal 객체에서 다시 가져오는 것을 고려할 수 있습니다.
        // 여기서는 usersDto.getAdminId()를 사용합니다.

        boolean success = adminListService.updateMyInfo(usersDto);

        if (success) {
            redirectAttributes.addFlashAttribute("message", "개인정보가 성공적으로 수정되었습니다.");
            return "redirect:/myInfo"; 
        } else {
            redirectAttributes.addFlashAttribute("error", "개인정보 수정에 실패했습니다.");
            return "redirect:/myInfo"; 
        }
    }
}
	