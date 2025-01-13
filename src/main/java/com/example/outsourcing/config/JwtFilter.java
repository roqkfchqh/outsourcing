package com.example.outsourcing.config;

import com.example.outsourcing.domain.common.util.JwtUtil;
import com.example.outsourcing.domain.user.entity.User.UserRole;
import com.example.outsourcing.domain.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private static final String[] WHITE_LIST = {"/", "/auth/register", "/auth/login", "/auth/logout"};
    private final JwtUtil jwtUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    /*
    JwtFilter의 역할 -> 공부용으로 적어둔 것이오니 최종 전에 지워야 합니다.
    - 유저가 로그인 되어있는지 확인 (인증)
      */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 요청URL 중, 포트번호와 쿼리 사이의 부분을 가져와서 문자열형 url로 저장 (컨텍스트 경로 + 서블릿 경로)
        String url = httpRequest.getRequestURI();


        if (isWhiteList(url)) {
            chain.doFilter(request, response);
            return;
        }

        // websocket 검증
        if (url.startsWith("/ws")) {
            processWebSocketAuthentication(httpRequest, httpResponse, chain);
            return;
        }

        // httpRequest.getHeader는 클라이언트 측 정보습득을 위해 사용, Authorization는 HttpServlet의 헤더 중, Jwt 토큰을 가져온다.
        String bearerJwt = httpRequest.getHeader("Authorization");

        // 가져온 토큰 값이 존재하지 않으면 httpResponse.sendError를 통하여 서블릿으로 예외처리한다.(상태코드, 메시지)
        // jwt 토큰은 Bearer jwt 형식으로 되어있기 때문에 String은 반드시 Bearer로 시작해야한다. - > 아래 쪽 jwtUtil.substringToken
        // 에서 예외처리 되어있음.
        if (bearerJwt == null) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 필요합니다.");
            return;
        }

        if (JwtUtil.expiredTokenSet.contains(bearerJwt)) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "토큰이 유효하지 않습니다.");
            return;
        }

        try {
      /*
      jwtUtil.substringToken()는 jwt 토큰값에서 앞에 붙은 Bearer을 제거하고 뒤의 토큰 값만 저장되도록 변환해주고, 그것을
      jwtUtil.extractClaims()를 이용하여 Claims 값만 추출한 뒤에 claims에 저장한다. 만약 claims가 비어있다면 sendError를
      통하여 서블릿으로 예외처리 후 종료한다.
       */
            Claims claims = jwtUtil.extractClaims(jwtUtil.substringToken(bearerJwt));
            if (claims == null) {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
                return;
            }

      /*
      setAttribute("키값 문자열",저장객체)를 이용하여 httpRequest에 값을 저장한다.
      userId 키값을 가진 claims의 sub값을 추출해 Long형으로 변환하여 저장
      email 키값의 email값을 저장
      userRole 키값을 가진 userRole값 저장
      */
            httpRequest.setAttribute("userId", Long.parseLong(claims.getSubject()));
            httpRequest.setAttribute("email", claims.get("email"));
            String userRoleString = claims.get("userRole").toString();
            UserRole userRole = UserRole.valueOf(userRoleString);
            httpRequest.setAttribute("userRole", userRole);

            chain.doFilter(request, response);
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("Invalid JWT token, 유효하지 않는 JWT 토큰 입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않는 JWT 토큰입니다.");
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private void processWebSocketAuthentication(HttpServletRequest httpRequest,
        HttpServletResponse httpResponse, FilterChain chain)
        throws IOException, ServletException {
        String token = httpRequest.getParameter("token");
        if (token == null || token.isEmpty()) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "웹소켓 토큰을 찾을 수 없습니다.");
            return;
        }
        Claims claims = jwtUtil.extractClaims(jwtUtil.substringToken(token));
        httpRequest.setAttribute("userId", Long.parseLong(claims.getSubject()));
        chain.doFilter(httpRequest, httpResponse);
    }

    private boolean isWhiteList(String requsetURI) {
        return PatternMatchUtils.simpleMatch(WHITE_LIST, requsetURI);
    }
}
