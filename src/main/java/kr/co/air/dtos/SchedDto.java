package kr.co.air.dtos;

import java.util.List;

import lombok.Data;

@Data
public class SchedDto {
	private Long scheduleIdx, totalSeats;
	private String airportCd, airlineNm, flightCd, flightNm, departureNm, arrivalNm, flightStatus;
	
	private List<SeatDto> seatTypes;
}
