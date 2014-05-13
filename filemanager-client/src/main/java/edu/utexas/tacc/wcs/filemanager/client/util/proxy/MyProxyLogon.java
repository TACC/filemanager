/**
 * Copyright (c) 2014, Texas Advanced Computing Center
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the University of Texas at Austin nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS AS IS
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package edu.utexas.tacc.wcs.filemanager.client.util.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;
import org.globus.gsi.CertUtil;
import org.globus.gsi.X509Credential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.myproxy.MyProxy;
import org.globus.myproxy.MyProxyException;
import org.gridforum.jgss.ExtendedGSSCredential;
import org.gridforum.jgss.ExtendedGSSManager;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.testng.Assert;

import edu.utexas.tacc.wcs.filemanager.client.ConfigOperation;

/**
 * The MyProxyLogon class provides an interface for retrieving credentials from
 * a MyProxy server.
 * <p>
 * First, use <code>setHost</code>, <code>setPort</code>,
 * <code>setUsername</code>, <code>setPassphrase</code>,
 * <code>setCredentialName</code>, <code>setLifetime</code> and
 * <code>requestTrustRoots</code> to configure. Then call <code>connect</code>,
 * <code>logon</code>, <code>getCredentials</code>, then <code>disconnect</code>
 * . Use <code>getCertificates</code> and <code>getPrivateKey</code> to access
 * the retrieved credentials, or <code>writeProxyFile</code> or
 * <code>saveCredentialsToFile</code> to write them to a file. Use
 * <code>writeTrustRoots</code>, <code>getTrustedCAs</code>,
 * <code>getCRLs</code>, <code>getTrustRootData</code>, and
 * <code>getTrustRootFilenames</code> for trust root information.
 * 
 * @version 1.0
 * @see <a href="http://myproxy.ncsa.uiuc.edu/">MyProxy Project Home Page</a>
 */
public class MyProxyLogon {
	static Logger logger = Logger.getLogger(MyProxyLogon.class);
	public final static String version = "1.0";
	public final static String BouncyCastleLicense = org.bouncycastle.LICENSE.licenseText;

	protected enum State {
		READY, CONNECTED, LOGGEDON, DONE
	}

	private class MyTrustManager implements X509TrustManager 
	{
		public X509Certificate[] getAcceptedIssuers() 
		{
			X509Certificate[] issuers = null;
			String certDirPath = MyProxyLogon.getExistingTrustRootPath();
			if (certDirPath == null) {
				return null;
			}
			File dir = new File(certDirPath);
			if (!dir.isDirectory()) {
				return null;
			}
			String[] certFilenames = dir.list();
			String[] certData = new String[certFilenames.length];
			for (int i = 0; i < certFilenames.length; i++) {
				try {
					FileInputStream fileStream = new FileInputStream(
							certDirPath + File.separator + certFilenames[i]);
					byte[] buffer = new byte[fileStream.available()];
					fileStream.read(buffer);
					certData[i] = new String(buffer);
				} catch (Exception e) {
					// ignore
				}
			}
			try {
				issuers = getX509CertsFromStringList(certData, certFilenames);
			} catch (Exception e) {
				// ignore
			}
			return issuers;
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType)
				throws CertificateException {
			throw new CertificateException(
					"checkClientTrusted not implemented by trust manager");
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType)
				throws CertificateException {
			checkServerCertPath(certs);
			checkServerDN(certs[0]);
		}

		private void checkServerCertPath(X509Certificate[] certs)
				throws CertificateException {
			try {
				CertPathValidator validator = CertPathValidator
						.getInstance(CertPathValidator.getDefaultType());
				CertificateFactory certFactory = CertificateFactory
						.getInstance("X.509");
				CertPath certPath = certFactory.generateCertPath(Arrays
						.asList(certs));
				X509Certificate[] acceptedIssuers = getAcceptedIssuers();
				if (acceptedIssuers == null) {
					String certDir = MyProxyLogon.getExistingTrustRootPath();
					if (certDir != null && !requestTrustRoots) {
						throw new CertificateException(
								"no CA certificates found in " + certDir);
					} else if (!requestTrustRoots) {
						throw new CertificateException(
								"no CA certificates directory found");
					}
					logger.info("No trusted CAs configured -- bootstrapping trust from MyProxy server at " + host);
					acceptedIssuers = new X509Certificate[1];
					acceptedIssuers[0] = certs[certs.length - 1];
				}
				Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>(
						acceptedIssuers.length);
				for (int i = 0; i < acceptedIssuers.length; i++) {
					TrustAnchor ta = new TrustAnchor(acceptedIssuers[i], null);
					trustAnchors.add(ta);
				}
				PKIXParameters pkixParameters = new PKIXParameters(trustAnchors);
				pkixParameters.setRevocationEnabled(false);
				validator.validate(certPath, pkixParameters);
			} catch (CertificateException e) {
				throw e;
			} catch (GeneralSecurityException e) {
				throw new CertificateException(e);
			}
		}

