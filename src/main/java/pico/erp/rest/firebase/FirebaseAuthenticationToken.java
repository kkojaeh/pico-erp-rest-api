package pico.erp.rest.firebase;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


public class FirebaseAuthenticationToken extends UsernamePasswordAuthenticationToken {

  private static final long serialVersionUID = 1L;

  public FirebaseAuthenticationToken(final String token) {
    super(token, null);
  }

}
