package kr.co.air.Service;

import java.util.List;

import kr.co.air.dtos.ReservationRequestDto;

public interface ReservationService {
	
    List<String> getAvailableDepartures();
    List<String> getAvailableArrivals(String depart);
    List<String> getAvailableSeatTypes(String depart, String arrival);
    void createReservation(ReservationRequestDto req);
}
