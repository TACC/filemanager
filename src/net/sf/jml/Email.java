/* 
 * Created on Feb 26, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package net.sf.jml;

import java.util.regex.Pattern;

public class Email {
    @SuppressWarnings("unused")
	private static final Pattern EMAIL_PATTERN = Pattern
    .compile("\\S+@\\S+\\.\\S+");
    
    private String emailAddress;
    
    private Email(String emailAddress) {
    this.emailAddress = emailAddress;
    }
    
    public static Email parseStr(String emailAddress) {
//    if (EMAIL_PATTERN.matcher(emailAddress).matches())
        return new Email(emailAddress);
//    return null;
    }
    
    @Override
    public boolean equals(Object obj) {
    if (this == obj) {
        return true;
    }
    if (!(obj instanceof Email)) {
        return false;
    }
    Email email = (Email) obj;
    return emailAddress == null ? email.emailAddress == null : emailAddress
            .equals(email.emailAddress);
    }
    
    @Override
    public int hashCode() {
    return emailAddress.hashCode();
    }
    
    public String getEmailAddress() {
    return emailAddress;
    }
    
    @Override
    public String toString() {
    return emailAddress;
    }
}
