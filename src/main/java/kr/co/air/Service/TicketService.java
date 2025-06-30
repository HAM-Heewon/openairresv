package kr.co.air.Service;

import java.util.List;

import kr.co.air.dtos.ResvDto;

public interface TicketService {

	List<ResvDto> findReservation(String searchType, String searchKeyword);
	
	void cancelReservation(List<Long> resvId);
}
