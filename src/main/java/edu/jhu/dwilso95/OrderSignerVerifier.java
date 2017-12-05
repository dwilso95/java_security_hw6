package edu.jhu.dwilso95;

import java.io.File;
import java.util.List;

import org.w3c.dom.Document;

import com.google.common.collect.Lists;

public class OrderSignerVerifier {

	private static Signer signer;
	private static Verifier verifier;

	/**
	 * one argument: s-sign, v-verify, sv-sign and verify, d-demo
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			usage();
		}
		final boolean sign = args[0].indexOf("s") != -1;
		final boolean verify = args[0].indexOf("v") != -1;
		final File unsignedFile = new File(args[1]);
		final File signedFile = new File(args[2]);

		org.apache.xml.security.Init.init();
		final File schemaFile = new File("src/main/resources/salesPerson.xsd");

		if (sign) {
			final Document document = XMLDocumentUtil.createXMLDocmentFromFile(unsignedFile, schemaFile);
			final List<String> idsToSign = Lists.newArrayList("order1", "commission1");
			signer = new Signer(KeyPairUtil.getKeyPair(new File("src/main/resources/client1.jks"),
					"client1".toCharArray(), "client1"));
			signer.signDocument(document, idsToSign, signedFile);
		}
		if (verify) {
			final Document document = XMLDocumentUtil.createXMLDocmentFromFile(signedFile, schemaFile);
			verifier = new Verifier();
			verifier.verifyDocument(document);
		}
	}

	/**
	 * Show main's usage
	 */
	private static void usage() {
		System.out.println(
				"Java OrderSignerVerifier Usage:\n Choose one of three arguments: ['s','v','sv'] followed by locations of unsigned XML, and then the location to write the signed file.\n Example: 'sv inputFile.xml outputFile.xml'");
	}

}
