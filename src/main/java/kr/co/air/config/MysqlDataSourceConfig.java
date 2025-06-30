package kr.co.air.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


@Configuration
@MapperScan(value = "kr.co.air.mapper", sqlSessionFactoryRef = "sqlfactory2") // 매퍼 인터페이스가 있는 패키지
public class MysqlDataSourceConfig {

	   private final ApplicationContext applicationContext;

	    // ⭐ @Value 어노테이션으로 database.properties 파일에서 각 속성 주입
	    @Value("${spring.mysql.datasource.url}")
	    private String url;

	    @Value("${spring.mysql.datasource.username}")
	    private String username;

	    @Value("${spring.mysql.datasource.password}")
	    private String password;

	    @Value("${spring.mysql.datasource.driver-class-name}")
	    private String driverClassName;

	    // ApplicationContext는 생성자 주입으로 받습니다.
	    public MysqlDataSourceConfig(ApplicationContext applicationContext) {
	        this.applicationContext = applicationContext;
	    }
	    
	    @Bean(name = "Mysql") // @Qualifier("Mysql")과 일치하도록 이름 지정
	    public DataSource mysqlDataSource() { // 메서드 이름은 상관없지만, 헷갈리지 않게 변경
	        return DataSourceBuilder.create()
	                .driverClassName(driverClassName)
	                .url(url)
	                .username(username)
	                .password(password)
	                .build();
	    }
	    
	 // 2. SqlSessionFactory 빈 정의
	    @Bean(name = "sqlfactory2") // SqlSessionFactory 빈 이름은 "sqlfactory2"로 유지
	    public SqlSessionFactory sqlfactory(@Qualifier("Mysql") DataSource ds, ApplicationContext ac) throws Exception {
	        SqlSessionFactoryBean sqlf = new SqlSessionFactoryBean();
	        sqlf.setDataSource(ds);
	        sqlf.setMapperLocations(ac.getResources("classpath:/mapper/*.xml")); // mapper 파일 위치는 application.properties에서 mybatis.mapper-locations에 설정된 대로
	        
	        sqlf.setTypeAliasesPackage("kr.co.air.dtos"); 
	         

	        return sqlf.getObject();
	    }

	    // 3. SqlSessionTemplate 빈 정의
	    @Bean(name = "sqltemplate2") // SqlSessionTemplate 빈 이름은 "sqltemplate2"로 유지
	    public SqlSessionTemplate sqltemplate(@Qualifier("sqlfactory2") SqlSessionFactory sf) throws Exception {
	        SqlSessionTemplate stp = new SqlSessionTemplate(sf);
	        return stp;
	    }
	    
	    // (선택 사항) JdbcTemplate 빈 정의 - TestController에서 사용한다면 필요
	    @Bean(name = "mysqlJdbcTemplate") // TestController에서 이 빈 이름으로 Qualifier 사용
	    public JdbcTemplate mysqlJdbcTemplate(@Qualifier("Mysql") DataSource ds) {
	        return new JdbcTemplate(ds);
	    }
}