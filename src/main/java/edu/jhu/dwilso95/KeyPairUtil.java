package edu.jhu.dwilso95;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Static utility class for creating {@link KeyPair}s
 */
public final class KeyPairUtil {

	/**
	 * Private to create static util class
	 */
	private KeyPairUtil() {
		// empty on purpose
	}

	/**
	 * Creates a {@link KeyPair} using the supplied information
	 * 
	 * @param file
	 *            - {@link KeyStore} location
	 * @param password
	 *            - Password for given store
	 * @param alias
	 *            - alias for which to extract key
	 * @return - a {@link KeyPair}
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 */
	public static final KeyPair getKeyPair(final File file, final char[] password, final String alias)
			throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
			UnrecoverableKeyException {
		try (FileInputStream is = new FileInputStream(file);) {
			final KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(is, password);
			return new KeyPair(keystore.getCertificate(alias).getPublicKey(),
					(PrivateKey) keystore.getKey(alias, password));
		}
	}

}
