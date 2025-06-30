package kr.co.air.dtos;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.Data;

@Data
public class ResvDto {
	private Long AIR_RSNO;
    private Long ADULT;
    private Long CHILDREN1;
    private Long CHILDREN2;
	private String AIR_RSPART,AIR_DEPARTNM, AIR_ARRIVNM, AIR_SEAT, P_CODE;
	private Date  DEPART_DATE, ARRIVAL_DATE;
	private BigDecimal totalPrice;
	private String AIR_USERNAME;
    private Date RESERVATION_DATE;
	
    private String AIRPORT_CD;   // 공항코드
    private String AIRLINE_NM;   // 항공사명  
    private String FLIGHT_CD;    // 항공코드
}
