package kr.co.air.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.air.dtos.FlightResvInfoDto;
import kr.co.air.dtos.ResvGroupDto;
import kr.co.air.mapper.ResvInfoMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResvInfoServiceImpl implements ResvInfoService{
	
	private final ResvInfoMapper rmapper;
	
	@Override
	public List<ResvGroupDto> getSettings(String departure, String configStatus) {
        Map<String, Object> params = new HashMap<>();
        params.put("departureKeyword", departure);
        params.put("configStatus", configStatus);

        return rmapper.findFlightSettings(params);
	}
	
	@Override
	@Transactional
	public void saveResvInfo(List<FlightResvInfoDto> dto) {
		if (dto == null || dto.isEmpty()){
			return;
		}
		rmapper.upsertFlightResvInfo(dto);
	}
	
    @Override
    public long getUnconfiguredCount() {
        return rmapper.countUnconfiguredFlights();
    }

    @Override
    public long getConfiguredCount() {
        return rmapper.countConfiguredFlights();
    }
	
}