		private void checkServerDN(X509Certificate cert)
		throws CertificateException 
		{
			String subject = cert.getSubjectX500Principal().getName();
			int index = subject.indexOf("CN=");
			if (index == -1) {
				throw new CertificateException("Server certificate subject ("
						+ subject + "does not contain a CN component.");
			}
			String CN = subject.substring(index + 3);
			index = CN.indexOf(',');
			if (index >= 0) {
				CN = CN.substring(0, index);
			}
			if ((index = CN.indexOf('/')) >= 0) {
				String service = CN.substring(0, index);
				CN = CN.substring(index + 1);
				if (!service.equals("host") && !service.equals("myproxy")) {
					throw new CertificateException(
							"Server certificate subject CN contains unknown service element: "
									+ subject);
				}
			}
			String myHostname = host;
			if (myHostname.equals("localhost")) {
				try {
					myHostname = InetAddress.getLocalHost().getHostName();
				} catch (Exception e) {
					// ignore
				}
			}
			if (!CN.equals(myHostname)) {
				throw new CertificateException(
						"Server certificate subject CN (" + CN
								+ ") does not match server hostname (" + host
								+ ").");
			}
		}
	}

	private final static int b64linelen = 64;
	private final static String X509_USER_PROXY_FILE = "x509up_u";
	private final static String VERSION = "VERSION=MYPROXYv2";
	private final static String GETCOMMAND = "COMMAND=0";
	private final static String TRUSTROOTS = "TRUSTED_CERTS=";
	private final static String USERNAME = "USERNAME=";
	private final static String PASSPHRASE = "PASSPHRASE=";
	private final static String LIFETIME = "LIFETIME=";
	private final static String CREDNAME = "CRED_NAME=";
	private final static String RESPONSE = "RESPONSE=";
	private final static String ERROR = "ERROR=";
	private final static String DN = "CN=ignore";
	private final static String TRUSTED_CERT_PATH = ConfigOperation.getCertificateDir();
	public static final int DEFAULT_PORT = 7512;
	
	protected final static int keySize = 2048;
	protected final int MIN_PASS_PHRASE_LEN = 6;
	protected final static String keyAlg = "RSA";
	protected final static String pkcs10SigAlgName = "SHA1withRSA";
	protected final static String pkcs10Provider = "SunRsaSign";
	protected State state = State.READY;
	protected String host = "myproxy.xsede.org";
	protected String username;
	protected String credname;
	protected String passphrase;
	protected int port = 7512;
	protected int lifetime = 43200;
	protected boolean requestTrustRoots = false;
	protected SSLSocket socket;
	protected BufferedInputStream socketIn;
	protected BufferedOutputStream socketOut;
	protected KeyPair keypair;
	protected Collection certificateChain;
	protected String[] trustrootFilenames;
	protected String[] trustrootData;
	
	public MyProxyLogon() {
		super();
	}
	
