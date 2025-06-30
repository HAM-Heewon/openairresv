package kr.co.air.dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReservationRequestDto {
    private String airRsPart;
    private String airDepartNm;
    private String airArrivNm;
    private LocalDate departDate;
    private LocalDate arrivalDate;
    private String airSeat;
    private String pCode;
    private int adult;
    private int child;    // 소아
    private int infant;   // 유아
    private String userName;
}