package kr.co.air.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
// 1. SimpleUrlAuthenticationFailureHandler를 상속받도록 변경합니다.
public class LoginFailHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	        AuthenticationException exception) throws IOException, ServletException {
	    String errorMessage;
	    
	    if (exception instanceof BadCredentialsException) {
	        errorMessage = "아이디 또는 비밀번호가 올바르지 않습니다.";
	    } else if (exception instanceof InternalAuthenticationServiceException ||
	               exception instanceof LockedException ||
	               exception instanceof DisabledException) {
	        errorMessage = exception.getMessage();
	    } else {
	        errorMessage = "알 수 없는 이유로 로그인에 실패하였습니다. 관리자에게 문의하세요.";
	    }
	    
	    // URL 인코딩 제거 - 세션에 저장하는 방식으로 변경
	    request.getSession().setAttribute("loginError", errorMessage);
	    setDefaultFailureUrl("/login?error=true");
	    
	    super.onAuthenticationFailure(request, response, exception);
	}
}
