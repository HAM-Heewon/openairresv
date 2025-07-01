package kr.co.air.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SeatDto {
    private int seatIdx;  
    private int schedIdx;
    private String seatTypeName;   // 좌석 형태명
    private BigDecimal seatTypePrice;
    private long seatCount;
}
