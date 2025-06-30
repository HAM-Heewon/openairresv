package kr.co.air.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.air.dtos.AirCodeDto;
import kr.co.air.mapper.AirCodeMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AirCodeService {
	
	private final AirCodeMapper amapper;
	
	//항공편 코드 중복 확인
	public boolean isFlightCodeDuplicate(String flightCode) {
        return amapper.countByFlightCode(flightCode) > 0;
    }
	
	//항공 코드 신규 입력
	@Transactional
    public void createAirCode(AirCodeDto dto) {
        // 프론트엔드에서 받은 "사용함"/"사용안함" 값을 DB에 맞는 'Y'/'N'으로 변환
        String useStatusValue = "사용함".equals(dto.getUseStatus()) ? "Y" : "N";
        dto.setUseStatus(useStatusValue);
        amapper.insertAirCode(dto);
    }
	
	//항공 코드 리스트 출력
	public List<AirCodeDto> getAllCodes(String stype, String keyword){
        Map<String, Object> params = new HashMap<>();
        params.put("searchType", stype);
        params.put("searchKeyword", keyword);
		
        List<AirCodeDto> codes = amapper.findAllCodes(params);
        // 'Y'/'N' 값을 '사용함'/'사용안함'으로 변환하여 화면에 표시
        codes.forEach(code -> {
            if ("Y".equals(code.getUseStatus())) {
                code.setUseStatus("사용함");
            } else {
                code.setUseStatus("사용안함");
            }
        });
        return codes;
	}
	
	//항공 코드 삭제
	@Transactional
    public void deleteAirCodes(List<Integer> airCodeIds) {
        if (airCodeIds != null && !airCodeIds.isEmpty()) {
            amapper.deleteAirCodesByIds(airCodeIds);
        }
    }
	
    public AirCodeDto getAirCodeById(int airIdx) {
        return amapper.findAirCodeById(airIdx);
    }
    
    public void updateAirCode(AirCodeDto dto) {
        amapper.updateAirCode(dto);
    }
}
