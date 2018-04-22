package blue.hive.security.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

/**
*
* DAO를 통한 인증 프로바이더
*
* org.springframework.security.authentication.dao.DaoAuthenticationProvider을 수정
*
*  - deprecated된 org.springframework.security.authentication.encoding.PasswordEncoder를
*    org.springframework.security.crypto.password.PasswordEncoder 계열로 변경
*    - 기본 인코더를 PlaintextPasswordEncoder에서 NoOpPasswordEncoder로 변경
*    - 불필요 Salt 관리 제거
*
* 필요시 additionalAuthenticationChecks에 추가로직 구현 원하는 추가 예외를 Throw 처리 가능
*
* An {@link AuthenticationProvider} implementation that retrieves user details from a
* {@link UserDetailsService}.
*
* @author DongMan Kwon <dmkwon@intellicode.co.kr>
*/
public class BHiveDaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
	
	// ~ Static fields/initializers
	// =====================================================================================

	/**
	 * The plaintext password used to perform
	 * {@link PasswordEncoder#isPasswordValid(String, String, Object)} on when
	 * the user is not found to avoid SEC-2056.
	 */
	private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

	// ~ Instance fields
	// ================================================================================================

	private PasswordEncoder passwordEncoder;

	/**
	 * The password used to perform
	 * {@link PasswordEncoder#isPasswordValid(String, String, Object)} on when
	 * the user is not found to avoid SEC-2056. This is necessary, because some
	 * {@link PasswordEncoder} implementations will short circuit if the
	 * password is not in a valid format.
	 */
	private String userNotFoundEncodedPassword;
	
	private UserDetailsService userDetailsService;

	public BHiveDaoAuthenticationProvider() {
		setPasswordEncoder(NoOpPasswordEncoder.getInstance());
	}

	// ~ Methods
	// ========================================================================================================

	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");
			throw new BadCredentialsException("Authentication failed: no credentials provided");
		}
		
		String presentedPassword = authentication.getCredentials().toString();
		if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
			logger.debug("Authentication failed: password does not match stored value");
			throw new BadCredentialsException("아이디 또는 비밀번호를 다시 확인하세요.<br>등록되지 않은 아이디 이거나, 아이디 또는 비밀번호를 잘못입력하셨습니다.");
		}
	}

	protected void doAfterPropertiesSet() throws Exception {
		Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
	}

	protected final UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		UserDetails loadedUser;

		try {
			loadedUser = this.getUserDetailsService().loadUserByUsername(username);
		} catch (UsernameNotFoundException notFound) {
			if (authentication.getCredentials() != null) {
				String presentedPassword = authentication.getCredentials().toString();
				passwordEncoder.matches(presentedPassword, userNotFoundEncodedPassword);
			}
			throw notFound;
		} catch (Exception repositoryProblem) {
			throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
		}

		if (loadedUser == null) {
			throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
		}

		return loadedUser;
	}

	/**
	 * Sets the PasswordEncoder instance to be used to encode and validate
	 * passwords. If not set, the password will be compared as plain text.
	 * <p>
	 * For systems which are already using salted password which are encoded
	 * with a previous release, the encoder should be of type
	 * {@code org.springframework.security.authentication.encoding.PasswordEncoder}
	 * . Otherwise, the recommended approach is to use
	 * {@code org.springframework.security.crypto.password.PasswordEncoder}.
	 *
	 * @param passwordEncoder
	 *            must be an instance of one of the {@code PasswordEncoder}
	 *            types.
	 */
	public void setPasswordEncoder(Object passwordEncoder) {
		Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");

		if (passwordEncoder instanceof PasswordEncoder) {
			setPasswordEncoder((PasswordEncoder) passwordEncoder);
			return;
		}

		throw new IllegalArgumentException("passwordEncoder must be a PasswordEncoder instance");
	}

	private void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
		this.userNotFoundEncodedPassword = passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
		this.passwordEncoder = passwordEncoder;
	}

	protected PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	protected UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

}
