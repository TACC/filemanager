/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package org.teragrid.portal.filebrowser.applet.transfer;

import java.awt.Component;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.globus.ftp.ByteRange;
import org.globus.ftp.FTPClient;
import org.globus.ftp.GridFTPSession;
import org.globus.ftp.StreamModeRestartMarker;
import org.globus.ftp.exception.NotImplementedException;
import org.globus.ftp.exception.ServerException;
import org.globus.io.streams.GlobusFileInputStream;
import org.globus.io.streams.GlobusInputStream;
import org.globus.io.streams.GlobusOutputStream;
import org.globus.io.urlcopy.UrlCopyListener;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ConfigOperation;
import org.teragrid.portal.filebrowser.applet.exception.UrlCopyException;
import org.teragrid.portal.filebrowser.applet.transfer.streams.BBFtpInputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.BBFtpOutputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.FTPInputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.FTPOutputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.GridFTPInputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.GridFTPOutputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.HTTPInputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.HTTPOutputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.IRODSInputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.IRODSOutputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.S3InputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.S3OutputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.SFTPInputStream;
import org.teragrid.portal.filebrowser.applet.transfer.streams.SFTPOutputStream;
import org.teragrid.portal.filebrowser.applet.ui.ListModel;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;

@SuppressWarnings("unchecked")
public class UrlCopy implements Runnable{

    protected int bufferSize           = 32768;
    protected boolean appendMode     = false;
    protected boolean canceled       = false;
    protected boolean thirdParty     = true;
    protected List listeners          = new LinkedList();
    protected FTPClient destFTP          = null;
    protected FTPClient srcFTP          = null;
    protected FTPSettings destServer     = null;
    protected FTPSettings srcServer     = null;
    protected String srcFile          = null;
    protected String desFile          = null;
    protected ByteRange range         = null;
    protected Component parent        = null;
    private FileTransferTask fileTask = null;
    private SHGridFTP srcConn = null;
    private SHGridFTP destConn = null;

    /**
     * Constructor
     * @param srcConn SHGridFTP
     * @param destConn SHGridFTP
     * @param fileTask FileTransferTask
     */
    public UrlCopy(SHGridFTP srcConn, SHGridFTP destConn, FileTransferTask fileTask){
        this.srcConn = srcConn;
        this.destConn = destConn;
        this.fileTask = fileTask;
        this.parent = destConn.getFtpServer().parent;
        if(fileTask.isResume()){
        	this.appendMode = true;
        }
       
        this.destFTP = destConn.getFtpClient();
        this.srcFTP = srcConn.getFtpClient();
        this.destServer = destConn.getFtpServer();
        this.srcServer = srcConn.getFtpServer();
		this.range = fileTask.getSrcRange();

//		if (fileTask.getFile() instanceof TGShareFileInfo) {
//			if (((TGShareFileInfo)fileTask.getFile()).isShared() && !((TGShareFileInfo)fileTask.getFile()).isOwner()) {
//				this.srcFile = fileTask.getSrcDir() + "/" + ((TGShareFileInfo)fileTask.getFile()).getNonce();
//			} else {
//				this.srcFile = fileTask.getSrcDir() + "/" + ListModel.getFileName(fileTask.getFile());
//			}
//		} else {
		this.srcFile = fileTask.getSrcDir() + "/" + ListModel.getFileName(fileTask.getFile());
//		}
		
        if(fileTask.getNewName() == null){
        	
            this.desFile = fileTask.getDestDir() + "/" + ListModel.getFileName(fileTask.getFile());
        }else{
            //the file has been renamed
            this.desFile = fileTask.getDestDir() + "/" + fileTask.getNewName();
        }
    }

