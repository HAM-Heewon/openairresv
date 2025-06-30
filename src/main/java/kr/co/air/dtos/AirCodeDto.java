package kr.co.air.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AirCodeDto {
    private int airIdx;            // 항공코드 고유번호
    private String airportCode;    // 공항 코드 
    private String airlineName;    // 항공사명 
    private String flightCode;     // 항공편 코드 
    private String flightName;     // 항공편 명
    private String useStatus;      // 사용 유무 
    private LocalDateTime createDate; // 생성일시
}
