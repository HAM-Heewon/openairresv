package kr.co.air.Service;

import java.util.List;

import kr.co.air.dtos.AirCodeDto;
import kr.co.air.dtos.SchedDto;

public interface ProductService {
	
	List<SchedDto> getAllProduct(String stype, String keyword);
	
	void registerSchedule(SchedDto dto);
	
	List<String> getAirportcode();
	
	List<String> getlinesByAirport(String airportCode);
	
	List<AirCodeDto> getflightName(String airportCode, String airlineName);
	
	void deleteProduct(List<Long> scheduleIdx);
	
	//업데이트
    SchedDto getScheduleById(int scheduleIdx);
    void updateSchedule(SchedDto dto);
}
