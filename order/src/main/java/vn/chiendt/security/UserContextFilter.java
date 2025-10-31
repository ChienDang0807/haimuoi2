package vn.chiendt.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class UserContextFilter implements Filter {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest http  = (HttpServletRequest) request;

        long userId = Long.parseLong(http.getHeader(USER_ID_HEADER));

        UserContext.setUserId(userId);

        try {
            chain.doFilter(request, response);
        }finally {
            UserContext.clear();
        }
    }
}
