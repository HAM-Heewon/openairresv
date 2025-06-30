package kr.co.air.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DatalistDto {
    private Integer eno;            // 게시글 고유번호
    private String eTitle;			// 게시글 제목
    private Long empno;             // 직원 번호
    private String ename;           // 직원 이름
    private String esubject;        // 게시글 내용 (제목으로 사용)
    private LocalDateTime createDate; // 생성일
    private LocalDateTime updateDate; // 수정일
    
    private String fileName;        // 원본 파일명
    private String fileSavename;    // 저장 파일명
    private String filePath;        // 파일 저장경로
    
    private Integer eView;			//조회수
    
    private Long fileSize;			//파일 크기
}
