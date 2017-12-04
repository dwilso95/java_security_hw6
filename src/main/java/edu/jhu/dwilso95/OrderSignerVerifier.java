package edu.jhu.dwilso95;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class OrderSignerVerifier {

	private KeyPair keyPair; // keypair used to sign & verify

	/** one argument: s-sign, v-verify, sv-sign and verify */
	public static void main(String[] args) throws Exception {
		if (args.length != 3)
			usage();
		boolean sign = args[0].indexOf("s") != -1;
		boolean verify = args[0].indexOf("v") != -1;
		File unsignedFile = new File(args[1]);
		File signedFile = new File(args[2]);
		new OrderSignerVerifier(unsignedFile, signedFile, sign, verify);
	}

	/** Show usage */
	private static void usage() {
		System.out.println("usage: java SignerVerifier {s,v,sv}" + " unsignedFile signedFile");
	}

	/** constructor */
	public OrderSignerVerifier(File unsignedFile, File signedFile, boolean sign, boolean verify) throws Exception {

		org.apache.xml.security.Init.init(); // Init apache library
		getKeys();
		if (sign)
			signFile(unsignedFile, signedFile);
		if (verify)
			verifyFile(signedFile);
	}

	/**
	 * sign an xml document found in unsignedFile place output in signedFile
	 */
	public void signFile(File unsignedFile, File signedFile) throws Exception {
		// Read XML file into memory
		Document doc = createXMLDocmentFromFile(unsignedFile);

		// Create object that will give us the XML signature
		String signedFileURI = signedFile.toURL().toString();
		XMLSignature signature = new XMLSignature(doc, signedFileURI, XMLSignature.ALGO_ID_SIGNATURE_DSA_SHA256);

		// Append <Signature> element to document
		Element root = doc.getDocumentElement();
		root.appendChild(signature.getElement());

		addReferences(signature, doc); // Add references that we'll sign
		addKeyInfoAndSign(signature); // Add key info and sign XML
		// Output XML document to a file
		try (FileOutputStream outputFile = new FileOutputStream(signedFile);) {
			XMLUtils.outputDOMc14nWithComments(doc, outputFile);
		}

		// Show what we signed
		System.out.println("--- Here is what we signed ---");
		for (int i = 0; i < signature.getSignedInfo().getSignedContentLength(); i++)
			System.out.println(new String(signature.getSignedInfo().getSignedContentItem(i)));
	}

	private void getKeys() throws Exception {

		FileInputStream is = new FileInputStream("src/main/resources/client1.jks");

		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(is, "client1".toCharArray());

		String alias = "client1";

		Key key = keystore.getKey(alias, "client1".toCharArray());
		if (key instanceof PrivateKey) {
			// Get certificate of public key
			Certificate cert = keystore.getCertificate(alias);
			// Get public key
			PublicKey publicKey = cert.getPublicKey();

			// Return a key pair
			this.keyPair = new KeyPair(publicKey, (PrivateKey) key);
		}
	}

	private void addReferences(XMLSignature sig, Document doc) throws Exception {
		System.out.println("SignerVerifier.addReferences(): entered");

		// Specify the transformations we want (Enveloping sig, C14N)
		Transforms transforms = new Transforms(doc);
		transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

		// add the references
		final String whatToSign1 = "#order1";
		sig.addDocument(whatToSign1, transforms);
	
		final String whatToSign2 = "#comission1";
		sig.addDocument(whatToSign2, transforms);
	}

	/** Add key information to the signature and sign */
	private void addKeyInfoAndSign(XMLSignature sig) throws Exception {
		System.out.println("SignerVerifier.addKeyInfoAndSign(): entered");
		sig.addKeyInfo(keyPair.getPublic()); // add raw public key
		sig.sign(keyPair.getPrivate()); // sign XML document
	}

	/** Retrieve public key to be used to verify XML signature */
	private PublicKey getPublicKeyForVerification(XMLSignature signature) throws Exception {
		return signature.getKeyInfo().getPublicKey();
	}

	/** Create DOM tree in memory based on contents of given file */
	private Document createXMLDocmentFromFile(File file) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Schema schema = SchemaFactory.newDefaultInstance().newSchema(new File("src/main/resources/salesPerson.xsd"));
		dbf.setSchema(schema);
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.parse(file); // Create in-memory DOM tree from original file
	}

	/** verify a signed XML document in file "signedFile" */
	private void verifyFile(File signedFile) throws Exception {
		System.out.println("About to verify: " + signedFile);
		Document doc = createXMLDocmentFromFile(signedFile);
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

}
