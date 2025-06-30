package kr.co.air.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.air.dtos.ReservationRequestDto;
import kr.co.air.dtos.ResvDto;
import kr.co.air.dtos.ResvSettingDto;

@Mapper
public interface ReservationMapper {
    List<String> findAvailableDepartures();
    List<String> findAvailableArrivals(String departure);
    List<String> findAvailableSeatTypes(@Param("departure") String depart, @Param("arrival") String arrival);
    // [수정] 반환 타입을 ResvSettingDto로 변경
    Optional<ResvSettingDto> findPriceInfo(ReservationRequestDto dto);
    void saveReservation(ResvDto dto);
}
