package edu.jhu.dwilso95;

import java.security.PublicKey;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Verifier {

	/**
	 * Empty default constructor
	 */
	public Verifier() {
		// nothing to do
	}

	/**
	 * Verify the signatures on the document
	 * 
	 * @param doc
	 *            - {@link Document} to verify
	 * @throws XMLSecurityException
	 * @throws XMLSignatureException
	 */
	public void verifyDocument(final Document doc) throws XMLSignatureException, XMLSecurityException {
		NodeList nodes = doc.getElementsByTagNameNS(org.apache.xml.security.utils.Constants.SignatureSpecNS,
				"Signature");
		if (nodes.getLength() != 0) {
			Element signatureElement = (Element) nodes.item(0);
			XMLSignature signature = new XMLSignature(signatureElement, "");
			PublicKey key = getPublicKeyForVerification(signature);
			System.out.println(
					"Signature verification " + (signature.checkSignatureValue(key) ? "successful" : "failed"));
		}
	}

	/**
	 * Retrieve public key to be used to verify XML signature
	 * 
	 * @throws KeyResolverException
	 */
	private PublicKey getPublicKeyForVerification(XMLSignature signature) throws KeyResolverException {
		return signature.getKeyInfo().getPublicKey();
	}

}
