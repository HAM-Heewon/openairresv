package kr.co.air.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.air.mapper.UserMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgreeMangeService {
	
	private final UserMapper usermapper;
	
	@Transactional
	public void Yadmin(Long adminId) {
		usermapper.updateAdminAgree(adminId,"Y");
	}
	
	@Transactional
	public void Nadmin(Long adminId) {
		usermapper.updateAdminAgree(adminId,"N");
	}
}
