/* 
 * Created on Aug 4, 2008
 * 
 * Developed by: Rion Dooley - dooley [at] tacc [dot] utexas [dot] edu
 * 				 TACC, Texas Advanced Computing Center
 * 
 * https://www.tacc.utexas.edu/
 */

package org.teragrid.portal.filebrowser.applet.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.GZIPInputStream;

import javax.swing.Icon;

import org.globus.ftp.FileInfo;

import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;

/**
 * Insert Template description here.
 * 
 * @author Rion Dooley < dooley [at] tacc [dot] utexas [dot] edu >
 * 
 */
public class FileUtils {

	public static void copy(String src, String dest) throws IOException {
		copy(new File(src), new File(dest));
	}

	public static void copy(File src, File dest) throws IOException {
		// if the source directory doesn't exist, then there will be no
		// copy, so throw an exception.
		if (src == null) {
			throw new NullPointerException("Source location cannot be null");
		}
		if (!src.exists()) {
			throw new IOException("Source location " + src.getAbsolutePath()
					+ " does not exist.");
		}
		if (dest == null) {
			throw new NullPointerException(
					"Destination location cannot be null");
		}

		// source is a directory
		if (src.isDirectory()) {
			if (!dest.exists()) {
				if (!dest.mkdirs()) {
					throw new IOException(
							"Failed to create destination directory "
									+ dest.getAbsolutePath());
				} else {
					copyDirectory(src, dest);
				}
			} else {
				if (!dest.isDirectory()) {
					throw new IOException("Cannot copy a directory to a file.");
				}

				copyDirectory(src, dest);
			}
			// source is a file
		} else {
			if (!dest.exists()) {
				if (!dest.createNewFile()) {
					throw new IOException("Failed to create destination file "
							+ dest.getAbsolutePath());
				} else {
					copyFile(src, dest);
				}
			} else {
				if (dest.isDirectory()) {
					copyFile(src, new File(dest, src.getName()));
				}
				if (!dest.isFile()) {
					throw new IOException("Cannot copy a directory to a file.");
				} else {
					copyFile(src, dest);
				}
			}
		}
	}

	public static void copyDirectory(String src, String dest)
			throws IOException {
		copyDirectory(new File(src), new File(dest));
	}

	public static void copyDirectory(File srcDir, File destDir)
			throws IOException {
		// if the source directory doesn't exist, then there will be no
		// copy, so throw an exception.
		if (srcDir == null) {
			throw new NullPointerException("Source directory cannot be null");
		}
		if (!srcDir.exists()) {
			throw new IOException("Source directory "
					+ srcDir.getAbsolutePath() + " does not exist.");
		}
		if (!srcDir.isDirectory()) {
			throw new IOException("Source " + srcDir.getAbsolutePath()
					+ " is not a directory.");
		}

		// Just create the destination directories. If they are not there, then
		// we need to throw an exception because we can't copy.
		if (destDir == null) {
			throw new NullPointerException(
					"Destination directory cannot be null");
		}
		if (!destDir.exists()) {
			if (!destDir.mkdirs()) {
				throw new IOException("Destination directory "
						+ destDir.getAbsolutePath() + " does not exist.");
			}
		}

		if (!destDir.isDirectory()) {
			throw new IOException("Destination " + destDir.getAbsolutePath()
					+ " is not a directory.");
		}

//		LogManager.info("Copying directory " + srcDir.getAbsolutePath()
//				+ " to " + destDir.getAbsolutePath());

		for (File srcFile : Arrays.asList(srcDir.listFiles())) {
			if (srcFile.isDirectory()) {
				File newDir = new File(destDir, srcFile.getName());
				newDir.mkdir();
				copyDirectory(srcFile, newDir);
			} else {
				copyFile(srcFile, new File(destDir, srcFile.getName()));
			}
		}
		destDir.setLastModified(srcDir.lastModified());
	}

	public static void copyFile(String src, String dest) throws IOException {
		copyFile(new File(src), new File(dest));
	}

