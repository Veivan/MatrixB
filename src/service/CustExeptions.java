package service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustExeptions {

	public static class AuthenticationException extends IOException {
		private static final long serialVersionUID = -104987171972968260L;
		private final String ident = "AuthenticationException : ";
		static Logger logger = LoggerFactory.getLogger(AuthenticationException.class);

		AuthenticationException() {
		}

		public AuthenticationException(final Exception cause) {
			super(cause);
			logger.error(ident + cause.getMessage());
			logger.debug(ident + cause.getMessage());
		}

		public AuthenticationException(final String message) {
			super(message);
			logger.error(ident + message);
			logger.debug(ident + message);
		}
	}

}
