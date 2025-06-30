package kr.co.air.dtos;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CurrentDto extends User {
	private final UsersDto usersdto;
	
    public CurrentDto(UsersDto usersdto, Collection<? extends GrantedAuthority> auth) {
        super(usersdto.getAdminId(), usersdto.getAdminPw(), auth);
        this.usersdto = usersdto;
    }

    public UsersDto getUsersDto() {
        return usersdto;
    }
}
