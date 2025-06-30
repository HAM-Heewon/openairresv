package kr.co.air.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.air.dtos.AirCodeDto;
import kr.co.air.dtos.SchedDto;
import kr.co.air.dtos.SeatDto;

@Mapper
public interface ProductMapper {

	
	//항공편 리스트 출력
	List<SchedDto> findAllProduct(Map<String, Object> params);
	
	//항공 편 등록
    void insertFlightSchedule(SchedDto schedDto);
    
    //항공편 별 좌석 등록
    void insertFlightSeatTypes(List<SeatDto> seatTypes);
    
    //공항 코드 출력
    List<String> findDistinctAirportCodes();
    
    //공항에등록된 항공사 출력
    List<String> findAirlinesByAirport(@Param("airportCode") String airportCode);
    
    //항공편 출력
    List<AirCodeDto> findFlightInfo(@Param("airportCode") String airportCode, @Param("airlineName") String airlineName);
    
    //항공편 삭제
    void deleteitemById(List<Long> scheduleIdx);
    
    SchedDto findScheduleById(int scheduleIdx);
    void updateFlightSchedule(SchedDto dto);
    void deleteSeatTypesByScheduleId(int scheduleIdx);
}
