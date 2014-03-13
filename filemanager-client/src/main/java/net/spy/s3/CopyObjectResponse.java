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
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class CopyObjectResponse extends Response {

    public Date lastModified = null;
    public String eTag = "";
    
    /**
     * @param connection
     * @throws IOException
     */
    public CopyObjectResponse(HttpURLConnection connection) throws IOException {
        super(connection);
        System.out.println("URL:" + connection.getURL());
        System.out.println("Request Method:" + connection.getRequestMethod());
        System.out.println("Headers:");
        for (String key: connection.getHeaderFields().keySet()) {
          System.out.println("\t" + key + ": " + connection.getHeaderFields().get(key));
        }
//        System.out.println("Request Properties:");
//        for (String key: connection.getRequestProperties().keySet()) {
//          System.out.println("\t" + key + ": " + connection.getRequestProperties().get(key));
//        }
        
        System.out.println("Response code:" + connection.getResponseCode());
        System.out.println("Response message:" + connection.getResponseMessage());
        
        if (connection.getResponseCode() < 400) {
            try {
                XMLReader xr = Utils.createXMLReader();
                CopyObjectResponseHandler handler = new CopyObjectResponseHandler();
                xr.setContentHandler(handler);
                xr.setErrorHandler(handler);

                xr.parse(new InputSource(connection.getInputStream()));
                lastModified = handler.getLastModified();
                eTag = handler.getETag();
            } catch (SAXException e) {
                throw new RuntimeException("Unexpected error parsing CopyObjectResponse xml", e);
            }
        }
    }
    
    static class CopyObjectResponseHandler extends DefaultHandler {

        private Date lastModified = null;
        private String eTag = "";
        private StringBuffer currText = null;
        private SimpleDateFormat iso8601Parser = null;

        public CopyObjectResponseHandler() {
            super();
            this.iso8601Parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            this.iso8601Parser.setTimeZone(new SimpleTimeZone(0, "GMT"));
            this.currText = new StringBuffer();
        }

        @Override
        public void startDocument() {
            // ignore
        }

        @Override
        public void endDocument() {
            // ignore
        }

        @Override
        public void startElement(String uri, String name, String qName, Attributes attrs) {
            //ignore
        }

        @Override
        public void endElement(String uri, String name, String qName) {
            if (name.equals("ETag")) {
                this.eTag = this.currText.toString();
            } else if (name.equals("LastModified")) {
                try {
                    this.lastModified = this.iso8601Parser.parse(this.currText.toString());
                } catch (ParseException e) {
                    throw new RuntimeException("Unexpected date format in list bucket output", e);
                }
            }
            this.currText = new StringBuffer();
        }

        @Override
        public void characters(char ch[], int start, int length) {
            this.currText.append(ch, start, length);
        }

        public Date getLastModified() {
            return this.lastModified;
        }
        
        public String getETag() {
            return this.eTag;
        }
        
    }
}