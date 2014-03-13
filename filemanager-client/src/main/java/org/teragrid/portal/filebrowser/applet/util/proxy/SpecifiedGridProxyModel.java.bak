/*Copyright (c) 2004,University of Illinois at Urbana-Champaign.  All rights reserved.

Developed by:
Chemistry and Computational Biology Group

NCSA, University of Illinois at Urbana-Champaign

http://ncsa.uiuc.edu/GridChem

Permission is hereby granted, free of charge, to any person 
obtaining a copy of this software and associated documentation
files (the "Software"), to deal with the Software without 
restriction, including without limitation the rights to use, 
copy, modify, merge, publish, distribute, sublicense, and/or 
sell copies of the Software, and to permit persons to whom 
the Software is furnished to do so, subject to the following 
conditions:
1. Redistributions of source code must retain the above copyright notice, 
   this list of conditions and the following disclaimers.
2. Redistributions in binary form must reproduce the above copyright notice, 
   this list of conditions and the following disclaimers in the documentation
   and/or other materials provided with the distribution.
3. Neither the names of Chemistry and Computational Biology Group , NCSA, 
   University of Illinois at Urbana-Champaign, nor the names of its contributors 
   may be used to endorse or promote products derived from this Software without 
   specific prior written permission.
    
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  
IN NO EVENT SHALL THE CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
DEALINGS WITH THE SOFTWARE.

*/
package org.teragrid.portal.filebrowser.applet.util.proxy;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.globus.common.CoGProperties;
import org.globus.gsi.CertUtil;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.OpenSSLKey;
import org.globus.gsi.bc.BouncyCastleOpenSSLKey;
import org.globus.tools.proxy.GridProxyModel;
import org.globus.gsi.bc.BouncyCastleCertProcessingFactory;

/**
 * Utility class to generate a GlobusCredential using the
 * properties file assigned by the user. Based on DefaultGridProxyModel
 * class from Java CoGKit.
 * 
 * @author Rion Dooley < dooley [at] cct [dot] lsu [dot] edu >
 *
 * @see GlobusCredential
 * @see DefaultGridProxyModel
 * @see CoGProperties
 */
public class SpecifiedGridProxyModel extends GridProxyModel {
    
    public void setProperties(CoGProperties props) {
        this.props = props;
    }
    
    public GlobusCredential createProxy(String pwd)
	throws Exception {   

	getProperties();

	userCert = CertUtil.loadCertificate(props.getUserCertFile());
	
	OpenSSLKey key = 
	    new BouncyCastleOpenSSLKey(props.getUserKeyFile());
	
	if (key.isEncrypted()) {
	    try {
	        key.decrypt(pwd);
	    } catch(GeneralSecurityException e) {
	        throw new Exception("Wrong password or other security error");
	    }
	}
	
	PrivateKey userKey = key.getPrivateKey();
	
	BouncyCastleCertProcessingFactory factory =
	    BouncyCastleCertProcessingFactory.getDefault();

	int proxyType = (getLimited()) ? 
	    GSIConstants.DELEGATION_LIMITED :
	    GSIConstants.DELEGATION_FULL;

	return factory.createCredential(new X509Certificate[] {userCert},
					userKey,
					props.getProxyStrength(), 
					props.getProxyLifeTime() * 3600,
					proxyType);
    }
    
    public GlobusCredential createProxy(String certFile, String keyFile, String pwd)
	throws Exception {   

	getProperties();

	userCert = CertUtil.loadCertificate(certFile);
	
	OpenSSLKey key = 
	    new BouncyCastleOpenSSLKey(keyFile);
	
	if (key.isEncrypted()) {
	    try {
		key.decrypt(pwd);
	    } catch(GeneralSecurityException e) {
		throw new Exception("Wrong password or other security error");
	    }
	}
	
	PrivateKey userKey = key.getPrivateKey();
	
	BouncyCastleCertProcessingFactory factory =
	    BouncyCastleCertProcessingFactory.getDefault();

	int proxyType = (getLimited()) ? 
	    GSIConstants.DELEGATION_LIMITED :
	    GSIConstants.DELEGATION_FULL;

	return factory.createCredential(new X509Certificate[] {userCert},
					userKey,
					props.getProxyStrength(), 
					props.getProxyLifeTime() * 3600,
					proxyType);
    }
    
}