	public static void copyFile(File src, File dest) throws IOException {
		// if the source file doesn't exist, then there will be no
		// copy, so throw an exception.
		if (src == null) {
			throw new NullPointerException("Source file cannot be null");
		}

		if (!src.exists()) {
			throw new IOException("Source file " + src.getAbsolutePath()
					+ " does not exist.");
		}

		if (!src.isFile()) {
			throw new IOException("Source " + src.getAbsolutePath()
					+ " is not a file.");
		}

		// Just create the destination file. If it is not there, then
		// we need to throw an exception because we can't copy.

		if (dest == null) {
			throw new NullPointerException("Destination file cannot be null");
		}

		if (dest.exists()) {
			if (!dest.isFile()) {
				throw new IOException("Destination " + dest.getAbsolutePath()
						+ " is not a file.");
			}
		} else {
			if (!dest.getParentFile().exists()) {
				if (!dest.getParentFile().mkdirs()) {
					throw new IOException("Destination directory "
							+ dest.getParentFile().getAbsolutePath()
							+ " does not exist.");
				}
			}
			if (!dest.createNewFile()) {
				throw new IOException("Destination file "
						+ dest.getAbsolutePath() + " does not exist.");
			}
		}

//		LogManager.info("Copying file " + src.getAbsolutePath() + " to "
//				+ dest.getAbsolutePath());

		InputStream fis = new FileInputStream(src);
		FileOutputStream fos = new FileOutputStream(dest);
		byte[] bytes = new byte[128];
		@SuppressWarnings("unused")
		int dataRead = 0;
		while ((dataRead = fis.read(bytes, 0, bytes.length)) != -1) {
			fos.write(bytes,0,bytes.length);
		}
		fis.close();
		fos.close();
		dest.setLastModified(src.lastModified());
	}

	public static void extractJarEntry(JarFile srcJarFile, String srcEntry,
			String dest) throws IOException {
		if (srcJarFile == null) {
			throw new NullPointerException("Jar file cannot be null.");
		}

		if (srcEntry == null) {
			throw new NullPointerException("Jar entry cannot be null.");
		}

		JarEntry srcJarEntry = srcJarFile.getJarEntry(srcEntry);

		if (srcJarEntry == null) {
			throw new IOException("No entry matching " + srcEntry + " found.");
		}

		if (dest == null) {
			throw new NullPointerException("Destination cannot be null.");
		}

		extractJarEntry(srcJarFile, srcJarEntry, new File(dest));
	}
	
