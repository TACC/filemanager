package org.teragrid.portal.filebrowser.applet.gat.discovery.gpir.query;

import org.xml.sax.SAXException;

/** An example sink for content events. 
 * it parses the xml tags into dot notion ignoring closing
 * tags and prints the parent tags as well as the values
 * to standard out. 
 * 
 * @author Rion Dooley < dooley [at] cct [dot] lsu [dot] edu >
 *
 */
final class Sink extends org.xml.sax.helpers.DefaultHandler 
implements org.xml.sax.ContentHandler
{ 
    static String tuple = "";
    static String fullText = "";
    private static void print(final String text ) 
    {
        System.out.println(tuple + "=" + text);
        fullText = "";
    }

    public void startElement(final String namespace, 
            final String localname, final String type, 
            final org.xml.sax.Attributes attributes) 
    throws org.xml.sax.SAXException 
    { 
        if(tuple.equals("")){
            tuple = type;
        } else {
            tuple = tuple + "." + type;
            if(type.equals("ComputeResource") || type.equals("LoadInfo") || 
                    type.equals("JobInfo") || type.equals("NodeInfo") ) {
	            String[] attributeNames = {".hostname",".settings",".timestamp"};
	            for(int i=0;i<3;i++){
	                System.out.println(tuple + attributeNames[i] + 
	                        "=" + attributes.getValue(i));
	            }
            }
        }
    }

  public void endElement(final String namespace, 
          final String localname, final String type) 
  throws org.xml.sax.SAXException
  {
      int depth = tuple.lastIndexOf(".");
      if(depth == -1)
          tuple = "";
      else 
          tuple = tuple.substring(0,tuple.lastIndexOf("."));
      
      if(!fullText.equals(""))
          print(fullText);
      
      if(type.equals("Node") || type.equals("Job") || type.equals("ComputeResource"))
          System.out.println("\n");
  }

  public void characters(final char[] ch, 
          final int start, final int len) throws SAXException  
  {
      final String text = new String(ch,start,len);
      fullText = fullText + text;
  }
}
