package com.dongwoo.SQM.common.util;


import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

@Slf4j
public class JWTSecretKeyUtils {
	private static byte[] SECRET_KEY_256;

	/**
	 * Generate random 256-bit (32-byte) shared secret
	 * @return
	 */
	public static byte[] getSecretKey256() {
		if (null == SECRET_KEY_256) {
			SecureRandom random = new SecureRandom();
			SECRET_KEY_256 = new byte[32];
			random.nextBytes(SECRET_KEY_256);

			log.info(">>>>> [ CREATE_SECRET_KEY_256 ]");
		}
		return SECRET_KEY_256;
	}

	/**
	 * Refresh random 256-bit (32-byte) shared secret
	 */
	public static void refreshSecretKey256() {
		SecureRandom random = new SecureRandom();
		SECRET_KEY_256 = new byte[32];
		random.nextBytes(SECRET_KEY_256);

		// logger.info(">>>>> [ REFRESH_SECRET_KEY_256 : \"" + SECRET_KEY_256 + "\" ]");
		log.info(">>>>> [ REFRESH_SECRET_KEY_256 ]");
	}

}
