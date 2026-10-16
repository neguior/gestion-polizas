package co.com.segurosbolivar.polizas.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {
    private final String apiKey;
    public ApiKeyInterceptor(@Value("${app.api-key:123456}") String apiKey) { this.apiKey = apiKey; }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // Permite preflight de CORS si en el futuro se habilita CORS.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        String supplied = request.getHeader("x-api-key");
        if (apiKey.equals(supplied)) return true;
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"API key ausente o inválida\"}");
        return false;
    }
}
