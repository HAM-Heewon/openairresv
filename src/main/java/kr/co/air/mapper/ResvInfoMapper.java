package kr.co.air.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.co.air.dtos.FlightResvInfoDto;
import kr.co.air.dtos.ResvGroupDto;

@Mapper
public interface ResvInfoMapper {

	List<ResvGroupDto> findFlightSettings(Map<String, Object> params);
	void upsertFlightResvInfo(List<FlightResvInfoDto> resvInfos);
    
    long countUnconfiguredFlights();
    long countConfiguredFlights();
}
