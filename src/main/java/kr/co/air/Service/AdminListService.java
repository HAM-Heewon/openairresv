package kr.co.air.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import kr.co.air.dtos.UsersDto;
import kr.co.air.mapper.UserMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminListService {
	private final UserMapper usermapper;
	private static final Logger logger = LoggerFactory.getLogger(AdminListService.class);
	
    public Map<String, Object> getAdminPageData(Authentication auth) {
        Map<String, Object> data = new HashMap<>();

        // 1. DB에서 전체 관리자 목록을 조회하여 Map에 담습니다.
        List<UsersDto> adminList = usermapper.findallusers();
        data.put("adminList", adminList);

        // 2. 현재 로그인한 사용자가 'ROLE_관리자' 권한을 가졌는지 확인하여 Map에 담습니다.
        boolean isTopLevelAdmin = false;
        if (auth != null) {
        	logger.info("<<<< [페이지 접근 시점] 현재 세션의 권한 목록: {}", auth.getAuthorities());
            isTopLevelAdmin = auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_관리자"));
        }
        data.put("isTopLevelAdmin", isTopLevelAdmin);

        return data;
    }
}
