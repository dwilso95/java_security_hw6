package edu.jhu.dwilso95;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Static utility class for generating {@link Document}s from given files
 */
public final class XMLDocumentUtil {

	/**
	 * Private to create static util class
	 */
	private XMLDocumentUtil() {
		// Empty on purpose
	}

	/**
	 * Create Document from given {@link File}
	 * 
	 * @param xmlFile
	 *            - File from which to read
	 * @param -
	 *            File from which to read {@link Schema}
	 * @return - Document
	 * @throws SAXException,
	 *             IOException
	 * @throws ParserConfigurationException
	 */
	public static final Document createXMLDocmentFromFile(final File xmlFile, final File schemaFile)
			throws SAXException, IOException, ParserConfigurationException {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setSchema(SchemaFactory.newDefaultInstance().newSchema(schemaFile));
		return dbf.newDocumentBuilder().parse(xmlFile);
	}

}
