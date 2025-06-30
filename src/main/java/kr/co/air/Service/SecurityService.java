package kr.co.air.Service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import kr.co.air.dtos.CurrentDto;
import kr.co.air.dtos.UsersDto;
import kr.co.air.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class SecurityService implements UserDetailsService {

    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        UsersDto user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        //계정 상태 검사
        if ("N".equals(user.getAdminAgree())) {
            throw new LockedException("승인이 거부된 계정입니다. 관리자에게 문의하세요.");
        } else if ("P".equals(user.getAdminAgree())) {
            throw new DisabledException("승인 대기중인 계정입니다. 관리자에게 문의하세요.");
        }
        
        String positionName = user.getPositionName();
        String role = userRole(positionName);
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
        
        return new CurrentDto(user, authorities);
    }

    private String userRole(String positionName) {
        if (positionName == null || positionName.trim().isEmpty()) {
            return "ROLE_EMPLOYEE";
        }
        switch (positionName.trim()) {
            case "관리자": return "ROLE_관리자";
            case "부장": return "ROLE_MANAGER";
            case "차장":
            case "과장": return "ROLE_SUPERVISOR";
            default: return "ROLE_EMPLOYEE";
        }
    }
}