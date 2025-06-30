package kr.co.air.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.air.dtos.DatalistDto;

@Mapper
public interface NoticeMapper {
	
	List<DatalistDto> findAllNotice();
	
	void insertNotice(DatalistDto dto);
	
	//공지사항 상세페이지
	DatalistDto findById(int eno);
	//조회수 증가
	void incrementView(int eno);
	
    void updateNotice(DatalistDto dto);
    
    void deleteByIds(List<Integer> ids);
    
    List<DatalistDto> findByNos(@Param("enos") List<Integer> enos);
}
