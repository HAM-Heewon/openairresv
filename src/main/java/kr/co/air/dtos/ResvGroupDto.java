package kr.co.air.dtos;

import java.util.List;

import lombok.Data;

@Data
public class ResvGroupDto {
    private int scheduleIdx;
    private String airportCd;
    private String airlineNm;
    private String flightCd;
    private String flightNm;
    private String departureNm;
    private String arrivalNm;
    
    private List<ResvSettingDto> seatSettings;
}
