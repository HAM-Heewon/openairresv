package kr.co.air.dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ResvSettingDto {

    // FLIGHT_SEAT_TYPES 정보
    private int seatTypeIdx;
    private String seatTypeName;
    private int seatCount;
    private long seatTypePrice;
    
    // FLIGHT_RESV_INFO 정보
    private LocalDate bookingStartDate;
    private LocalDate bookingEndDate;
    private Long pricePerPerson;
}
