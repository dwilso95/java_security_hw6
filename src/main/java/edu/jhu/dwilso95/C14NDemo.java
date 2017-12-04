package edu.jhu.dwilso95;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;

import com.oracle.tools.packager.IOUtils;

public class C14NDemo {

	/**
	 * Main method for executing this demo
	 * 
	 * @param args
	 *            - Requires one argument, XML file to canonicalize
	 * @throws Exception
	 *             - Throws all exceptions. No reason to try to handle any at this
	 *             point.
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			throw new IllegalArgumentException("One and only one argument is allowed. Receieved: " + args.length);
		}

		final File file = getFile(args[0]);
		final byte[] inputXML = readFile(file);

		System.out.println("\n<---------- Input XML ---------->");
		System.out.println(new String(inputXML, Charset.forName("UTF-8")));

		// Initialize Apache XML security libraries
		org.apache.xml.security.Init.init();

		// Get the defualt canonicalizer (C14N, with comments)
		final Canonicalizer canonicalizer = getDefaultCanonicalizer();

		// Canonicalize
		final String canonicalizedXML = new String(canonicalizer.canonicalize(inputXML));

		System.out.println("\n<------ Canonicalized XML ------>");
		System.out.println(canonicalizedXML);

	}

	/**
	 * Returns the file at the given location
	 * 
	 * @param fileName
	 *            - name of file to get
	 * @return the {@link File}
	 * @throws FileNotFoundException
	 *             - thrown if file does not exist
	 */
	private static File getFile(final String fileName) throws FileNotFoundException {
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("Argument is empty. Please input a file to be canonicalized.");
		}

		final File file = new File(fileName);

		if (!file.exists()) {
			throw new FileNotFoundException("File name specified does not exist: " + fileName);
		}

		return file;
	}

	/**
	 * Reads file into byte[], utilizes Google's Guava library
	 * 
	 * @param file
	 *            - {@link File} to read
	 * @return byte[] of file contents
	 */
	private static byte[] readFile(final File file) {
		try {
			return IOUtils.readFully(file);
		} catch (IOException e) {
			throw new RuntimeException("Problem reading file: " + file.getAbsolutePath(), e);
		}
	}

	/**
	 * @return {@link Canonicalizer} of type C14N with comments
	 */
	private static Canonicalizer getDefaultCanonicalizer() {
		try {
			return Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
		} catch (InvalidCanonicalizerException e) {
			throw new RuntimeException("Unable to get Canonicalizer instance", e);
		}
	}
}
