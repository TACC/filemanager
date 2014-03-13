/* 
 * Created on Jul 24, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package net.spy.s3;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class DeleteResponse extends Response {

    /**
     * @param connection
     * @throws IOException
     */
    public DeleteResponse(HttpURLConnection connection) throws IOException {
        super(connection);
        
        
        try {
            String code = getS3ErrorCode(connection.getInputStream());
            System.out.println("Error code is: " + code);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
            
        }
        
    }
    
    public String getS3ErrorCode(InputStream doc) throws Exception
    {
        System.out.println("byest in reply: " + doc.available());
        if (doc.available() == 0) {
            return null;
        }
        
      String code = null;
      SAXParserFactory parserfactory = SAXParserFactory.newInstance();
      parserfactory.setNamespaceAware(false);
      parserfactory.setValidating(false);
      SAXParser xmlparser = parserfactory.newSAXParser();
      S3ErrorHandler handler = new S3ErrorHandler();
      byte[] resultarray = new byte[32525];
      doc.read(resultarray);
      System.out.println(new String(resultarray));
      
      xmlparser.parse(doc, handler);
      code = handler.getErrorCode();
      return code;
    }

    // This inner class implements a SAX handler.
    class S3ErrorHandler extends DefaultHandler
    {
      private StringBuffer code = new StringBuffer();
      private boolean append = false;

      public void startElement(String uri, String ln, String qn, Attributes atts)
      {
        if (qn.equalsIgnoreCase("Code")) append = true;
      }
      public void endElement(String url, String ln, String qn)
      {
        if (qn.equalsIgnoreCase("Code")) append = false;
      }
      public void characters(char[] ch, int s, int length)
      {
          System.out.println(new String(ch));
        if (append) code.append(new String(ch, s, length));
      }

      public String getErrorCode()
      {
        return code.toString();
      }
    }
}
