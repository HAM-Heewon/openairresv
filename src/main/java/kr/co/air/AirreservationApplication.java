package kr.co.air;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("kr.co.air.mapper")
public class AirreservationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirreservationApplication.class, args);
	}

}
