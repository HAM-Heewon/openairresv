package kr.co.air.dtos;

import lombok.Data;

@Data
public class UsersDto {
    private Long adIdx;         // AD_IDX
    private String adminId;     // ADMIN_ID
    private String adminPw;     // ADMIN_PW
    private String adminNm;     // ADMIN_NM
    private String adminTel;    // ADMIN_TEL
    private String adminEmail;  // ADMIN_EMAIL
    private String department;  // DEPARTMENT
    private Integer positionCode;   // POSITION_CODE
    private String positionName;// POSITION_NAME
    private String adminAgree;	//ADMIN_AGREE
}
