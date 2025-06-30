package kr.co.air.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.air.dtos.ResvDto;
import kr.co.air.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService{

	private final TicketMapper tmapper;
	
	@Override
	public List<ResvDto> findReservation(String searchType, String searchKeyword) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("searchType", searchType);
            params.put("searchKeyword", searchKeyword);
            
            List<ResvDto> result = tmapper.findReservationsByName(params);
            
            return result;
            
        } catch (Exception e) {
            log.error("findReservation 실행 중 오류", e);
            throw e;
        }
    }
	
	@Override
	@Transactional
	public void cancelReservation(List<Long> resvId) {
		if(resvId != null && !resvId.isEmpty()) {
			tmapper.deleteReservationById(resvId);
			
		}
	}
}
