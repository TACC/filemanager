/* 
 * Created on Jul 27, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.ui.table;

import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import org.globus.ftp.FileInfo;
import org.teragrid.portal.filebrowser.applet.ui.ListModel;
import org.teragrid.portal.filebrowser.applet.util.LogManager;


/**
 * Compares files for sorting
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
@SuppressWarnings("unchecked")
public class FileComparator implements Comparator {

    protected int        m_sortCol;
    protected boolean m_sortAsc;
    
    public FileComparator(int sortCol, boolean sortAsc) {
        m_sortCol = sortCol;
        m_sortAsc = sortAsc;
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        if(!(o1 instanceof FileInfo) || !(o2 instanceof FileInfo))
            return 0;
        FileInfo s1 = (FileInfo)o1;
        FileInfo s2 = (FileInfo)o2;
        int result = 0;
        Long l1,l2;
        switch (m_sortCol) {
            case 0:     // name
                result = s1.getName().toLowerCase().compareTo(s2.getName().toLowerCase());
                break;
            case 1:     // size
                l1 = s1.getSize();
                l2 = s2.getSize();
                result = l1<l2 ? -1 : (l1>l2 ? 1 : 0);
                break;
            case 2:     // type
                byte type1 = getType(s1);
                byte type2 = getType(s2);
                result = type1<type2 ? -1 : (type1>type2 ? 1 : 0);
                int t1=ListModel.getType(s1), t2=ListModel.getType(s2);
                String name1=ListModel.getFileName(s1),name2=ListModel.getFileName(s2);
                if(t1!=t2){
                    if(FileInfo.DIRECTORY_TYPE==t1) {
                        result = -1;
                    }
                    else if(FileInfo.DIRECTORY_TYPE==t2) {
                        result = 1;
                    }
                    else if(FileInfo.SOFTLINK_TYPE==t1) {
                        result = -1;
                    }
                    else if(FileInfo.SOFTLINK_TYPE==t2) {
                        result = 1;
                    }
                    else if(FileInfo.FILE_TYPE==t1) {
                        result = -1;
                    }
                    else if(FileInfo.FILE_TYPE==t2) {
                        result = 1;
                    }
                    else if(FileInfo.UNKNOWN_TYPE==t1) {
                        result = -1;
                    }
                    else {
                        result = 1;
                    }
                }
                else{
                    if(s1.equals("..")) {
                        result = -1;
                    }
                    else if(s2.equals("..")) {
                        result = 1;
                    }
                    result = Collator.getInstance().compare(name1,name2);
                }
                break;
            case 3:     // date
                try {
                    l1 = formatDate(s1).getTime();
                    l2 = formatDate(s2).getTime();
                    result = l1<l2 ? -1 : (l1>l2 ? 1 : 0);
                } catch (ParseException e) {
                    LogManager.error("Failed to parse date when sorting",e);
                    result = 0;
                }
                    break;
            case 4:     // permission
                l1 = (long)s1.getMode();
                l2 = (long)s2.getMode();
                result = l1<l2 ? -1 : (l1>l2 ? 1 : 0);
                break;
            default:
                result = 0;
                break;
        }

        if (!m_sortAsc)
            result = -result;
        return result;
    }
    
    private byte getType(FileInfo file) {
        if (file.isFile()) {
            return FileInfo.FILE_TYPE;
        } else if (file.isDirectory()) {
            return FileInfo.DIRECTORY_TYPE;
        } else if (file.isSoftLink()) {
            return FileInfo.SOFTLINK_TYPE;
        } else if (file.isDevice()) {
            return FileInfo.DEVICE_TYPE;
        } else 
            return FileInfo.UNKNOWN_TYPE;
    }
    
    private Date formatDate(FileInfo file) throws ParseException {
        
        return new SimpleDateFormat("MMM dd HH:mm").parse(file.getDate() + " " + file.getTime());
    }

    public boolean equals(Object obj) {
        if (obj instanceof FileComparator) {
            FileComparator compObj = (FileComparator)obj;
            return (compObj.m_sortCol==m_sortCol) &&
                (compObj.m_sortAsc==m_sortAsc);
        }
        return false;
    }

}