    /**
     * Constructor
     * @param destConn SHGridFTP
     * @param srcConn SHGridFTP
     * @param rangeValue ByteRange
     */
    public UrlCopy(SHGridFTP destConn, SHGridFTP srcConn, ByteRange rangeValue) {
        this.destFTP = destConn.getFtpClient();
        this.srcFTP = srcConn.getFtpClient();
        this.destServer = destConn.getFtpServer();
        this.srcServer = srcConn.getFtpServer();
        this.range = rangeValue;
        this.parent = destConn.getFtpServer().parent;
    }

   /**
    * Adds url copy listener.
    *
    * @param listener url copy listener
    */
   public void addUrlCopyListener(UrlCopyListener listener) {
       if (this.listeners == null) {
    	   this.listeners = new LinkedList();
       }
       this.listeners.add(listener);
   }

   /**
    * Remove url copy listener
    *
    * @return listener url copy listener
    */
   public void removeUrlCopyListener(UrlCopyListener listener) {
       if (this.listeners == null) {
    	   return;
       }
       this.listeners.remove(listener);
   }

   /**
    * Sets buffer size for transfering data.
    * It does not set the TCP buffers.
    *
    * @param size size of the data buffer
    */
   public void setBufferSize(int size) {
	   this.bufferSize = size;
   }

   /**
    * Returns buffer size used for transfering
    * data.
    *
    * @return data buffer size
    */
   public int getBufferSize() {
       return this.bufferSize;
   }

   /**
    * Enables/disables append mode.
    *
    * @param appendMode if true, destination file
    *                   will be appended.
    */
   public void setAppendMode(boolean appendMode) {
       this.appendMode = appendMode;
   }

   /**
    * Checks if append mode is enabled.
    *
    * @return true if appending will be performed,
    *         false otherwise.
    */
   public boolean isAppendMode() {
       return this.appendMode;
   }

   /**
    * Enables/disables usage of third party transfers.
    *
    * @param thirdPary if true enable, false disable
    */
   public void setUseThirdPartyCopy(boolean thirdParty) {
       this.thirdParty  = thirdParty;
   }

   /**
    * Cancels the transfer in progress. If no transfer
    * is in progress it is ignored.
    */
   public void cancel() {
	   this.canceled = true;
   }

   /**
    * Checks if the transfer was canceled.
    *
    * @return true if transfer was canceled
    */
   public boolean isCanceled() {
       return this.canceled;
   }

   /**
    * Used for as a thread.
    */
   public void run() {
       try {
           copy();
       } catch(Exception e) {
           this.fireTransferErrorEvent(e);
       } finally {
    	   this.fireTransferCompletedEvent();
    	   this.listeners.clear();
       }
   }

   /**
    * Performs the copy function.
    * Source and destination urls must be specified otherwise
    * a exception is thrown. Also, if source and destination url
    * are ftp urls and thirdPartyCopy is enabled, third party transfer
    * will be performed. Urls, of course, must be of supported protocol.
    * Currently, gsiftp, ftp, https, http, and file are supported.
    *
    * @throws UrlCopyException in case of an error.
    */
   public void copy()
       throws UrlCopyException {

       if (this.srcFTP == null) {
           throw new UrlCopyException(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_URLCOPY_SOURCENOTSPECIFIED));
       }

       if (this.destFTP == null) {
           throw new UrlCopyException(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_URLCOPY_DESTNOTSPECIFIED));
       }
       
       // catch a 3rd party transfer when it's scheduled.
       //TODO: find a way to do a remote copy for transfers to/from the same resource. Right now, it relays through the client, which is bad for large files.
      if (thirdParty && (srcServer.type == FTPType.GRIDFTP) &&
              (destServer.type == FTPType.GRIDFTP) && 
               !srcServer.host.equals(destServer.host)) {
           thirdPartyTransfer();
           return;
       }
       
