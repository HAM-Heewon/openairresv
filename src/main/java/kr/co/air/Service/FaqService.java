package kr.co.air.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.air.dtos.FaqDto;
import kr.co.air.mapper.FaqMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FaqService {
		
	private final FaqMapper fmapper;
	
	public List<FaqDto> getAllFAQ(String keyword){
		Map<String, Object> params = new HashMap<>();
		params.put("keyword", keyword);
		
		List<FaqDto> datas = fmapper.findAllFAQ(params);
		
		return datas;
	};
	
	@Transactional
	public void insertNewFAQ(FaqDto dto) {
		fmapper.newFAQData(dto);
	};
	
	public FaqDto getFAQById(int fIdx) {
		return fmapper.findFAQById(fIdx);
	};
	
	@Transactional
	public void updateFAQ(FaqDto dto) {
		fmapper.updateFAQById(dto);
	};
	
	@Transactional
	public void deleteFAQ(List<Integer> fIdx) {
		if(fIdx != null && !fIdx.isEmpty()) {
			fmapper.deleteFAQById(fIdx);
		}
	};
}
