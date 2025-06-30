package kr.co.air.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.air.dtos.ResvDto;

@Mapper
public interface TicketMapper {
	
	List<ResvDto> findReservationsByName(Map<String, Object> params);
	
	void deleteReservationById(List<Long> resvId);

}