//      // uncomment when adding share support
//      if (srcServer.type == FTPType.XSHARE ||
//    		  destServer.type == FTPType.XSHARE) {
//    	  tgShareTransfer();
//    	  return;
//      }
      
       if (canUseIrodsThirdPartyTransfer()) {
           irodsThirdPartyTransfer();
           return;
       }
       
       GlobusInputStream in   = null;
       GlobusOutputStream out = null;
       boolean rs             = false;
       
       Exception _exception = null;
       int nRetry = 0;

       try {
           // make sure we're not trying a parallel connection to/from
           // a serial resource
           adjust2Party();
           
		   nRetry = this.srcServer.connRetry;
    	   do {
    		   _exception = null;
    		   try {
				in = getInputStream();
    		   } catch (Exception e) {
    			   _exception = e;
    			   nRetry--;
    			   Long start = new Date().getTime();
    			   while ((new Date().getTime() - start) < 100) {
//                     wait(100);
                   }
    		   }
    	   } while (_exception != null && nRetry > 0);
    	   if (_exception != null) {
    	       AppMain.Error(parent, "Can't open source file: " + _exception.getMessage());
        	   LogManager.debug("Can't open source file: " + this.srcFile);
    		   throw _exception;
    	   }

		   nRetry = this.destServer.connRetry;
    	   do {
    		   _exception = null;
    		   try {
    			   out = getOutputStream();
    		   } catch (Exception e) {
    			   _exception = e;
    			   nRetry--;
    			   Long start = new Date().getTime();
    			   while ((new Date().getTime() - start) < 100) {
//    			       wait(100);
    			   }
    		   }
    	   } while (_exception != null && nRetry > 0);
    	   
    	   if (_exception != null) {
    	       org.teragrid.portal.filebrowser.applet.AppMain.Error(parent,"Can't open destination file: " + _exception.getMessage());
    		   LogManager.debug("Can't open destination file: " + this.desFile);
    		   throw _exception;
    	   }

    	   rs = transfer(in, out);
    	   
           in.close();
           out.close();
       } catch(Exception e) {
    	   e.printStackTrace();
    	   
           if (in != null) {
        	   in.abort();
           } else {
        	   this.srcServer.removeConn(this.srcConn);
           }
           if (out != null) {
        	   out.abort();
           } else {
        	   this.destServer.removeConn(this.destConn);
           }
           if(rs){
               e.printStackTrace();
               org.teragrid.portal.filebrowser.applet.util.LogManager.debug(e.getLocalizedMessage() + " at " + (e.getStackTrace())[0]);  
               throw new UrlCopyException(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_URLCOPY_URLTRANSFERFAILED), e);
           }
           this.fireTransferErrorEvent(e);
       } finally {
			try {
				this.srcConn.close();
			} catch (Exception e) {
				LogManager.debug(this.srcConn.getFtpServer().host + ": " + e.getLocalizedMessage() + " at " + e.getStackTrace()[0]);
//				this.fireTransferErrorEvent(e);
			}
			this.srcConn.setIdle(true);
			
			try {
				this.destConn.close();
			} catch (Exception e) {
				LogManager.debug(this.destConn.getFtpServer().host + ": " + e.getLocalizedMessage() + " at " + e.getStackTrace()[0]);
//				this.fireTransferErrorEvent(e);
			}
			this.destConn.setIdle(true);
		}
       if (!rs && isCanceled()) {
           throw new UrlCopyException(SGGCResourceBundle.getResourceString(ResourceName.KEY_EXCEPTION_URLCOPY_TRANSFERABORT));
       }
   }

   public void setFiles(String src, String des) {
	   this.srcFile = src;
	   this.desFile = des;
   }

   /**
    * This function performs the actual transfer.
    * @param in GlobusInputStream
    * @param out GlobusOutputStream
    * @return boolean
    * @throws IOException
    */
   private boolean transfer(GlobusInputStream in, GlobusOutputStream out)
       throws IOException {
       
       byte [] buffer       = new byte[this.bufferSize];
       int bytes            = 0;
       long leftBytes      = this.range.to - this.range.from;
       long transferedBytes = this.range.from;

       if (this.range.to == -1) { //Unknown size
           while( (bytes = in.read(buffer)) != -1) {
               out.write(buffer, 0, bytes);
               out.flush();

               transferedBytes += bytes;
               fireUrlTransferProgressEvent(transferedBytes);

               if (isCanceled()) {
            	   return false;
               }
           }
       } else {
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
               fireUrlTransferProgressEvent(transferedBytes);
               
               if (isCanceled()) {
            	   return false;
               }
           }
           out.flush();
           
       }

       return true;
   }

   /**
    * Returns input stream based on the source url
    */
   protected GlobusInputStream getInputStream()
       throws Exception {

       GlobusInputStream in = null;

       switch(this.srcServer.type) {
       case FTPType.GRIDFTP:
            in = new GridFTPInputStream(this.srcFTP, this.srcFile, this.srcServer.passiveMode, this.range.from>0?new StreamModeRestartMarker(this.range.from):null);
            break;
        case FTPType.BBFTP:
            in = new BBFtpInputStream();
            break;
        case FTPType.SFTP:
            in = new SFTPInputStream();
            break;
        case FTPType.FTP:
            in = new FTPInputStream(this.srcFTP, this.srcFile, this.srcServer.passiveMode, this.range.from>0?new StreamModeRestartMarker(this.range.from):null);
            break;
        case FTPType.FILE:
            in = new GlobusFileInputStream(this.srcFile);
            long check = in.skip(this.range.from);
            if (check != this.range.from) {
                LogManager.error("actually skip: " + check);
            }
            break;
        case FTPType.HTTP:
            in = new HTTPInputStream(this.srcFTP, this.srcFile);
            break;
        case FTPType.S3:
            in = new S3InputStream(this.srcFTP,this.srcFile);
            break;
//        case FTPType.XSHARE:
//            in = new TGShareInputStream(this.srcFTP,this.srcFile);
//            break;
        case FTPType.IRODS:
            in = new IRODSInputStream(this.srcFTP,this.srcFile);
            break;
        default:

            break;
        }
       return in;
   }

   /**
    * Return the input stream from the source file
    * @param file String
    * @return GlobusInputStream
    */
   protected GlobusInputStream getInputStream(String fromFile, String type)
           throws Exception{
       GlobusInputStream in = null;
       return in;
   }

   /**
    * Get the size of the file to transfer
    * @return long
    * @throws Exception
    */
   protected long getTargetSize()
   throws Exception{
       GlobusInputStream in = null;
       long result = 0;

       switch(this.destServer.type) {
       case FTPType.GRIDFTP:
            in = new GridFTPInputStream(this.destFTP, this.desFile, this.destServer.passiveMode);
            break;
        case FTPType.BBFTP:
            in = new BBFtpInputStream();
            break;
        case FTPType.SFTP:
            in = new SFTPInputStream();
            break;
        case FTPType.FTP:
            in = new FTPInputStream(this.destFTP, this.desFile, this.destServer.passiveMode);
            break;
        case FTPType.FILE:
            in = new GlobusFileInputStream(this.desFile);
            break;
        case FTPType.HTTP:
            in = new HTTPInputStream(this.destFTP, this.desFile);
            break;
        case FTPType.S3:
            in = new S3InputStream(this.destFTP, this.desFile);
            break;
//        case FTPType.XSHARE:
//            in = new TGShareInputStream(this.destFTP, this.desFile);
//            break;
        case FTPType.IRODS:
            in = new IRODSInputStream(this.destFTP, this.desFile);
            break;
        default:

            break;
        }
        result  = in.getSize();
        //in.abort();
        //in.close();
        return result;
   }

   /**
    * Return the output stream for the destination file
    * @param file String
    * @param size long
    * @return GlobusOutputStream
    */
   protected GlobusOutputStream getOutputStream(String toFile, String type, long size)
           throws Exception{
       GlobusOutputStream out = null;

       return out;
   }


   /**
    * Returns output stream based on the destination url.
    * @return GlobusOutputStream
    * @throws Exception
    */
   protected GlobusOutputStream getOutputStream()
   throws Exception {

       GlobusOutputStream out = null;

       switch(this.destServer.type) {
       case FTPType.GRIDFTP:
            out = new GridFTPOutputStream(this.destFTP, this.desFile, this.destServer.passiveMode, this.appendMode);
            break;
        case FTPType.BBFTP:
            out = new BBFtpOutputStream();
            break;
        case FTPType.SFTP:
            out = new SFTPOutputStream();
            break;
        case FTPType.FTP:
            out = new FTPOutputStream(this.destFTP, this.desFile, this.destServer.passiveMode, this.appendMode);
            break;
        case FTPType.FILE:
//            out = new GlobusFileOutputStream(this.desFile, this.appendMode);
        	out = new GlobusRandomFileOutputStream(this.desFile, this.appendMode);
        	if (this.fileTask.getDstStartOffset() > 0) {
        		((GlobusRandomFileOutputStream)out).seek(this.fileTask.getDstStartOffset());
        	}
            break;
        case FTPType.HTTP:
            out = new HTTPOutputStream(this.destFTP, this.desFile, 0, true);
            break;
        case FTPType.S3:
            out = new S3OutputStream(this.destFTP,this.desFile,this.range.to);
            break;
//        case FTPType.XSHARE:
//            out = new TGShareOutputStream(this.destFTP,this.desFile,this.range.to);
//            break;
        case FTPType.IRODS:
            out = new IRODSOutputStream(this.destFTP,this.desFile);
            break;
        default:
            break;
        }
       return out;
   }

   /**
    * Fire an event to show the progress of the transfer
    * @param transferedBytes long
    */
   private void fireUrlTransferProgressEvent(long transferedBytes) {
       if(this.listeners == null) {
    	   return;
       }

       Iterator iter = this.listeners.iterator();
       while(iter.hasNext()) {
           ((UrlCopyListener)iter.next()).transfer(transferedBytes - this.range.from, this.range.to - this.range.from);
       }
   }

   /**
    * Fire an event when an error occurs durling transfer
    * @param e Exception
    */
   private void fireTransferErrorEvent(Exception e){
       if(this.listeners == null) {
    	   return;
       }

       Iterator iterator = this.listeners.iterator();
       while(iterator.hasNext()){
           ((UrlCopyListener)iterator.next()).transferError(e);
       }
   }

   /**
    * Fire an event to be catch when the transfer completes
    */
   private void fireTransferCompletedEvent(){
       if(this.listeners == null) {
    	   return;
       }

       Iterator iterator = this.listeners.iterator();
       while(iterator.hasNext()){
           ((UrlCopyListener)iterator.next()).transferCompleted();
       }
   }

   /**
    * This performs thrid party transfer only if source and destination urls
    * are ftp urls.
    */
   private void thirdPartyTransfer()
       throws UrlCopyException {

       LogManager.info("Trying third party transfer...");
       
       try {
           
//           not supported 
           // negotiateDCAU(srcFTP, dstFTP);

           destFTP.setType(GridFTPSession.TYPE_IMAGE);
//           destFTP.setMode(GridFTPSession.MODE_EBLOCK);
           ((GridFTP)destFTP).setTCPBufferSize(destServer.bufferSize*1024);
           ((GridFTP)destFTP).setParellel(ConfigOperation.getInstance().getSiteByName(destServer.name).connParallel);
           destFTP.setMode(GridFTPSession.MODE_EBLOCK);
           
           srcFTP.setType(GridFTPSession.TYPE_IMAGE);
//           srcFTP.setMode(GridFTPSession.MODE_EBLOCK);
           ((GridFTP)srcFTP).setTCPBufferSize(srcServer.bufferSize*1024);
           ((GridFTP)srcFTP).setParellel(ConfigOperation.getInstance().getSiteByName(srcServer.name).connParallel);
           srcFTP.setMode(GridFTPSession.MODE_EBLOCK);
           
//           LogManager.info("Enabling parallel transfers: \n" + 
//        		   ((GridFTP)srcFTP).getHost() + "::" + ((GridFTP)srcFTP).nParallel + "\n" + 
//        		   ((GridFTP)destFTP).getHost() + "::" + ((GridFTP)destFTP).nParallel);
           
           if (srcServer.stripeTransfers || destServer.stripeTransfers) {
               LogManager.info("Enabling striped transfer.");
               ((GridFTP)srcFTP).setStripedActive(((GridFTP)destFTP).setStripedPassive());
           }
           
           if (listeners != null) {
               fireUrlTransferProgressEvent(-1);
           }
           
           if (((GridFTP)srcFTP).getHost().equals(((GridFTP)destFTP).getHost())) {
//        	   ((GridFTP)destFTP).setPassive();
//        	   ((GridFTP)srcFTP).setActive();
        	   
        	   ((GridFTP)srcFTP).extendedTransfer(this.srcFile,
                       (GridFTP)destFTP,
                       this.desFile,
                       new MarkerListenerImpl(this.range, this.listeners, this));
           } else {
        	   ((GridFTP)srcFTP).extendedTransfer(this.srcFile,
                       (GridFTP)destFTP,
                       this.desFile,
                       new MarkerListenerImpl(this.range, this.listeners, this));
           }
           
       } catch(Exception e) {
           throw new UrlCopyException("UrlCopy third party transfer failed.",
                                      e);
       } finally {
           try {
               this.srcConn.close();
           } catch (Exception e) {
               LogManager.debug(this.srcConn.getFtpServer().host + ": " + e.getLocalizedMessage() + " at " + e.getStackTrace()[0]);
//             this.fireTransferErrorEvent(e);
           }
           this.srcConn.setIdle(true);
           
           try {
               this.destConn.close();
           } catch (Exception e) {
               LogManager.debug(this.destConn.getFtpServer().host + ": " + e.getLocalizedMessage() + " at " + e.getStackTrace()[0]);
//             this.fireTransferErrorEvent(e);
           }
           this.destConn.setIdle(true);
       }
   }
   
   @SuppressWarnings("unused")
   private void directUpload() throws UrlCopyException{
       
       try {
           destFTP.setType(GridFTPSession.TYPE_IMAGE);
           destFTP.setMode(GridFTPSession.MODE_EBLOCK);
           ((GridFTP)destFTP).setTCPBufferSize(destServer.bufferSize*1024);
           
           ((GridFTP)destFTP).put(srcFile,desFile,new MarkerListenerImpl(this.range, this.listeners, this),false);
       } catch (Exception e) {
            throw new UrlCopyException("UrlCopy direct upload failed.", e);
       } finally {
            try {
                this.srcConn.close();
            } catch (Exception e) {
                LogManager.debug(this.srcConn.getFtpServer().host + ": " + e.getLocalizedMessage() + " at " + e.getStackTrace()[0]);
                //this.fireTransferErrorEvent(e);
            }
            this.srcConn.setIdle(true);
            
            try {
                this.destConn.close();
            } catch (Exception e) {
                LogManager.debug(this.destConn.getFtpServer().host + ": " + e.getLocalizedMessage() + " at " + e.getStackTrace()[0]);
                //this.fireTransferErrorEvent(e);
            }
            this.destConn.setIdle(true);
        }
   }
   
   private void adjust2Party() throws IOException, ServerException {
       if (srcServer.type == FTPType.FILE && destServer.type == FTPType.FILE) {
           return;
       }
       
       if (srcServer.type == FTPType.FILE && 
               destServer.connParallel > 1) {
           ((GridFTP)destFTP).setParellel(1);
       } else if (destServer.type == FTPType.FILE && 
               srcServer.connParallel > 1) {
           ((GridFTP)srcFTP).setParellel(1);
       }
   }
   
   
   private boolean canUseIrodsThirdPartyTransfer() {
       return (srcServer.type == FTPType.IRODS && destServer.type == FTPType.IRODS);
   }
       
   private void irodsThirdPartyTransfer() throws UrlCopyException{
       try {
           if (srcServer.type == destServer.type) {
               ((Irods)srcFTP).copy(srcFile, desFile, true);
//        	 } else if (srcServer.type == FTPType.IRODS) {
//               String destUriString = "gridftp://" + destServer.host + "/" + desFile;
//               URI destUri = new URI(destUriString);
//               ((Irods)srcFTP).copyToURI(srcFile, destUri);
           } else {
//               String srcUriString = "gridftp://" + srcServer.host + "/" + desFile;
//               URI srcUri = new URI(srcUriString);
//               ((Irods)srcFTP).copyFromURI(desFile, srcUri);
        	   throw new NotImplementedException();
           }
       } catch (NotImplementedException e) {
    	   throw new UrlCopyException("Third party IRODS transfers are note yet supported.",e);
       } catch(Exception e) {
           throw new UrlCopyException("UrlCopy third party transfer failed.",e);
       } finally {
           try {
               this.srcConn.close();
           } catch (Exception e) {
               LogManager.debug(this.srcConn.getFtpServer().host + ": " + e.getLocalizedMessage() + " at " + e.getStackTrace()[0]);
//                     this.fireTransferErrorEvent(e);
           }
           this.srcConn.setIdle(true);
           
           try {
               this.destConn.close();
           } catch (Exception e) {
               LogManager.debug(this.destConn.getFtpServer().host + ": " + e.getLocalizedMessage() + " at " + e.getStackTrace()[0]);
//                     this.fireTransferErrorEvent(e);
           }
           this.destConn.setIdle(true);
       }
   }
   
