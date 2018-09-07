package pico.erp.restapi.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.NonceExpiredException;

/**
 * UserCache 를 사용 해 봤지만 AbstractUserDetailsAuthenticationProvider 에서 캐쉬를 저장할때 token 의 principal 로
 * 조회하지만 저장시에는 리턴받은 UserDetails 의 username을 사용하기 때문에 불일치로 인해 캐싱이 동작할 수 없는 구조로 인하여 캐싱처리를
 * UserDetailsServiceImpl#loadUserByUsername 에 추가함
 */
public class FirebaseAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

  @Autowired
  private FirebaseAuth firebaseAuth;

  @Lazy
  @Autowired
  private UserDetailsService userDetailsService;

  @Override
  protected void additionalAuthenticationChecks(UserDetails userDetails,
    UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
  }

  @Override
  protected UserDetails retrieveUser(String username,
    UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    final FirebaseAuthenticationToken authenticationToken = (FirebaseAuthenticationToken) authentication;
    final CompletableFuture<FirebaseToken> future = new CompletableFuture<>();
    try {
      final FirebaseToken token = firebaseAuth
        .verifyIdTokenAsync(authenticationToken.getPrincipal().toString())
        .get();
      return userDetailsService.loadUserByUsername(token.getUid());
    } catch (InterruptedException | ExecutionException e) {
      throw new NonceExpiredException(e.getMessage(), e);
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return (FirebaseAuthenticationToken.class.isAssignableFrom(authentication));
  }

}
