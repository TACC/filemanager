//  This software code is made available "AS IS" without warranties of any
//  kind.  You may copy, display, modify and redistribute the software
//  code either by itself or as incorporated into your code; provided that
//  you do not remove any proprietary notices.  Your use of this software
//  code is at your own risk and you waive any claim against Amazon
//  Digital Services, Inc. or its affiliates with respect to your use of
//  this software code. (c) 2006 Amazon Digital Services, Inc. or its
//  affiliates.

package net.spy.s3;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class Utils {
	static final String METADATA_PREFIX = "x-amz-meta-";

	static final String AMAZON_HEADER_PREFIX = "x-amz-";

	static final String ALTERNATIVE_DATE_HEADER = "x-amz-date";

	public static final String DEFAULT_HOST = "s3.amazonaws.com";

	static final int SECURE_PORT = 443;

	static final int INSECURE_PORT = 80;

	/**
	 * HMAC/SHA1 Algorithm per RFC 2104.
	 */
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	static String makeCanonicalString(String method, String resource,
			Map<String, List<String>> headers) {
		return makeCanonicalString(method, resource, headers, null);
	}

	/**
	 * Calculate the canonical string. When expires is non-null, it will be used
	 * instead of the Date header.
	 */
	static String makeCanonicalString(String method, String resource,
			Map<String, List<String>> headers, String expires) {
		StringBuffer buf = new StringBuffer();
		buf.append(method + "\n");

		// Add all interesting headers to a list, then sort them. "Interesting"
		// is defined as Content-MD5, Content-Type, Date, and x-amz-
		SortedMap<String, String> interestingHeaders = new TreeMap<String, String>();
		if (headers != null) {
			for (Map.Entry<String, List<String>> me : headers.entrySet()) {
				String key = me.getKey();
				if (key == null) {
					continue;
				}
				String lk = key.toLowerCase();

				// Ignore any headers that are not particularly interesting.
				if (lk.equals("content-type") || lk.equals("content-md5")
						|| lk.equals("date")
						|| lk.startsWith(AMAZON_HEADER_PREFIX)) {
					interestingHeaders.put(lk, concatenateList(me.getValue()));
				}
			}
		}

		if (interestingHeaders.containsKey(ALTERNATIVE_DATE_HEADER)) {
			interestingHeaders.put("date", "");
		}

		// if the expires is non-null, use that for the date field. this
		// trumps the x-amz-date behavior.
		if (expires != null) {
			interestingHeaders.put("date", expires);
		}

		// these headers require that we still put a new line in after them,
		// even if they don't exist.
		if (!interestingHeaders.containsKey("content-type")) {
			interestingHeaders.put("content-type", "");
		}
		if (!interestingHeaders.containsKey("content-md5")) {
			interestingHeaders.put("content-md5", "");
		}

		// Finally, add all the interesting headers (i.e.: all that startwith
		// x-amz- ;-))
		for(Map.Entry<String, String> me : interestingHeaders.entrySet()) {
			String key = me.getKey();
			if (key.startsWith(AMAZON_HEADER_PREFIX)) {
				buf.append(key).append(':').append(me.getValue());
			} else {
				buf.append(me.getValue());
			}
			buf.append("\n");
		}

		// don't include the query parameters...
		int queryIndex = resource.indexOf('?');
		if (queryIndex == -1) {
			buf.append("/" + resource);
		} else {
			buf.append("/" + resource.substring(0, queryIndex));
		}

		// ...unless there is an acl or torrent parameter
		if (resource.matches(".*[&?]acl($|=|&).*")) {
			buf.append("?acl");
		} else if (resource.matches(".*[&?]torrent($|=|&).*")) {
			buf.append("?torrent");
		} else if (resource.matches(".*[&?]logging($|=|&).*")) {
			buf.append("?logging");
		}

		return buf.toString();
	}

	/**
	 * Calculate the HMAC/SHA1 on a string.
	 * 
	 * @param data
	 *            Data to sign
	 * @param passcode
	 *            Passcode to sign it with
	 * @return Signature
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm does not exist. Unlikely
	 * @throws InvalidKeyException
	 *             If the key is invalid.
	 */
	static String encode(String awsSecretAccessKey, String canonicalString,
			boolean urlencode) {
		// The following HMAC/SHA1 code for the signature is taken from the
		// AWS Platform's implementation of RFC2104
		// (amazon.webservices.common.Signature)
		//
		// Acquire an HMAC/SHA1 from the raw key bytes.
		SecretKeySpec signingKey = new SecretKeySpec(awsSecretAccessKey
				.getBytes(), HMAC_SHA1_ALGORITHM);

		// Acquire the MAC instance and initialize with the signing key.
		Mac mac = null;
		try {
			mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			// should not happen
			throw new RuntimeException("Could not find sha1 algorithm", e);
		}
		try {
			mac.init(signingKey);
		} catch (InvalidKeyException e) {
			// also should not happen
			throw new RuntimeException(
					"Could not initialize the MAC algorithm", e);
		}

		// Compute the HMAC on the digest, and set it.
		String b64 = new String(Base64.encodeBase64(
				mac.doFinal(canonicalString.getBytes())));

		if (urlencode) {
			return urlencode(b64);
		} else {
			return b64;
		}
	}

	static String pathForListOptions(String bucket, String prefix,
			String marker, Integer maxKeys) {
		return pathForListOptions(bucket, prefix, marker, maxKeys, null);
	}

	static String pathForListOptions(String bucket, String prefix,
			String marker, Integer maxKeys, String delimiter) {
		StringBuffer path = new StringBuffer(bucket);
		path.append("?");

		// these three params must be url encoded
		if (prefix != null)
			path.append("prefix=" + urlencode(prefix) + "&");
		if (marker != null)
			path.append("marker=" + urlencode(marker) + "&");
		if (delimiter != null)
			path.append("delimiter=" + urlencode(delimiter) + "&");

		if (maxKeys != null)
			path.append("max-keys=" + maxKeys + "&");
		path.deleteCharAt(path.length() - 1); // we've always added exactly
												// one too many chars

		return path.toString();
	}

	static String urlencode(String unencoded) {
		try {
			return URLEncoder.encode(unencoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// should never happen
			throw new RuntimeException("Could not url encode to UTF-8", e);
		}
	}

	static XMLReader createXMLReader() {
		try {
			return XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			// oops, lets try doing this (needed in 1.4)
			System.setProperty("org.xml.sax.driver",
					"org.apache.crimson.parser.XMLReaderImpl");
		}
		try {
			// try once more
			return XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			throw new RuntimeException(
					"Couldn't initialize a sax driver for the XMLReader");
		}
	}

	/**
	 * Concatenates a bunch of header values, seperating them with a comma.
	 * 
	 * @param values
	 *            List of header values.
	 * @return String of all headers, with commas.
	 */
	private static String concatenateList(List<String> values) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0, size = values.size(); i < size; ++i) {
			buf.append(values.get(i).replaceAll("\n", "").trim());
			if (i != (size - 1)) {
				buf.append(",");
			}
		}
		return buf.toString();
	}
}
