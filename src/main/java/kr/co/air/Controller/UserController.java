package kr.co.air.Controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
	