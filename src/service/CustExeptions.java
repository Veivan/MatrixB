package service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustExeptions {

	public static class AuthenticationException extends IOException {
		private static final long serialVersionUID = -104987171972968260L;
		private final String ident = "AuthenticationException : ";
	    private int errorCode = -1;
		static Logger logger = LoggerFactory.getLogger(AuthenticationException.class);

		AuthenticationException() {
		}

		public AuthenticationException(final Exception cause) {
			super(cause);
			logger.error(ident + cause.getMessage());
		}

		public AuthenticationException(final String message) {
			super(message);
			logger.error(ident + message);
		}
		
		public AuthenticationException(final String message, final int errorCode) {
			super(message);
			this.errorCode = errorCode;
			logger.error(ident + message);
		}
	    
		public int getErrorCode() {
	        return this.errorCode;
	    }
	}

	public static class ProxyException extends IOException {
		private static final long serialVersionUID = -3327581775717381785L;
		private final String ident = "ProxyException : ";
		static Logger logger = LoggerFactory.getLogger(ProxyException.class);

		ProxyException() {
		}

		public ProxyException(final Exception cause) {
			super(cause);
			logger.error(ident + cause.getMessage());
		}

		public ProxyException(final String message) {
			super(message);
			logger.error(ident + message);
		}		
	}
	
	public static class AuthRetypeException extends IOException {
		private static final long serialVersionUID = -7529966783610067769L;
		private final String ident = "AuthRetypeException : ";
		static Logger logger = LoggerFactory.getLogger(AuthRetypeException.class);

		AuthRetypeException() {
		}

		public AuthRetypeException(final String message) {
			super(message);
			logger.error(ident + message);
		}
	}

}
