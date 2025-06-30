package kr.co.air.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.air.dtos.UsersDto;
import kr.co.air.mapper.UserMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterService {
	
	private final UserMapper mapper;
	private final PasswordEncoder Pencoder;
	
	public boolean checkId(String adminId) {
		return mapper.checkid(adminId) == 0;
	}
	
	@Transactional
	public void registerNew(UsersDto dto) {
		
		//비번 암호화
		dto.setAdminPw(Pencoder.encode(dto.getAdminPw()));
		
		//직책이름-코드변환
		dto.setPositionCode(positonencode(dto.getPositionName()));
		
		dto.setAdminAgree("P");
		
		mapper.insertAdmin(dto);
	}
	
	//직책 이름을 직책 코드로 변환하여 전달
	private Integer positonencode(String positionNm) {
		if(positionNm == null) return null;
		
		switch (positionNm) {
        case "사원": return 1;
        case "대리": return 2;
        case "과장": return 3;
        case "차장": return 4;
        case "부장": return 5;
        default: return 1; // 기타 또는 예외 처리

		}
	}
	
}
