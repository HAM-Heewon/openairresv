package kr.co.air.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.air.dtos.FaqDto;

@Mapper
public interface FaqMapper {
	
	List<FaqDto> findAllFAQ(Map<String, Object> params);
	
	void newFAQData(FaqDto dto);
	
	FaqDto findFAQById(int fIdx);
	
	void updateFAQById(FaqDto dto);
	
	void deleteFAQById(List<Integer> id);
}