	/**
	 * Recursive deletion of a file or folder.
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteRecursive(String path) throws Exception {
		File file = new File(path);
		if (file.isDirectory()) {
			for (File child: file.listFiles()) {
				FileUtils.deleteRecursive(child.getPath());
			}
		}
		return file.delete();
	}
	
	public static boolean delete(String path) throws Exception {
		return new File(path).delete();
	}

	public static void extractJarEntry(JarFile srcJarFile,
			JarEntry srcJarEntry, File dest) throws IOException {
		if (srcJarFile == null) {
			throw new NullPointerException("Jar file cannot be null");
		}

		if (srcJarEntry == null) {
			throw new NullPointerException("Jar entry cannot be null");
		}

		if (dest == null) {
			throw new NullPointerException("Destination file cannot be null");
		}

		if (dest.exists()) {
			if (!dest.isFile()) {
				throw new IOException("Destination " + dest.getAbsolutePath()
						+ " is not a file.");
			}
		} else {
			if (!dest.getParentFile().exists()) {
				if (!dest.getParentFile().mkdirs()) {
					throw new IOException("Destination directory "
							+ dest.getParentFile().getAbsolutePath()
							+ " does not exist.");
				}
			}
			if (!dest.createNewFile()) {
				throw new IOException("Destination " + dest.getAbsolutePath()
						+ " does not exist.");
			}
		}

		int dataRead;
		LogManager.info("Unpacking jar entry " + srcJarEntry.getName() + " to "
				+ dest.getAbsolutePath());

		InputStream jis = srcJarFile.getInputStream(srcJarEntry);
		FileOutputStream fos = new FileOutputStream(dest);
		while ((dataRead = jis.read()) != -1) {
			fos.write(dataRead);
		}
		jis.close();
		fos.close();
		dest.setLastModified(srcJarEntry.getTime());
	}

	public static void unpackJarEntry(JarFile srcJarFile, String srcEntry,
			String dest) throws IOException {
		String tmpJarPath = dest + File.separator + "tmp.jar";

		extractJarEntry(srcJarFile, srcEntry, tmpJarPath);

		unpack(new JarFile(tmpJarPath), dest);

	}

	public static void unpack(JarFile srcJarFile, String dest)
			throws IOException {

		unpack(srcJarFile, new File(dest));

	}

	public static void unpack(JarFile srcJarFile, File dest) throws IOException {
		if (srcJarFile == null) {
			throw new NullPointerException("Jar file cannot be null");
		}

		if (dest.exists()) {
			if (!dest.isDirectory()) {
				throw new IOException("Destination " + dest.getAbsolutePath()
						+ " is not a directory.");
			}
		} else {
			if (!dest.exists()) {
				if (!dest.mkdirs()) {
					throw new IOException("Destination directory "
							+ dest.getParentFile().getAbsolutePath()
							+ " does not exist.");
				}
			}
		}

		LogManager.info("Unpacking jar file " + srcJarFile.getName() + " to "
				+ dest.getAbsolutePath());

		Enumeration<JarEntry> entries = srcJarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();

			if (entry.getName().indexOf("META-INF") > -1) {
				continue;
			}

			File newDir = new File(dest, entry.getName());
			if (entry.isDirectory()) {
				LogManager.info("Creating directory " + dest.getAbsolutePath()
						+ File.separator + entry.getName());
				newDir.mkdirs();
				newDir.setLastModified(entry.getTime());
				continue;
			}

			extractJarEntry(srcJarFile, entry.getName(), newDir
					.getAbsolutePath());
		}
	}

	public static void unpackTGZ(String zipFile, String dest) throws Exception {
		InputStream in = getInputStream(zipFile);
		unTar(in, dest);
	}

	public static void unpackTGZ(String zipFile, File dest) throws Exception {
		unpackTGZ(zipFile, dest.getAbsolutePath());
	}

	public static String getPackedFolderName(String zipFile) throws Exception {
		InputStream in = getInputStream(zipFile);
		TarInputStream tin = new TarInputStream(in);
		TarEntry tarEntry = tin.getNextEntry();
		return tarEntry.getName();
		
	}
	
	public static InputStream getInputStream(String tarFileName)
			throws Exception {
		if (tarFileName.substring(tarFileName.lastIndexOf(".") + 1,
				tarFileName.lastIndexOf(".") + 3).equalsIgnoreCase("gz")) {
			return new GZIPInputStream(new FileInputStream(
					new File(tarFileName)));
		} else {
			return new FileInputStream(new File(tarFileName));
		}
	}

	public static void unTar(InputStream in, String untarDir)
			throws IOException {
		LogManager.debug("Reading TarInputStream...");
		TarInputStream tin = new TarInputStream(in);
		TarEntry tarEntry = tin.getNextEntry();
		if (new File(untarDir).exists()) {
			while (tarEntry != null) {
				if (!tarEntry.getName().endsWith(".crl_url") && !tarEntry.getName().endsWith(".info")) {
					File destPath = new File(untarDir + File.separatorChar
							+ tarEntry.getName());
					//LogManager.info("Processing " + destPath.getAbsoluteFile());
					if (!tarEntry.isDirectory()) {
						FileOutputStream fout = new FileOutputStream(destPath);
						tin.copyEntryContents(fout);
						fout.close();
					} else {
						destPath.mkdir();
					}
				}
				tarEntry = tin.getNextEntry();
			}
			tin.close();
		} else {
			System.out.println("That destination directory doesn't exist! "
					+ untarDir);
		}
	}

	public static void download(URL remoteFile, String dest) throws IOException {
		OutputStream out = null;
		URLConnection conn = null;
		InputStream in = null;
		try {
			File certBundle = new File(dest);

			if (certBundle.exists())
				certBundle.delete();
			certBundle.createNewFile();

			out = new BufferedOutputStream(new FileOutputStream(certBundle));

			conn = remoteFile.openConnection();

			InputStream instream = conn.getInputStream();

			byte[] b = new byte[1024];
			int numRead;
			while ((numRead = instream.read(b)) != -1) {
				out.write(b, 0, numRead);
			}
			out.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
			}
		}
	}
	
	public static Icon getLocalFileIcon(FileInfo fileItem) {
		if (fileItem.isDirectory()) return null;
		
		String filename = fileItem.getName();
		
		if (filename.equals("..") || filename.equals(".") || filename.equals("")) return null;
		
		Icon icon = null;
		try {
			
			String ext;
			String basename;
			File file;
			if (filename.startsWith(".") || filename.lastIndexOf(".") == -1) {
				ext = "";
				basename = filename;
				
				//Create a temporary file with the specified extension
				file = File.createTempFile("tmp_" + basename, "");
			
			} else {
				ext = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
				basename = filename.substring(0, filename.lastIndexOf('.') - 1);
				
				//Create a temporary file with the specified extension
				file = File.createTempFile("tmp_" + basename, "." + ext);
				
			}
			
			javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
			icon = fc.getUI().getFileView(fc).getIcon(file);
	
			//Delete the temporary file
			file.delete();
		} catch (IOException ioe) {}

		return icon;

	}
}
