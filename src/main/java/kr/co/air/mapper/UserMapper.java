package kr.co.air.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.air.dtos.UsersDto;

@Mapper
public interface UserMapper {
	
	//로그인
	UsersDto findByUsername(String adminId);
	List<UsersDto> findallusers();
	
	//관리자 승인
	void updateAdminAgree(@Param("adIdx") Long adIdx, @Param("adminAgree") String adminAgree);
	
	//관리자 회원가입
	void insertAdmin(UsersDto dto);
	//아이디 중복 확인
	int checkid(String adminId);
	
	//공지사항 : 사용자이름으로 idx 조회
	Long findAdIdx(String adminId);
	
	UsersDto findalldata(String adminId);
	void updateMyAdmin(UsersDto dto);

}
