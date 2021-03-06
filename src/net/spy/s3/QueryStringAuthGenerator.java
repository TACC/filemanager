//  This software code is made available "AS IS" without warranties of any
//  kind.  You may copy, display, modify and redistribute the software
//  code either by itself or as incorporated into your code; provided that
//  you do not remove any proprietary notices.  Your use of this software
//  code is at your own risk and you waive any claim against Amazon
//  Digital Services, Inc. or its affiliates with respect to your use of
//  this software code. (c) 2006 Amazon Digital Services, Inc. or its
//  affiliates.

package net.spy.s3;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class mimics the behavior of AWSAuthConnection, except instead of
 * actually performing the operation, QueryStringAuthGenerator will return URLs
 * with query string parameters that can be used to do the same thing. These
 * parameters include an expiration date, so that if you hand them off to
 * someone else, they will only work for a limited amount of time.
 */
public class QueryStringAuthGenerator {

	private String awsAccessKeyId;

	private String awsSecretAccessKey;

	private boolean isSecure;

	private String server;

	private int port;

	private Long expiresIn = null;

	private Long expires = null;

	// by default, expire in 1 minute.
	private static final Long DEFAULT_EXPIRES_IN = new Long(60 * 1000);

	public QueryStringAuthGenerator(String awsAccessKeyId,
			String awsSecretAccessKey) {
		this(awsAccessKeyId, awsSecretAccessKey, true);
	}

	public QueryStringAuthGenerator(String awsAccessKeyId,
			String awsSecretAccessKey, boolean isSecure) {
		this(awsAccessKeyId, awsSecretAccessKey, isSecure, Utils.DEFAULT_HOST);
	}

	public QueryStringAuthGenerator(String awsAccessKeyId,
			String awsSecretAccessKey, boolean isSecure, String server) {
		this(awsAccessKeyId, awsSecretAccessKey, isSecure, server,
				isSecure ? Utils.SECURE_PORT : Utils.INSECURE_PORT);
	}

	public QueryStringAuthGenerator(String awsAccessKeyId,
			String awsSecretAccessKey, boolean isSecure, String server, int port) {
		this.awsAccessKeyId = awsAccessKeyId;
		this.awsSecretAccessKey = awsSecretAccessKey;
		this.isSecure = isSecure;
		this.server = server;
		this.port = port;

		this.expiresIn = DEFAULT_EXPIRES_IN;
		this.expires = null;
	}

	public void setExpires(long millisSinceEpoch) {
		expires = new Long(millisSinceEpoch);
		expiresIn = null;
	}

	public void setExpiresIn(long millis) {
		expiresIn = new Long(millis);
		expires = null;
	}

	public String createBucket(String bucket,
			Map<String, List<String>> headers) {
		return generateURL("PUT", bucket, headers);
	}

	public String listBucket(String bucket, String prefix, String marker,
			Integer maxKeys, Map<String, List<String>> headers) {
		return listBucket(bucket, prefix, marker, maxKeys, null, headers);
	}

	public String listBucket(String bucket, String prefix, String marker,
			Integer maxKeys, String delimiter,
			Map<String, List<String>> headers) {
		String path = Utils.pathForListOptions(bucket, prefix, marker, maxKeys,
				delimiter);
		return generateURL("GET", path, headers);
	}

	public String deleteBucket(String bucket,
			Map<String, List<String>> headers) {
		return generateURL("DELETE", bucket, headers);
	}

	public String put(String bucket, String key, S3Object object,
			Map<String, List<String>> headers) {
		Map<String, List<String>> metadata = null;
		if (object != null) {
			metadata = object.metadata;
		}

		return generateURL("PUT", bucket + "/" + Utils.urlencode(key),
				mergeMeta(headers, metadata));
	}

	public String get(String bucket, String key,
			Map<String, List<String>> headers) {
		return generateURL("GET", bucket + "/" + Utils.urlencode(key), headers);
	}

	public String delete(String bucket, String key,
			Map<String, List<String>> headers) {
		return generateURL("DELETE", bucket + "/" + Utils.urlencode(key),
				headers);
	}

	public String getBucketLogging(String bucket,
			Map<String, List<String>> headers) {
		return generateURL("GET", bucket + "?logging", headers);
	}

	public String putBucketLogging(String bucket, String loggingXMLDoc,
			Map<String, List<String>> headers) {
		return generateURL("PUT", bucket + "?logging", headers);
	}

	public String getBucketACL(String bucket,
			Map<String, List<String>> headers) {
		return getACL(bucket, "", headers);
	}

	public String getACL(String bucket, String key,
			Map<String, List<String>> headers) {
		return generateURL("GET", bucket + "/" + Utils.urlencode(key) + "?acl",
				headers);
	}

	public String putBucketACL(String bucket, String aclXMLDoc,
			Map<String, List<String>> headers) {
		return putACL(bucket, "", aclXMLDoc, headers);
	}

	public String putACL(String bucket, String key, String aclXMLDoc,
			Map<String, List<String>> headers) {
		return generateURL("PUT", bucket + "/" + Utils.urlencode(key) + "?acl",
				headers);
	}

	public String listAllMyBuckets(Map<String, List<String>> headers) {
		return generateURL("GET", "", headers);
	}

	public String makeBareURL(String bucket, String key) {
		StringBuffer buffer = new StringBuffer();
		if (this.isSecure) {
			buffer.append("https://");
		} else {
			buffer.append("http://");
		}
		buffer.append(this.server).append(":").append(this.port).append("/")
				.append(bucket);
		buffer.append("/").append(Utils.urlencode(key));

		return buffer.toString();
	}

	private String generateURL(String method, String path,
			Map<String, List<String>> headers) {
		long exp = 0L;
		if (this.expiresIn != null) {
			exp = System.currentTimeMillis() + this.expiresIn.longValue();
		} else if (this.expires != null) {
			exp = this.expires.longValue();
		} else {
			throw new RuntimeException("Illegal expires state");
		}

		// convert to seconds
		exp /= 1000;

		String canonicalString = Utils.makeCanonicalString(method, path,
				headers, "" + exp);
		String encodedCanonical = Utils.encode(this.awsSecretAccessKey,
				canonicalString, true);

		StringBuffer buffer = new StringBuffer();
		if (this.isSecure) {
			buffer.append("https://");
		} else {
			buffer.append("http://");
		}

		buffer.append(this.server).append(":").append(this.port).append("/")
				.append(path);

		if (path.indexOf('?') == -1) {
			// no other query parameters
			buffer.append("?");
		} else {
			// there exist other query parameters
			buffer.append("&");
		}

		buffer.append("Signature=").append(encodedCanonical);
		buffer.append("&Expires=").append(exp);
		buffer.append("&AWSAccessKeyId=").append(this.awsAccessKeyId);

		return buffer.toString();
	}

	private Map<String, List<String>> mergeMeta(
			Map<String, List<String>> headers,
			Map<String, List<String>> metadata) {
		Map<String, List<String>> merged = new TreeMap<String, List<String>>();
		if (headers != null) {
			merged.putAll(headers);
		}
		if (metadata != null) {
			for(Map.Entry<String, List<String>> me : metadata.entrySet()) {
				String key = me.getKey();
				String metadataKey=Utils.METADATA_PREFIX + key;
				if(merged.containsKey(metadataKey)) {
					merged.get(metadataKey).addAll(me.getValue());
				} else {
					merged.put(metadataKey, me.getValue());
				}
			}
		}
		return merged;
	}
}
