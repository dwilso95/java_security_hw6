package edu.jhu.dwilso95;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.util.List;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Uses a {@link KeyPair} to sign a {@link Document} and writes it to a given
 * output file.
 */
public class Signer {

	private final KeyPair keyPair;

	/**
	 * Creates a {@link java.security.Signer} which uses the given {@link KeyPair}
	 * 
	 * @param keyPair
	 */
	public Signer(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	/**
	 * Sign an xml document found in unsignedFile place output in signedFile
	 * 
	 * @param doc
	 *            - {@link Document} to sign
	 * @param signedFile
	 *            - File location to write signed document
	 * @throws Exception
	 */
	public void signDocument(final Document doc, final List<String> idsToSign, final File signedFile) throws Exception {

		@SuppressWarnings("deprecation")
		String signedFileURI = signedFile.toURL().toString();
		XMLSignature signature = new XMLSignature(doc, signedFileURI, XMLSignature.ALGO_ID_SIGNATURE_DSA_SHA256);

		Element root = doc.getDocumentElement();
		root.appendChild(signature.getElement());

		addReferences(signature, doc, idsToSign);
		addKeyInfoAndSign(signature);
		try (FileOutputStream outputFile = new FileOutputStream(signedFile);) {
			XMLUtils.outputDOMc14nWithComments(doc, outputFile);
		}

		System.out.println("--- Here is what we signed ---");
		for (int i = 0; i < signature.getSignedInfo().getSignedContentLength(); i++)
			System.out.println(new String(signature.getSignedInfo().getSignedContentItem(i)));
	}

	/**
	 * Add references to sign. This signs the given ids.
	 * 
	 * @param signature
	 * @param document
	 * @throws TransformationException
	 * @throws XMLSignatureException
	 */
	private void addReferences(final XMLSignature signature, final Document document, final List<String> idsToSign)
			throws TransformationException, XMLSignatureException {
		final Transforms transforms = new Transforms(document);
		transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

		for (final String idToSign : idsToSign) {
			signature.addDocument("#" + idToSign, transforms);
		}

	}

	/**
	 * Add key information to the signature and sign
	 * 
	 * @param signature
	 *            - XMLSignature to use for signing
	 * @throws XMLSignatureException
	 */
	private void addKeyInfoAndSign(final XMLSignature signature) throws XMLSignatureException {
		System.out.println("SignerVerifier.addKeyInfoAndSign(): entered");
		signature.addKeyInfo(keyPair.getPublic());
		signature.sign(keyPair.getPrivate());
	}

}
