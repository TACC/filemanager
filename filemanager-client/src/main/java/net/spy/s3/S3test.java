/* 
 * Created on Jul 23, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package net.spy.s3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Test the spy s3 api
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 *
 */
public class S3test {

    public static String key = "1JFY56B1XFGXH7S2SJ82";
    public static String secret = "DlLnFRZAvKbvyh1o/iI+KsARzf5OiEDr1FKx31sP";
    /**
     * @param args
     */
    public static void main(String[] args) {
        AWSAuthConnection connection = new AWSAuthConnection(key,secret);
        
        @SuppressWarnings("unused")
		List<Bucket> buckets = new ArrayList<Bucket>();
        try {
            
//            buckets = connection.listAllMyBuckets(null).entries;
//            
//            System.out.println("Buckets found are: ");
//            for (Bucket bucket: buckets) {
//                System.out.println(bucket.name);
//            }
//            System.out.println("Creating bucket: test888");
//            connection.createBucket("test888",null);
////            connection.createBucket("test999",null);
////            try {
////                Thread.sleep(5000);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//            File file = new File("/Users/dooley/deleteme/conditt.xls");
//            InputStream in = new FileInputStream(file);
//            byte[] data = new byte[(int)file.length()];
//            long bytes = in.read(data);
//            S3Object object = new S3Object(data,null);
//            connection.put("test888", "conditt.xls4", object, null);
            
//            buckets = connection.listAllMyBuckets(null).entries;
//            List<ListEntry> entries = null;
//            System.out.println("Buckets found are: ");
//            for (Bucket bucket: buckets) {
//                System.out.println(bucket.name);
//                
//                entries = connection.listBucket(bucket.name, null,null,null,null).entries;
//                
//                for (ListEntry entry: entries) {
////                    GetResponse response = connection.getACL(bucket.name, entry.key, null);
//                    System.out.println(//response.object.data.toString() + "\t" + 
//                            entry.key + "\t" + 
//                            entry.owner.displayName + "\t" +
//                            entry.size + "\t" +
//                            entry.lastModified + "\t" + 
//                            entry.storageClass);
//                }
//            }
//           
            
            
//            connection.delete("test888", "conditt.xls",null);
            
            uploadFile(connection);
//            
//            System.out.println("Deleting bucket: test888");
//            Response response = connection.deleteBucket("test888", null);
////            
//            buckets = connection.listAllMyBuckets(null).entries;
//            System.out.println("Buckets found are: ");
//            for (Bucket bucket: buckets) {
//                System.out.println(bucket.name);
//            }
//            
//            System.out.println("Copying file: ");
//            connection.copy("1jfy56b1xfgxh7s2sj82.test", "dissertation-data2.tgz", "1jfy56b1xfgxh7s2sj82.test", "dissertation-data.tgz", null);
//            
//            entries = connection.listBucket("1jfy56b1xfgxh7s2sj82.test", null,null,null,null).entries;
//            
//            for (ListEntry entry: entries) {
////                GetResponse response = connection.getACL("1jfy56b1xfgxh7s2sj82.test", entry.key, null);
//                System.out.println(//response.object.data.toString() + "\t" + 
//                        entry.key + "\t" + 
//                        entry.owner.displayName + "\t" +
//                        entry.size + "\t" +
//                        entry.lastModified + "\t" + 
//                        entry.storageClass);
//            }
//            
//            System.out.println("Deleting file: ");
//            connection.delete("1jfy56b1xfgxh7s2sj82.test", "dissertation-data2.tgz",null);
//            
//            entries = connection.listBucket("1jfy56b1xfgxh7s2sj82.test", null,null,null,null).entries;
//            
//            for (ListEntry entry: entries) {
////                GetResponse response = connection.getACL("1jfy56b1xfgxh7s2sj82.test", entry.key, null);
//                System.out.println(//response.object.data.toString() + "\t" + 
//                        entry.key + "\t" + 
//                        entry.owner.displayName + "\t" +
//                        entry.size + "\t" +
//                        entry.lastModified + "\t" + 
//                        entry.storageClass);
//            }
            
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void uploadFile(AWSAuthConnection connection) throws IOException {
        File file = new File("/Users/dooley/deleteme/conditt.xls");
        InputStream in = new FileInputStream(file);
        System.out.println("Uploading file: " + file.getAbsolutePath());
        byte [] buffer       = new byte[32768];
        int bytes            = 0;
        long leftBytes      = file.length();
        long transferedBytes = 0;
        HttpURLConnection c  = connection.getOutputStream("test888", "conditt.xls2", null, null,file.length());
        OutputStream out = c.getOutputStream();
        while ( leftBytes > 0 ) {
            bytes = in.read(buffer);
            if(bytes <=0 ) {
                break;
            }
            if(leftBytes < bytes) {//For partial transfer 
                bytes = (int) leftBytes;
            }
            out.write(buffer, 0, bytes);

            leftBytes -= bytes;
            transferedBytes += bytes;
            System.out.println("Wrote " + transferedBytes + " bytes. Upload is " + new DecimalFormat("#00").format(((double)transferedBytes/(double)file.length())*100.0) + "% done.");
//            fireUrlTransferProgressEvent(transferedBytes);
            
        }
        out.flush();
        System.out.println("URL:" + c.getURL());
        System.out.println("Request Method:" + c.getRequestMethod());
//        System.out.println("Headers:");
//        for (String mapKey: c.getHeaderFields().keySet()) {
//          System.out.println("\t" + mapKey + ": " + c.getHeaderFields().get(mapKey));
//        }
//        System.setProperties(props)
//        if (c.getInputStream().available() > 0) {
//            bytes = c.getInputStream().read(buffer);
//            System.out.println(new String(buffer));
//        } else {
//            System.out.println("no output");
//        }
        
        
        in.close();
        out.close();
        
            
        
        
    }

}
