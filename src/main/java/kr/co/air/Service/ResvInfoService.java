package kr.co.air.Service;

import java.util.List;

import kr.co.air.dtos.FlightResvInfoDto;
import kr.co.air.dtos.ResvGroupDto;

public interface ResvInfoService {
	
	List<ResvGroupDto> getSettings(String departure, String configStatus);
	void saveResvInfo(List<FlightResvInfoDto> dto);
	
    long getUnconfiguredCount();
    long getConfiguredCount();
}
