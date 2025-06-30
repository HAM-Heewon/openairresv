package kr.co.air.mapper;

import org.apache.ibatis.annotations.Mapper;

import kr.co.air.dtos.CompanyInfoDto;

@Mapper
public interface CompanyMapper {
	
	CompanyInfoDto getCompanyInfo();
	void updateInfo(CompanyInfoDto dto);
}