	/**
	 * Constructs a MyProxyLogon object. Order of preference lookup for server
	 * and port are TGFM config, System env, default values.
	 */
	public MyProxyLogon(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	/**
	 * Gets the hostname of the MyProxy server.
	 * 
	 * @return MyProxy server hostname
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * Sets the hostname of the MyProxy server. Defaults to localhost.
	 * 
	 * @param host
	 *            MyProxy server hostname
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Gets the port of the MyProxy server.
	 * 
	 * @return MyProxy server port
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Sets the port of the MyProxy server. Defaults to 7512.
	 * 
	 * @param port
	 *            MyProxy server port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Gets the MyProxy username.
	 * 
	 * @return MyProxy server port
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Sets the MyProxy username. Defaults to user.name.
	 * 
	 * @param username
	 *            MyProxy username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the optional MyProxy credential name.
	 * 
	 * @return credential name
	 */
	public String getCredentialName() {
		return this.credname;
	}

	/**
	 * Sets the optional MyProxy credential name.
	 * 
	 * @param credname
	 *            credential name
	 */
	public void setCredentialName(String credname) {
		this.credname = credname;
	}

	/**
	 * Sets the MyProxy passphrase.
	 * 
	 * @param passphrase
	 *            MyProxy passphrase
	 */
	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	/**
	 * Gets the requested credential lifetime.
	 * 
	 * @return Credential lifetime
	 */
	public int getLifetime() {
		return this.lifetime;
	}

	/**
	 * Sets the requested credential lifetime. Defaults to 43200 seconds (12
	 * hours).
	 * 
	 * @param seconds
	 *            Credential lifetime
	 */
	public void setLifetime(int seconds) {
		this.lifetime = seconds;
	}

	/**
	 * Gets the certificates returned from the MyProxy server by
	 * getCredentials().
	 * 
	 * @return Collection of java.security.cert.Certificate objects
	 */
	public Collection getCertificates() {
		return this.certificateChain;
	}

	/**
	 * Gets the private key generated by getCredentials().
	 * 
	 * @return PrivateKey
	 */
	public PrivateKey getPrivateKey() {
		return this.keypair.getPrivate();
	}

	/**
	 * Sets whether to request trust roots (CA certificates, CRLs, signing
	 * policy files) from the MyProxy server. Defaults to false (i.e., not to
	 * request trust roots).
	 * 
	 * @param flag
	 *            If true, request trust roots. If false, don't request trust
	 *            roots.
	 */
	public void requestTrustRoots(boolean flag) {
		this.requestTrustRoots = flag;
	}

	/**
	 * Gets trust root filenames.
	 * 
	 * @return trust root filenames
	 */
	public String[] getTrustRootFilenames() {
		return this.trustrootFilenames;
	}

	/**
	 * Gets trust root data corresponding to the trust root filenames.
	 * 
	 * @return trust root data
	 */
	public String[] getTrustRootData() {
		return this.trustrootData;
	}

	/**
	 * Connects to the MyProxy server at the desired host and port. Requires
	 * host authentication via SSL. The host's certificate subject must match
	 * the requested hostname. If CA certificates are found in the standard GSI
	 * locations, they will be used to verify the server's certificate. If trust
	 * roots are requested and no CA certificates are found, the server's
	 * certificate will still be accepted.
	 */
	public void connect() throws IOException, GeneralSecurityException {
		SSLContext sc = SSLContext.getInstance("SSL");
		TrustManager[] trustAllCerts = new TrustManager[] { new MyTrustManager() };
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		SSLSocketFactory sf = sc.getSocketFactory();
		this.socket = (SSLSocket) sf.createSocket(this.host, this.port);
		this.socket.setEnabledProtocols(new String[] { "SSLv3" });
		this.socket.startHandshake();
		this.socketIn = new BufferedInputStream(this.socket.getInputStream());
		this.socketOut = new BufferedOutputStream(this.socket.getOutputStream());
		this.state = State.CONNECTED;
	}

	/**
	 * Disconnects from the MyProxy server.
	 */
	public void disconnect() throws IOException {
		this.socket.close();
		this.socket = null;
		this.socketIn = null;
		this.socketOut = null;
		this.state = State.READY;
	}

	/**
	 * Logs on to the MyProxy server by issuing the MyProxy GET command.
	 */
	public void logon() throws IOException, GeneralSecurityException {
		String line;
		char response;

		if (this.state != State.CONNECTED) {
			this.connect();
		}

		this.socketOut.write('0');
		this.socketOut.flush();
		this.socketOut.write(VERSION.getBytes());
		this.socketOut.write('\n');
		this.socketOut.write(GETCOMMAND.getBytes());
		this.socketOut.write('\n');
		this.socketOut.write(USERNAME.getBytes());
		this.socketOut.write(this.username.getBytes());
		this.socketOut.write('\n');
		this.socketOut.write(PASSPHRASE.getBytes());
		this.socketOut.write(this.passphrase.getBytes());
		this.socketOut.write('\n');
		this.socketOut.write(LIFETIME.getBytes());
		this.socketOut.write(Integer.toString(this.lifetime).getBytes());
		this.socketOut.write('\n');
		if (this.credname != null) {
			this.socketOut.write(CREDNAME.getBytes());
			this.socketOut.write(this.credname.getBytes());
			this.socketOut.write('\n');
		}
		if (this.requestTrustRoots) {
			this.socketOut.write(TRUSTROOTS.getBytes());
			this.socketOut.write("1\n".getBytes());
		}
		this.socketOut.flush();

		line = readLine(this.socketIn);
		if (line == null) {
			throw new EOFException();
		}
		if (!line.equals(VERSION)) {
			throw new ProtocolException("bad MyProxy protocol VERSION string: "
					+ line);
		}
		line = readLine(this.socketIn);
		if (line == null) {
			throw new EOFException();
		}
		if (!line.startsWith(RESPONSE)
				|| line.length() != RESPONSE.length() + 1) {
			throw new ProtocolException(
					"bad MyProxy protocol RESPONSE string: " + line);
		}
		response = line.charAt(RESPONSE.length());
		if (response == '1') {
			StringBuffer errString;

			errString = new StringBuffer("MyProxy logon failed");
			while ((line = readLine(this.socketIn)) != null) {
				if (line.startsWith(ERROR)) {
					errString.append('\n');
					errString.append(line.substring(ERROR.length()));
				}
			}
			throw new FailedLoginException(errString.toString());
		} else if (response == '2') {
			throw new ProtocolException(
					"MyProxy authorization RESPONSE not implemented");
		} else if (response != '0') {
			throw new ProtocolException(
					"unknown MyProxy protocol RESPONSE string: " + line);
		}
		while ((line = readLine(this.socketIn)) != null) {
			if (line.startsWith(TRUSTROOTS)) {
				String filenameList = line.substring(TRUSTROOTS.length());
				this.trustrootFilenames = filenameList.split(",");
				this.trustrootData = new String[this.trustrootFilenames.length];
				for (int i = 0; i < this.trustrootFilenames.length; i++) {
					String lineStart = "FILEDATA_" + this.trustrootFilenames[i]
							+ "=";
					line = readLine(this.socketIn);
					if (line == null) {
						throw new EOFException();
					}
					if (!line.startsWith(lineStart)) {
						throw new ProtocolException(
								"bad MyProxy protocol RESPONSE: expecting "
										+ lineStart + " but received " + line);
					}
					this.trustrootData[i] = new String(Base64.decode(line
							.substring(lineStart.length())));
				}
			}
		}
		this.state = State.LOGGEDON;
	}

	/**
	 * Retrieves credentials from the MyProxy server.
	 */
	@SuppressWarnings("rawtypes")
	public GSSCredential getCredentials() throws MyProxyException 
	{
		try 
		{
			int numCertificates;
			KeyPairGenerator keyGenerator;
			PKCS10CertificationRequest pkcs10;
			CertificateFactory certFactory;
	
			if (this.state != State.LOGGEDON) {
				this.logon();
			}
	
			keyGenerator = KeyPairGenerator.getInstance(keyAlg);
			keyGenerator.initialize(keySize);
			this.keypair = keyGenerator.genKeyPair();
	
			pkcs10 = new PKCS10CertificationRequest(pkcs10SigAlgName, new X509Name(
					DN), this.keypair.getPublic(), null, this.keypair.getPrivate(),
					pkcs10Provider);
	
			this.socketOut.write(pkcs10.getEncoded());
			this.socketOut.flush();
			numCertificates = this.socketIn.read();
			if (numCertificates == -1) {
				System.err.println("connection aborted");
				System.exit(1);
			} else if (numCertificates == 0 || numCertificates < 0) {
				System.err.print("bad number of certificates sent by server: ");
				System.err.println(Integer.toString(numCertificates));
				System.exit(1);
			}
			certFactory = CertificateFactory.getInstance("X.509");
			this.certificateChain = certFactory.generateCertificates(this.socketIn);
			this.state = State.DONE;
			
			X509Certificate certificate;
			PrintStream printStream;
			
			Iterator iter = this.certificateChain.iterator();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
			certificate = (X509Certificate) iter.next();
			printStream = new PrintStream(bos);
			printCert(certificate, printStream);
			printKey(keypair.getPrivate(), printStream);
			while (iter.hasNext()) {
				certificate = (X509Certificate) iter.next();
				printCert(certificate, printStream);
			}
	
			ExtendedGSSManager manager = (ExtendedGSSManager) ExtendedGSSManager
					.getInstance();
			GSSCredential proxy = (GlobusGSSCredentialImpl)manager.createCredential(bos.toByteArray(),
					ExtendedGSSCredential.IMPEXP_OPAQUE,
					GSSCredential.DEFAULT_LIFETIME, null, // use default mechanism -
															// GSI
					GSSCredential.INITIATE_AND_ACCEPT);
			return proxy;
		} 
		catch (Exception e) 
		{
			throw new MyProxyException(e.getMessage(), e);
		}
	}

	/**
	 * Writes the retrieved credentials to the Globus proxy file location.
	 */
	public void writeProxyFile() throws IOException, GeneralSecurityException {
		saveCredentialsToFile(getProxyLocation());
	}

	public Collection getCertificateChain() {
		return certificateChain;
	}

	/**
	 * Writes the retrieved credentials to the specified filename.
	 */
	public void saveCredentialsToFile(String filename) throws IOException,
	GeneralSecurityException {
		Iterator iter;
		X509Certificate certificate;
		PrintStream printStream;

		iter = this.certificateChain.iterator();
		certificate = (X509Certificate) iter.next();
		File outFile = new File(filename);
		outFile.delete();
		outFile.createNewFile();
		setFilePermissions(filename, "0600");
		printStream = new PrintStream(new FileOutputStream(outFile));
		printCert(certificate, printStream);
		printKey(keypair.getPrivate(), printStream);
		while (iter.hasNext()) {
			certificate = (X509Certificate) iter.next();
			printCert(certificate, printStream);
		}
	}

	public X509Credential convertCredentialsToGlobusCredential()
			throws CertificateEncodingException, IOException, GSSException {
		Iterator<X509Certificate> iter;
		X509Certificate certificate;
		PrintStream printStream;

		iter = this.certificateChain.iterator();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		certificate = (X509Certificate) iter.next();
		printStream = new PrintStream(bos);
		printCert(certificate, printStream);
		printKey(keypair.getPrivate(), printStream);
		while (iter.hasNext()) {
			certificate = (X509Certificate) iter.next();
			printCert(certificate, printStream);
		}

		ExtendedGSSManager manager = (ExtendedGSSManager) ExtendedGSSManager
				.getInstance();
		GSSCredential proxy = manager.createCredential(bos.toByteArray(),
				ExtendedGSSCredential.IMPEXP_OPAQUE,
				GSSCredential.DEFAULT_LIFETIME, null, // use default mechanism -
														// GSI
				GSSCredential.ACCEPT_ONLY);

		X509Credential globusCred = null;

		if (proxy instanceof GlobusGSSCredentialImpl) {
			globusCred = ((GlobusGSSCredentialImpl) proxy).getX509Credential();
		}

		return globusCred;

	}

	/**
	 * Writes the retrieved trust roots to the Globus trusted certificates
	 * directory.
	 * 
	 * @return true if trust roots are written successfully, false if no trust
	 *         roots are available to be written
	 */
	public boolean writeTrustRoots() throws IOException {
		return writeTrustRoots(getTrustRootPath());
	}

	/**
	 * Writes the retrieved trust roots to a trusted certificates directory.
	 * 
	 * @param directory
	 *            path where the trust roots should be written
	 * @return true if trust roots are written successfully, false if no trust
	 *         roots are available to be written
	 */
	public boolean writeTrustRoots(String directory) throws IOException {
		if (this.trustrootFilenames == null || this.trustrootData == null) {
			return false;
		}
		File rootDir = new File(directory);
		if (!rootDir.exists()) {
			rootDir.mkdirs();
		}
		for (int i = 0; i < trustrootFilenames.length; i++) {
//			logger.debug("Wrote trust file " + directory + File.separator
//					+ this.trustrootFilenames[i]);
			FileOutputStream out = new FileOutputStream(directory
					+ File.separator + this.trustrootFilenames[i]);
			out.write(this.trustrootData[i].getBytes());
			out.close();
		}
		logger.info("Wrote trust files for " + host + " to " + directory);
		
		return true;
	}
	
	/**
	 * Writes the retrieved trust roots to a trusted certificates directory.
	 * 
	 * @param directory
	 *            path where the trust roots should be written
	 * @return true if trust roots are written successfully, false if no trust
	 *         roots are available to be written
	 */
	public Map<String, String> writeTrustRootsAsMap() 
	{
		Map<String, String> trustRootMap = new HashMap<String, String>();
    	
    	for (int i = 0; i < trustrootFilenames.length; i++) {
    		trustRootMap.put(this.trustrootFilenames[i], this.trustrootData[i]);
        }
        return trustRootMap;
	}

	/**
	 * Gets the trusted CA certificates returned by the MyProxy server.
	 * 
	 * @return trusted CA certificates, or null if none available
	 */
	public X509Certificate[] getTrustedCAs() throws CertificateException {
		if (trustrootData == null)
			return null;
		return getX509CertsFromStringList(trustrootData, trustrootFilenames);
	}

	private static X509Certificate[] getX509CertsFromStringList(
			String[] certList, String[] nameList) throws CertificateException {
		CertificateFactory certFactory = CertificateFactory
				.getInstance("X.509");
		Collection<X509Certificate> c = new ArrayList<X509Certificate>(
				certList.length);
		for (int i = 0; i < certList.length; i++) {
			int index = -1;
			String certData = certList[i];
			if (certData != null) {
				index = certData.indexOf("-----BEGIN CERTIFICATE-----");
			}
			if (index >= 0) {
				certData = certData.substring(index);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(
						certData.getBytes());
				try {
					X509Certificate cert = (X509Certificate) certFactory
							.generateCertificate(inputStream);
					c.add(cert);
				} catch (Exception e) {
					if (nameList != null) {
						logger.debug(nameList[i]
								+ " can not be parsed as an X509Certificate.");
					} else {
						logger.error("failed to parse an X509Certificate");
					}
				}
			}
		}
		if (c.isEmpty())
			return null;
		return c.toArray(new X509Certificate[0]);
	}

	/**
	 * Gets the CRLs returned by the MyProxy server.
	 * 
	 * @return CRLs or null if none available
	 */
	public X509CRL[] getCRLs() throws CertificateException {
		if (trustrootData == null)
			return null;
		CertificateFactory certFactory = CertificateFactory
				.getInstance("X.509");
		Collection<X509CRL> c = new ArrayList<X509CRL>(trustrootData.length);
		for (int i = 0; i < trustrootData.length; i++) {
			String crlData = trustrootData[i];
			int index = crlData.indexOf("-----BEGIN X509 CRL-----");
			if (index >= 0) {
				crlData = crlData.substring(index);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(
						crlData.getBytes());
				try {
					X509CRL crl = (X509CRL) certFactory
							.generateCRL(inputStream);
					c.add(crl);
				} catch (Exception e) {
					logger.error(this.trustrootFilenames[i]
							+ " can not be parsed as an X509CRL.");
				}
			}
		}
		if (c.isEmpty())
			return null;
		return c.toArray(new X509CRL[0]);
	}

	/**
     * Returns the trusted certificates directory location where
     * writeTrustRoots() will store certificates.
     * It first checks the X509_CERT_DIR system property.
     * If that property is not set, it uses
     * ${user.home}/.globus/certificates.
     * Note that, unlike CoGProperties.getCaCertLocations(),
     * it does not return /etc/grid-security/certificates or
     * ${GLOBUS_LOCATION}/share/certificates.
     */
    public static String getTrustRootPath() 
    {
        return TRUSTED_CERT_PATH;
    }

	/**
	 * Gets the existing trusted CA certificates directory.
	 * 
	 * @return directory path string or null if none found
	 */
	public static String getExistingTrustRootPath() {
		return TRUSTED_CERT_PATH;
	}

	/**
	 * Returns the default Globus proxy file location.
	 */
	public static String getProxyLocation() throws IOException {
		String loc, suffix = null;
		Process proc;
		BufferedReader bufferedReader;

		loc = System.getenv("X509_USER_PROXY");
		if (loc == null) {
			loc = System.getProperty("X509_USER_PROXY");
		}
		if (loc != null) {
			return loc;
		}

		try {
			proc = Runtime.getRuntime().exec("id -u");
			bufferedReader = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			suffix = bufferedReader.readLine();
		} catch (IOException e) {
			// will fail on windows
		}

		if (suffix == null) {
			suffix = System.getProperty("user.name");
			if (suffix != null) {
				suffix = suffix.toLowerCase();
			} else {
				suffix = "nousername";
			}
		}

		return System.getProperty("java.io.tmpdir") + File.separator
				+ X509_USER_PROXY_FILE + suffix;
	}

	/**
	 * Provides a simple command-line interface.
	 */
	public static void main(String[] args) {
		try {
			MyProxyLogon m = new MyProxyLogon("myproxy.xsede.org", DEFAULT_PORT);
			// Console cons = System.console();
			String passphrase = null;
			X509Certificate[] CAcerts;
			X509CRL[] CRLs;
			
			m.setUsername("dooley");
			System.out.println("Warning: terminal will echo passphrase as you type.");
			System.out.print("MyProxy Passphrase: ");
			passphrase = readLine(System.in);
			
			if (passphrase == null) {
				System.err.println("Error reading passphrase.");
				System.exit(1);
			}
			m.setPassphrase(passphrase);
			m.requestTrustRoots(true);
			GSSCredential proxy = m.getCredentials();
			m.writeProxyFile();
			
			Assert.assertNotNull(proxy, "No proxy retrieved");
			Assert.assertTrue(new File(getTrustRootPath()).exists(), "Trusted CA folder was not created");
			Assert.assertTrue(new File(getTrustRootPath()).list().length > 0, "Trusted CA folder is empty");
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private static void printB64(byte[] data, PrintStream out) {
		byte[] b64data;

		b64data = Base64.encode(data);
		for (int i = 0; i < b64data.length; i += b64linelen) {
			if ((b64data.length - i) > b64linelen) {
				out.write(b64data, i, b64linelen);
			} else {
				out.write(b64data, i, b64data.length - i);
			}
			out.println();
		}
	}

	private static void printCert(X509Certificate certificate, PrintStream out)
			throws CertificateEncodingException {
		out.println("-----BEGIN CERTIFICATE-----");
		printB64(certificate.getEncoded(), out);
		out.println("-----END CERTIFICATE-----");
	}

	private static void printKey(PrivateKey key, PrintStream out)
			throws IOException {
		out.println("-----BEGIN RSA PRIVATE KEY-----");
		ByteArrayInputStream inStream = new ByteArrayInputStream(
				key.getEncoded());
		ASN1InputStream derInputStream = new ASN1InputStream(inStream);
		DERObject keyInfo = derInputStream.readObject();
		PrivateKeyInfo pkey = new PrivateKeyInfo((ASN1Sequence) keyInfo);
		DERObject derKey = pkey.getPrivateKey();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DEROutputStream der = new DEROutputStream(bout);
		der.writeObject(derKey);
		printB64(bout.toByteArray(), out);
		out.println("-----END RSA PRIVATE KEY-----");
	}

	private static void setFilePermissions(String file, String mode) {
		String command = "chmod " + mode + " " + file;
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			logger.error("Failed to run: " + command); // windows
		}
	}

	private static String readLine(InputStream is) throws IOException {
		StringBuffer sb = new StringBuffer();
		for (int c = is.read(); c > 0 && c != '\n'; c = is.read()) {
			sb.append((char) c);
		}
		if (sb.length() > 0) {
			return new String(sb);
		}
		return null;
	}

	private static String getDir(String path) {
		if (path == null)
			return null;
		File f = new File(path);
		if (f.isDirectory() && f.canRead()) {
			return f.getAbsolutePath();
		}
		return null;
	}
	
	public static String serializeCredential(GSSCredential cred) throws GSSException 
	{
		byte[] serializedCredential = ((GlobusGSSCredentialImpl)cred).export(ExtendedGSSCredential.IMPEXP_OPAQUE);
		return new String(serializedCredential);
	}
	
	public Map<String,String> bootstrapTrust(boolean writeToDisk) throws MyProxyException 
    {
        try 
        {
        	Map<String, String> trustCertMap = new HashMap<String,String>();
        	
            SSLContext sc = SSLContext.getInstance("SSL");
            MyTrustManager myTrustManager = new MyTrustManager();
            TrustManager[] trustAllCerts = new TrustManager[] { myTrustManager };
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory sf = sc.getSocketFactory();
            SSLSocket socket = (SSLSocket)sf.createSocket(getHost(), getPort());
            socket.setEnabledProtocols(new String[] { "SSLv3" });
            socket.startHandshake();
            socket.close();

            X509Certificate[] acceptedIssuers = myTrustManager.getAcceptedIssuers();
            
            if (acceptedIssuers == null) {
                throw new MyProxyException("Failed to determine MyProxy server trust roots in bootstrapTrust.");
            }
            
            for (int idx = 0; idx < acceptedIssuers.length; idx++)
            {   
            	File x509Dir = new File(getTrustRootPath());
                if (!x509Dir.exists())
                {
                	x509Dir.mkdirs();
                }
                
                StringBuffer newSubject = new StringBuffer();
                String[] subjArr = acceptedIssuers[idx].getSubjectDN().getName().split(", ");
                for(int i = (subjArr.length - 1); i > -1; i--)
                {
                    newSubject.append("/");
                    newSubject.append(subjArr[i]);
                }
                String subject = newSubject.toString();
                String hash = opensslHash(acceptedIssuers[idx]);
                String filename = x509Dir.getPath() + File.separator + hash + ".0";

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                FileOutputStream fos = new FileOutputStream(new File(filename));
                CertUtil.writeCertificate(os, acceptedIssuers[idx]);
                CertUtil.writeCertificate(fos, acceptedIssuers[idx]);
                
                os.close();
                fos.close();

                trustCertMap.put(hash + ".0", new String(os.toByteArray()));
                
                if (logger.isDebugEnabled()) {
                	logger.debug("wrote trusted certificate " + hash + ".0");
                    logger.debug("wrote trusted certificate to " + filename);
                }

                filename = x509Dir.getPath() + File.separator + hash + ".signing_policy";

                fos = new FileOutputStream(new File(filename));
                Writer wr = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
                wr.write("access_id_CA X509 '");
                wr.write(subject);
                wr.write("'\npos_rights globus CA:sign\ncond_subjects globus \"*\"\n");
                wr.flush();
                wr.close();
                fos.close();
                
                
                StringBuilder sb = new StringBuilder();
                sb.append("access_id_CA X509 '");
                sb.append(subject);
                sb.append("'\npos_rights globus CA:sign\ncond_subjects globus \"*\"\n");
                
                trustCertMap.put(hash + ".signing_policy", sb.toString());
                
                logger.debug("wrote trusted certificate policy " + hash + ".signing_policy");
            }
            
//            if (writeToDisk) {
//            	writeBootstrapedTrust(acceptedIssuers);
//            }
            	
            return trustCertMap;
        } 
        catch(Exception e) 
        {
            throw new MyProxyException("MyProxy bootstrapTrust failed.", e);
        }
    }
    
    public void writeBootstrapedTrust(X509Certificate[] acceptedIssuers) 
    throws MyProxyException 
    {
        try 
        {   
            if (acceptedIssuers == null) {
                throw new MyProxyException("Failed to determine MyProxy server trust roots in bootstrapTrust.");
            }
         
            for (int idx = 0; idx < acceptedIssuers.length; idx++)
            {
                File x509Dir = new File(getTrustRootPath());
                if (!x509Dir.exists())
                {
                	x509Dir.mkdirs();
                }
                
                StringBuffer newSubject = new StringBuffer();
                String[] subjArr = acceptedIssuers[idx].getSubjectDN().getName().split(", ");
                for(int i = (subjArr.length - 1); i > -1; i--)
                {
                    newSubject.append("/");
                    newSubject.append(subjArr[i]);
                }
                String subject = newSubject.toString();

                String hash = opensslHash(acceptedIssuers[idx]);
                String filename = x509Dir.getPath() + File.separator + hash + ".0";

                FileOutputStream os = new FileOutputStream(new File(filename));
                CertUtil.writeCertificate(os, acceptedIssuers[idx]);

                os.close();
                if (logger.isDebugEnabled()) {
                    logger.debug("wrote trusted certificate to " + filename);
                }

                filename = x509Dir.getPath() + File.separator + hash + ".signing_policy";

                os = new FileOutputStream(new File(filename));
                Writer wr = new OutputStreamWriter(os, Charset.forName("UTF-8"));
                wr.write("access_id_CA X509 '");
                wr.write(subject);
                wr.write("'\npos_rights globus CA:sign\ncond_subjects globus \"*\"\n");
                wr.flush();
                wr.close();
                os.close();

                if (logger.isDebugEnabled()) {
                    logger.debug("wrote trusted certificate policy to " + filename);
                }                    
            }
        } catch(Exception e) {
            throw new MyProxyException("MyProxy bootstrapTrust failed.", e);
        }
    }
    
	/*
		the following methods are based off code to compute the subject
		name hash from:
		http://blog.piefox.com/2008/10/javaopenssl-ca-generation.html
	*/
	private String opensslHash(X509Certificate cert) 
	{
		try {
			return openssl_X509_NAME_hash(cert.getSubjectX500Principal());
		}
		catch (Exception e) {
			throw new Error("MD5 isn't available!", e);
		}
	}
	
	/**
	* Generates a hex X509_NAME hash (like openssl x509 -hash -in cert.pem)
	* Based on openssl's crypto/x509/x509_cmp.c line 321
	*/
	private String openssl_X509_NAME_hash(X500Principal p) throws Exception 
	{
		// This code replicates OpenSSL's hashing function
		// DER-encode the Principal, MD5 hash it, then extract the first 4 bytes and reverse their positions
		byte[] derEncodedSubject = p.getEncoded();
		byte[] md5 = MessageDigest.getInstance("MD5").digest(derEncodedSubject);
	
		// Reduce the MD5 hash to a single unsigned long
		byte[] result = new byte[] { md5[3], md5[2], md5[1], md5[0] };
		return toHex(result);
	}
	
	// encode binary to hex
	private String toHex(final byte[] bin) 
	{
		if (bin == null || bin.length == 0)
			return "";

		char[] buffer = new char[bin.length * 2];

		final char[] hex = "0123456789abcdef".toCharArray();

		// i tracks input position, j tracks output position
		for (int i = 0, j = 0; i < bin.length; i++)
		{
			final byte b = bin[i];
			buffer[j++] = hex[(b >> 4) & 0x0F];
			buffer[j++] = hex[b & 0x0F];
		}
		return new String(buffer);
	}
}
