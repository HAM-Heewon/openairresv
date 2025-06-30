package kr.co.air.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.air.dtos.ReservationRequestDto;
import kr.co.air.dtos.ResvDto;
import kr.co.air.dtos.ResvSettingDto;
import kr.co.air.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService{
	
	private final ReservationMapper rmapper;
	
	@Override
	public List<String> getAvailableDepartures() {
		return rmapper.findAvailableDepartures();
	}
	@Override
	public List<String> getAvailableArrivals(String depart) {
		return rmapper.findAvailableArrivals(depart);
	}
	@Override
	public List<String> getAvailableSeatTypes(String depart, String arrival) {
		return rmapper.findAvailableSeatTypes(depart, arrival);
	}
	@Override
	public void createReservation(ReservationRequestDto req) {
		
		ResvSettingDto priceInfo = rmapper.findPriceInfo(req)
				.orElseThrow(() -> new IllegalStateException("해당 조건의 운항 정보 또는 가격 정보를 찾을 수 없습니다."));
		
        if (req.getDepartDate().isBefore(priceInfo.getBookingStartDate()) || req.getDepartDate().isAfter(priceInfo.getBookingEndDate())) {
            throw new IllegalStateException("선택하신 날짜는 예약 가능한 기간이 아닙니다.");
        }
        
        BigDecimal basePrice = BigDecimal.valueOf(priceInfo.getPricePerPerson());
        BigDecimal adultPrice = basePrice.multiply(BigDecimal.valueOf(req.getAdult()));
        BigDecimal childPrice = basePrice.multiply(new BigDecimal("0.7")).multiply(BigDecimal.valueOf(req.getChild()));
        BigDecimal infantPrice = basePrice.multiply(new BigDecimal("0.5")).multiply(BigDecimal.valueOf(req.getInfant()));
        BigDecimal totalPrice = adultPrice.add(childPrice).add(infantPrice);
        
        ResvDto resvToSave = new ResvDto();
        resvToSave.setAIR_RSPART(req.getAirRsPart());
        resvToSave.setAIR_DEPARTNM(req.getAirDepartNm());
        resvToSave.setAIR_ARRIVNM(req.getAirArrivNm());
        resvToSave.setDEPART_DATE(Date.valueOf(req.getDepartDate()));
        if (req.getArrivalDate() != null) {
            resvToSave.setARRIVAL_DATE(Date.valueOf(req.getArrivalDate()));
        }
        resvToSave.setAIR_SEAT(req.getAirSeat());
        resvToSave.setP_CODE(req.getPCode());
        resvToSave.setADULT((long) req.getAdult());
        resvToSave.setCHILDREN1((long) req.getChild());
        resvToSave.setCHILDREN2((long) req.getInfant());
        resvToSave.setTotalPrice(totalPrice);
        resvToSave.setAIR_USERNAME("홍길동");
        rmapper.saveReservation(resvToSave);
	}
}
