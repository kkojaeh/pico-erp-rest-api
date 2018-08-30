package pico.erp.restapiserver.firebase;

import static org.springframework.util.StringUtils.isEmpty;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class FirebaseAuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter {

  private final static String TOKEN_HEADER = "X-Firebase-Auth";

  private final static String TOKEN_PARAMETER = "_firebase_auth";

  public FirebaseAuthenticationTokenFilter() {
    super("/**");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
    HttpServletResponse response) {
    String token = request.getHeader(TOKEN_HEADER);
    if (token == null) {
      token = request.getParameter(TOKEN_PARAMETER);
    }
    if (isEmpty(token)) {
      throw new RuntimeException("Invaild auth token");
    }
    return getAuthenticationManager().authenticate(new FirebaseAuthenticationToken(token));
  }

  @Override
  protected boolean requiresAuthentication(HttpServletRequest request,
    HttpServletResponse response) {
    String token = request.getHeader(TOKEN_HEADER);
    if (token == null) {
      token = request.getParameter(TOKEN_PARAMETER);
    }
    return !isEmpty(token);
  }

  /**
   * Make sure the rest of the filterchain is satisfied
   */
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
    FilterChain chain, Authentication authResult)
    throws IOException, ServletException {
    super.successfulAuthentication(request, response, chain, authResult);

    // As this authentication is in HTTP header, after success we need convert continue the request normally
    // and return the response as if the resource was not secured at all
    chain.doFilter(request, response);
  }
}
