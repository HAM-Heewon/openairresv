package kr.co.air.dtos;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class FaqDto {
	private int fIdx;
	private String fTitle;
	private String fText;
	private LocalDate fCreatedate;
	private LocalDate fUpdatedate;
	
}	
