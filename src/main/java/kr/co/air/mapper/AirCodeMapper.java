package kr.co.air.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.air.dtos.AirCodeDto;

@Mapper
public interface AirCodeMapper {

	int countByFlightCode(String flightCode);
	
	void insertAirCode(AirCodeDto dto);
	
	List<AirCodeDto> findAllCodes(Map<String, Object> params);
	
	void deleteAirCodesByIds(List<Integer> airCodeIds);
	
    AirCodeDto findAirCodeById(int airIdx);
    void updateAirCode(AirCodeDto dto);
}
