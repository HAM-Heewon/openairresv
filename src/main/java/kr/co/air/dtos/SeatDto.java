package kr.co.air.dtos;

import lombok.Data;

@Data
public class SeatDto {
    private int seatIdx;  
    private int schedIdx;
    private String seatTypeName;   // 좌석 형태명
    private long seatTypePrice;
    private long seatCount;
}
