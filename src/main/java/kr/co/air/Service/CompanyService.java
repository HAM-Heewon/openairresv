package kr.co.air.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.air.dtos.CompanyInfoDto;
import kr.co.air.mapper.CompanyMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {
	
	private final CompanyMapper mapper;
	
	public CompanyInfoDto getInfo() {
		return mapper.getCompanyInfo();
	}
	
	@Transactional
	public void updateInfo(CompanyInfoDto dto) {
		mapper.updateInfo(dto);
	}
}