//   // uncomment when adding share support
//   private void tgShareTransfer() throws UrlCopyException {
//	   try {
//           if (srcServer.type == destServer.type) {
//               ((TGShare)srcFTP).copy(srcFile, desFile);
//           } else if (srcServer.type == FTPType.XSHARE) {
//               ((TGShare)srcFTP).get(srcFile, getOutputStream());
//           } else {
//        	   // for some reason, streaming upload won't work, so we have to just copy
//        	   // it to disk, then send the file...weak!
//        	   if (srcConn.getFtpServer().isLocal()) {
//        		   ((TGShare)destFTP).put(new File(srcFile), desFile, false);
//        	   } else {
//	        	   InputStream in = getInputStream();
//	        	   
//	        	   File tmpFile = new File(ConfigOperation.getInstance().getDataDir() + File.separator + new File(desFile).getName());
//	        	  
//	        	   if (!tmpFile.exists()) {
//	        		   tmpFile.createNewFile();
//	        	   }
//	        	   
//	        	   FileOutputStream out = new FileOutputStream(tmpFile);
//	        	   byte[] tmp = new byte[4096];
//	        	   int len;
//	        	   while ((len = in.read(tmp)) >= 0) {
//	        		   out.write(tmp,0,len);
//	        	   }
//	        	   out.close();
//	        	   in.close();
//	        	   
//	               ((TGShare)destFTP).put(tmpFile, desFile, false);
//	               
//	               tmpFile.delete();
//        	   }
//           }
//       } catch(Exception e) {
//           throw new UrlCopyException("Share transfer failed.",e);
//       } finally {
//           try {
//               this.srcConn.close();
//           } catch (Exception e) {
//               LogManager.debug(this.srcConn.getFtpServer().host + ": " + e.getLocalizedMessage() + " at " + e.getStackTrace()[0]);
//           }
//           this.srcConn.setIdle(true);
//           
//           try {
//               this.destConn.close();
//           } catch (Exception e) {
//               LogManager.debug(this.destConn.getFtpServer().host + ": " + e.getLocalizedMessage() + " at " + e.getStackTrace()[0]);
////                     this.fireTransferErrorEvent(e);
//           }
//           this.destConn.setIdle(true);
//       }
//   }
   
}

