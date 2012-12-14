/**
 * 
 */
package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.text.DecimalFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.globus.ftp.FileInfo;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ConfigSettings;
import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.transfer.FTPType;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.server.servlet.model.resource.DBUtil;
import org.teragrid.service.profile.wsclients.SpeedpageClient;


/**
 * Dialog to dispaly bandwidth prediction between two sites.
 * 
 * @author dooley
 *
 */
@SuppressWarnings("serial")
public class DlgBandwidth extends DlgEscape {
	
	public static Double uploadSpeed;
	public static Double downloadSpeed;
	private JPanel pnlMain;
	/**
	 * 
	 */
	public DlgBandwidth(Frame frame, FTPSettings from, FTPSettings to, FileInfo file) {
		super(frame);
		
//		if (uploadSpeed == null) {
//			measureUploadSpeed();
//		}
//		
//		if (downloadSpeed == null) {
//			measureDownloadSpeed();
//		}
		setTitle("Transfer time prediction");
		
		pnlMain = new JPanel();
		pnlMain.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(7,7,7,7), 
				BorderFactory.createLineBorder(Color.GRAY)));
		pnlMain.setLayout(new BoxLayout(pnlMain,BoxLayout.Y_AXIS));
		pnlMain.setPreferredSize(new Dimension(325, 200));
		layoutComparisonPanel(from,to);
		
		// layout the bandwidth measurement
		double measurement = getPrediction(from,to);
		layoutMeasurementPanel(measurement);
		
		// layout the transfer time prediction
		long transferTime = predictTransferTime(measurement,file.getSize());
		layoutTransferTimePrediction(transferTime);
		
		add(pnlMain);
		
		locateDialog(frame);
		
		pack();
		setResizable(false);
		setModal(true);
		setVisible(true);
		
		
	}

	private void layoutComparisonPanel(FTPSettings from, FTPSettings to) {
		// layout both resources and their icons
		JPanel pnlCompare = new JPanel();
		pnlCompare.setLayout(new BoxLayout(pnlCompare,BoxLayout.X_AXIS));
		pnlCompare.setBorder(BorderFactory.createEmptyBorder(15,10,10,10));
		JLabel lblFrom = new JLabel(from.name);
		lblFrom.setIcon(getIconForResource(from));
		JLabel lblArrow = new JLabel();
		lblArrow.setIcon(AppMain.icoRightArrow);
		JLabel lblto = new JLabel(to.name);
		lblto.setIcon(getIconForResource(to));
		
		pnlCompare.add(Box.createHorizontalGlue());
		pnlCompare.add(lblFrom);
		pnlCompare.add(Box.createRigidArea(new Dimension(20,20)));
		pnlCompare.add(lblArrow);
		pnlCompare.add(Box.createRigidArea(new Dimension(20,20)));
		pnlCompare.add(lblto);
		pnlCompare.add(Box.createHorizontalGlue());
		pnlCompare.setAlignmentX(CENTER_ALIGNMENT);
		
		pnlMain.add(pnlCompare);
	}
	
	private void layoutMeasurementPanel(double measurement) {
		JPanel pnlBandwidthMeasurement = new JPanel();
		pnlBandwidthMeasurement.setLayout(new BoxLayout(pnlBandwidthMeasurement,BoxLayout.X_AXIS));
		pnlBandwidthMeasurement.setBorder(BorderFactory.createEmptyBorder(15,10,10,10));
		JLabel lblBandwidthMeasurement = new JLabel();
		
		if (measurement == Double.MAX_VALUE) {
			lblBandwidthMeasurement.setText("<html><div align='center'>This is essentially an internal copy and<br>" +
					"should be nearly instantaneous.</div></html>");
		} else if (measurement < 0) {
			lblBandwidthMeasurement.setText("<html><div align='center'>No measurement is available between these sytems.</div></html>");
		} else {
			lblBandwidthMeasurement.setText("<html><div align='center'>Measured bandwidth is " + 
					roundToOneDecimal(measurement) + " MB per second.</div></html>");
		}
		pnlBandwidthMeasurement.add(Box.createHorizontalGlue());
		pnlBandwidthMeasurement.add(lblBandwidthMeasurement);
		pnlBandwidthMeasurement.add(Box.createHorizontalGlue());
		pnlBandwidthMeasurement.setAlignmentX(CENTER_ALIGNMENT);
		
		pnlMain.add(pnlBandwidthMeasurement);
	}
	
	private void layoutTransferTimePrediction(long transferTime) {
		JPanel pnlPrediction = new JPanel();
		pnlPrediction.setLayout(new BoxLayout(pnlPrediction,BoxLayout.X_AXIS));
		pnlPrediction.setBorder(BorderFactory.createEmptyBorder(15,10,10,10));
		JLabel lblPrediction = new JLabel();
//		lblPrediction.setPreferredSize(new Dimension(275,50));
		
		if (transferTime < 0) {
//			lblPrediction.setText("<html><div align='center'>No prediction available.</div></html>");
		} else if (transferTime == 0) {
			lblPrediction.setText("<html><div align='center'>After connecting, this transfer will essentially be instant.</div></html>");
		} else {
			Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.add(Calendar.SECOND, (int)transferTime);
			
			String time = formatTime(cal);
			
			lblPrediction.setText("<html><div align='center'>After connecting, transfering this file will take approximately<br>" +
					time + "</div></html>");
		}
		
//		pnlPrediction.add(Box.createHorizontalGlue());
		pnlPrediction.add(lblPrediction);
//		pnlPrediction.add(Box.createHorizontalGlue());
		lblPrediction.setAlignmentX(RIGHT_ALIGNMENT);
		
		pnlMain.add(pnlPrediction);
		
	}
	
	private double getPrediction(FTPSettings from, FTPSettings to) {

		// query the middleware for the bandwidth prediction
		if (from.type == to.type && from.type == FTPType.GRIDFTP) {
		
			return queryService(from,to);
			
		// we default internal copies to 100mb, which is pretty close
		// for most hpc systems.
		} else if (from.type == to.type) {
		
			return Double.MAX_VALUE; 
		
		} else {
			// we can't accurately predict the other values without
			//doing a manual measurement which would take quite a while.
			return new Double (-1).doubleValue();
		}
		
	}
	
	/**
	 * Updated to use the IIS speedpage service 2/11/11
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	private float queryService(FTPSettings from, FTPSettings to) {
		LogManager.debug("Querying service for bandwidth between " + 
				from.name + " and " + to.name);

		if (from.name.equalsIgnoreCase(ConfigSettings.RESOURCE_NAME_LOCAL) ||
				from.name.equalsIgnoreCase(ConfigSettings.RESOURCE_NAME_AMAZONS3) ||
				from.name.equalsIgnoreCase(ConfigSettings.RESOURCE_NAME_TGSHARE) ||
				from.name.equalsIgnoreCase(ConfigSettings.RESOURCE_NAME_SRB) ||
				to.name.equalsIgnoreCase(ConfigSettings.RESOURCE_NAME_LOCAL) ||
				to.name.equalsIgnoreCase(ConfigSettings.RESOURCE_NAME_AMAZONS3) ||
				to.name.equalsIgnoreCase(ConfigSettings.RESOURCE_NAME_TGSHARE) ||
				to.name.equalsIgnoreCase(ConfigSettings.RESOURCE_NAME_SRB) || 
				from.hostType == DBUtil.ARCHIVE || to.hostType == DBUtil.ARCHIVE) {
			return -1;
		}
		
		// pull the latest measurements from speedpage's iis service
		SpeedpageClient speedpage = new SpeedpageClient(ConfigSettings.SERVICE_SPEEDPAGE);
		
		return speedpage.getTransferRate(from.resourceId, to.resourceId);
		
	}
	
	private long predictTransferTime(double measurement, long size) {
		if (measurement <= 0) {
			return -1; 
		} else {
			//TODO: figure in striping, buffer size, and parallelism
			return (long)(size / (measurement*1000000));
		}
	}
	
	private String formatTime(Calendar cal) {
		String time = "";
		if (cal.get(Calendar.DAY_OF_YEAR) > 2) {
			time += cal.get(Calendar.DAY_OF_YEAR)-1 + " days ";
		} else if (cal.get(Calendar.DAY_OF_YEAR) > 1) {
			time += "1 day ";
		}
		
		if (cal.get(Calendar.HOUR_OF_DAY) > 1) {
			time += cal.get(Calendar.HOUR_OF_DAY) + " hours ";
		} else if (cal.get(Calendar.HOUR_OF_DAY) > 0) {
			time += "1 hour ";
		}
		
		if (cal.get(Calendar.MINUTE) == 1) {
			time += "1 minute ";
		} else {
			time += cal.get(Calendar.MINUTE) + " minutes ";
		}
		
		if (cal.get(Calendar.SECOND) > 0) {
			time += cal.get(Calendar.SECOND) + " seconds ";
		}
		
		return time;
	}
	
	private ImageIcon getIconForResource(FTPSettings site) {
		ImageIcon ico = null;
		if (site.type == FTPType.FILE) {
			ico = AppMain.icoResourceLocal;
		} else if (site.type == FTPType.S3) {
			ico = AppMain.icoResourceAmazon;
		} else if (site.type == FTPType.XSHARE) {
			ico = AppMain.icoResourceTeraGridShare;
		} else if (site.hostType.equals(DBUtil.VIZ)) {
			ico = AppMain.icoResourceViz;
		} else if (site.hostType.equals(DBUtil.ARCHIVE)) {
			ico = AppMain.icoResourceArchive;
		} else {
			ico = AppMain.icoResourceCompute;
		}
		return ico;
	}
	
	private double roundToOneDecimal(double d) {
    	DecimalFormat oneDForm = new DecimalFormat("#.#");
    	return Double.valueOf(oneDForm.format(d));
	}

}
