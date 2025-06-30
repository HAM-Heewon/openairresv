package kr.co.air.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CompanyInfoDto {
    private Integer cId;
    private String cSitename;
    private String cAdminmail;
    private String cPointuse;
    private Integer cNewmoney;
    private Integer cNewlevel;
    private String cName;
    private String cBusnum;
    private String cMaster;
    private String cMastertel;
    private String cMailordernum;
    private String cTelconum;
    private String cZipcode;
    private String cAddr;
    private String cInfomaster;
    private String cInfomastermail;
    private String cBank;
    private String cAccount;
    private String cCarduse;
    private String cTelcredituse;
    private String cGiftuse;
    private Integer cPointpaymin;
    private Integer cPointpaymax;
    private String cCashreceiptuse;
    private LocalDateTime cUpdatedAt;
}