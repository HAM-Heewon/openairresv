package kr.co.air.dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class FlightResvInfoDto {
    private int pricingIdx; // 이 필드는 UPSERT 로직에서 사용되지 않습니다.
    private int scheduleIdx;
    private int seatTypeIdx;
    
    private LocalDate bookingStartDate;
    private LocalDate bookingEndDate;
    private long pricePerPerson;
}
