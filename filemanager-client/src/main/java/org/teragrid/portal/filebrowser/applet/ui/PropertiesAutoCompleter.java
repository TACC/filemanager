/* 
 * Created on Oct 16, 2007
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.text.JTextComponent;

import org.teragrid.portal.filebrowser.applet.util.LogManager;

//@author Santhosh Kumar T - santhosh@in.fiorano.com 

/**
 * Implements a Java properties backed autocomplete utility on a JTextComponent.
 * It first loads the properties from the user's file system, then looks the 
 * values for this text field by the hostname of the server associated with the 
 * referring PnlBrowse object.  When a selection is made, it is stored in the
 * existing properties file if not already there and the list is sorted.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings({"unused","unchecked"})
public class PropertiesAutoCompleter extends AutoCompleter{
    
    protected String hostname;
    protected ArrayList<String> values;
    
    static {
        try {
            props.load(new FileInputStream(AUTO_CONF_FILE));
        } catch (Exception e) {
            LogManager.error("Failed to load autocomplete history");
            File propsFile = new File(AUTO_CONF_FILE);
            propsFile.getParentFile().mkdirs();
            try {
                propsFile.createNewFile();
            } catch (IOException e1) {
                LogManager.error("Failed to create new autocomplete history file.",e);
            }
            
        }
    }
    
    public PropertiesAutoCompleter(JTextComponent comp, String host){ 
        super(comp);
        hostname = host;
        if (props.getProperty(hostname) != null) {
            values = new ArrayList<String>(Arrays.asList(props.getProperty(hostname).split(";")));
        } else {
            values = new ArrayList<String>();
        }
    } 
 
    protected boolean updateListData(){ 
        String value = textComp.getText(); 
//        int index1 = value.lastIndexOf('\\'); 
//        int index2 = value.lastIndexOf('/'); 
//        int index = Math.max(index1, index2); 
//        if(index==-1) 
//            return false; 
//        
//        String dir = value.substring(0, index+1);
//        final String prefix = index==value.length()-1 ? null : value.substring(index + 1).toLowerCase(); 
        List<String> filteredValues = filterValues(value);
        sort(filteredValues);
//        String[] files = new File(dir).list(new FilenameFilter(){ 
//            public boolean accept(File dir, String name){ 
//                return prefix!=null ? name.toLowerCase().startsWith(prefix) : true; 
//            } 
//        }); 
        if(filteredValues == null){ 
            list.setListData(new String[0]); 
            return true; 
        } else{ 
            if(filteredValues.size()==1 && filteredValues.get(0).equalsIgnoreCase(value)) 
                list.setListData(new String[0]); 
            else 
                list.setListData(values.toArray()); 
            return true; 
        } 
    } 
 
    protected void acceptedListItem(String selected){ 
        if(selected==null) 
            return; 
 
        String value = textComp.getText(); 
//        int index1 = value.lastIndexOf('\\'); 
//        int index2 = value.lastIndexOf('/'); 
//        int index = Math.max(index1, index2); 
//        if(index==-1) 
//            return; 
        int prefixlen = textComp.getDocument().getLength(); 
        try{ 
            textComp.getDocument().insertString(textComp.getCaretPosition(), selected.substring(prefixlen), null);
            if (!values.contains(selected)) {
                values.add(selected);
            }
            sort(values);
            props.put(hostname, serializeList());
            props.store(new FileOutputStream(AUTO_CONF_FILE),"TGFM Autocomplete File " + new Date().toString());
        } catch(Exception e){ 
            LogManager.error("Failed to accept autocomplete value " + selected,e);
        }
    } 
    
    protected void sort(List<String> vals) {
        Collections.sort(vals,new Comparator() {
            public int compare(Object arg0, Object arg1) {
                if (!(arg0 instanceof String) || !(arg1 instanceof String)) {
                    return 0;
                }
                return ((String)arg0).compareTo(((String)arg1));
            }   
        });
    }
    
    protected List<String> filterValues(String prefix) {
        List<String> filteredValues = new ArrayList<String>();
        
        for (String value: values) {
            if (value.startsWith(prefix)) {
                filteredValues.add(value);
            }
        }
        
        return filteredValues;
    }
    
    private String serializeList() {
        
        if (values.size() == 0) return "";
        
        String stringValue = "";
        
        for (String val: values) {
            stringValue += ";" + val;
        }
        
        return stringValue.substring(1);
    }
}
