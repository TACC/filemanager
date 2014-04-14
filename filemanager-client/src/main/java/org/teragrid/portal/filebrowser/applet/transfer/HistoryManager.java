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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.restlet.resource.ClientResource;
import org.teragrid.portal.filebrowser.applet.AppMain;
import org.teragrid.portal.filebrowser.applet.ConfigOperation;
import org.teragrid.portal.filebrowser.applet.ui.DrawState;
import org.teragrid.portal.filebrowser.applet.util.LogManager;
import org.teragrid.portal.filebrowser.applet.util.ServletUtil;
import org.teragrid.portal.filebrowser.applet.util.SwingWorker;

import edu.utexas.tacc.wcs.filemanager.common.model.BulkNotificationRequest;
import edu.utexas.tacc.wcs.filemanager.common.model.BulkTransferRequest;
import edu.utexas.tacc.wcs.filemanager.common.model.Task;
import edu.utexas.tacc.wcs.filemanager.common.model.Transfer;
import edu.utexas.tacc.wcs.filemanager.common.model.enumeration.NotificationType;
import edu.utexas.tacc.wcs.filemanager.service.resources.BulkAddNotificationsResource;
import edu.utexas.tacc.wcs.filemanager.service.resources.BulkDeleteNotificationsResource;
import edu.utexas.tacc.wcs.filemanager.service.resources.BulkTransfersResource;
import edu.utexas.tacc.wcs.filemanager.service.resources.TransfersResource;

public class HistoryManager {
	private static List<FileTransferTask> taskList = Collections.synchronizedList(new ArrayList<FileTransferTask>());
	private static List<DrawState> statusList = Collections.synchronizedList(new ArrayList<DrawState>());

	/**
	 * Get the TaskList
	 * 
	 * @return List - Return all FileTransferTask
	 */
	public static List<FileTransferTask> getTaskList() {
		return taskList;
	}

	/**
	 * Get the list for DrawState Objects
	 * 
	 * @param tasks
	 *            List
	 */
	public static List<DrawState> getStatusList() {
		return statusList;
	}

	/**
	 * Add a list of tasks to the tasklist
	 * 
	 * @param tasks
	 *            List - the task lists to be added
	 */
	public static void addTasks(List<FileTransferTask> tasks) {

		for (FileTransferTask task : tasks) {
			String filename = task.getFile().getName();
			if (filename.equals(".") || filename.equals("..")) {
				tasks.remove(task);
			}
		}

		// log this transfer on the middleware service
		register(tasks);

	}

	private static void updateTaskList(List<FileTransferTask> tasks) {
		for (FileTransferTask task: tasks) {
			if (task.getId() == null) task.setId((long)taskList.size());
			LogManager.debug("New transfer id = " + task.getId());
			
			taskList.add(task);
			statusList.add(task.task2State());
		}

	}

	/**
	 * Add a task to the tasklist
	 * 
	 * @param task
	 *            FileTransferTask - the task to be added
	 */
	public static void addTask(FileTransferTask task) {
		String filename = task.getFile().getName();
		if (filename.equals(".") || filename.equals("..")) {
			return;
		}

		// log this transfer on the middleware service
		register(Arrays.asList(task));

		LogManager.debug("New transfer id = " + task.getId());
	}

	// private void updateTaskList(FileTransferTask tasks) {
	// taskList.add(task);
	// statusList.add(task.task2State());
	//        
	// }

	/**
	 * delete a transfer task
	 * 
	 * @param task
	 *            FileTransferTask - the task to be deleted
	 */
	public static void deleteTask(FileTransferTask task) {
		task.cancel();
		taskList.remove(task);
		if (task.getId() > -1) {
			ArrayList<Long> ids = new ArrayList<Long>();
			ids.add(task.getId());
			deregister(ids);
		}
	}

	/**
	 * delete a transfer task
	 * 
	 * @param index
	 *            int - the index of the task to be deleted
	 */
	public static void deleteTask(int index) {
		deleteTasks(new int[] { index });
	}

	/**
	 * delete a transfer task
	 * 
	 * @param taskIndeces
	 *            int - the index of the task to be deleted
	 */
	public static void deleteTasks(int[] taskIndecies) {

		List<Long> taskIds = new ArrayList<Long>();

		for (int i = 0; i < taskIndecies.length; i++) {
			if (taskIndecies[i] < 0 || taskIndecies[i] >= taskList.size()) {
				continue;
			}

			FileTransferTask fileTask = taskList.get(taskIndecies[i]);

			if (fileTask.getStatus() != Task.DONE
					&& fileTask.getStatus() != Task.FAILED
					&& fileTask.getStatus() != Task.STOPPED) {
				fileTask.kill();
			}
			if (fileTask.getId() > -1)
				taskIds.add(fileTask.getId());
		}

		deregister(taskIds);

		for (int i = 0; i < taskIndecies.length; i++) {
			if (taskIndecies[i] < 0 || taskIndecies[i] >= taskList.size()) {
				continue;
			}
			taskList.remove(taskIndecies[i]);
		}

	}

	public static void deleteTasks(List<FileTransferTask> tasks) {
		for (FileTransferTask task : tasks) {
			deleteTask(task);
		}
	}

	/**
	 * delete all transfer tasks
	 */
	public static void deleteAllTasks() {
		for (FileTransferTask fileTask : taskList) {
			if (fileTask.getStatus() != Task.DONE
					&& fileTask.getStatus() != Task.FAILED
					&& fileTask.getStatus() != Task.STOPPED) {
				fileTask.cancel();
			}
		}

		clearHistory();

		taskList.clear();
	}

	/**
	 * delete all finished tasks
	 */
	public static void deleteFinishedTasks() {
		ArrayList<FileTransferTask> tasks = new ArrayList<FileTransferTask>();

		for (int i = 0; i < taskList.size();) {
			FileTransferTask fileTask = (FileTransferTask) taskList.get(i);
			if (fileTask.getStatus() == Task.DONE) {
				taskList.remove(fileTask);
				tasks.add(fileTask);
			} else {
				i++;
			}
		}

		// delete on the server
		ArrayList<Long> ids = new ArrayList<Long>();
		
		for (FileTransferTask fileTask : tasks) {
			if (fileTask.getId() > -1)
				ids.add(Long.valueOf(fileTask.getId()));
		}

		deregister(ids);
	}

	/**
	 * stop a task
	 * 
	 * @param taskIndecies
	 *            int
	 */
	public static void stopTasks(int[] taskIndecies) {
		List<FileTransferTask> tasks = new ArrayList<FileTransferTask>();

		for (int i = 0; i < taskIndecies.length; i++) {
			if (taskIndecies[i] < 0 || taskIndecies[i] >= taskList.size()) {
				return;
			}

			FileTransferTask fileTask = taskList.get(taskIndecies[i]);

			if (fileTask.getStatus() != Task.DONE
					&& fileTask.getStatus() != Task.FAILED
					&& fileTask.getStatus() != Task.STOPPED) {
				fileTask.kill();
				if (fileTask.getId() > -1)
					tasks.add(fileTask);

			}
		}

		update(tasks);
	}

	/**
	 * Make an RPC call to the TG File History servlet and retrieve the user's
	 * file transfer history.
	 */
	public static void refreshTaskList() 
	{	
		if (!ConfigOperation.isLoggingEnabled()) return;
		
		try 
		{
			taskList.clear();
			
			ClientResource clientResource = ServletUtil.getClient(ServletUtil.TRANSFERS_SERVICE_URL);
			BulkTransfersResource client = clientResource.wrap(BulkTransfersResource.class);
			List<Transfer> transfers = client.getAllTransfers();
			for(Transfer transfer: transfers) {
				taskList.add(new FileTransferTask(transfer));
			}
		} 
		catch (Exception e)
		{
			if (ConfigOperation.isLoggingEnabled()) {
//				AppMain.enableLogging(AppMain.Confirm(
//						AppMain.getFrame(),
//						"There was an error connecting with the\n" +
//						"middleware. Would you like to work offline?",
//						"Transfer History Error") != 0);
				LogManager.error("Failed to refresh the user's file transfer history.", e);
			}
		}
	
		for (FileTransferTask task : taskList) {
			String name = ConfigOperation.getInstance().getSiteName(
					task.getSrcSite().host);
			if (name != null) {
				task.getSrcSite().name = name;
			}
			name = ConfigOperation.getInstance().getSiteName(
					task.getDestSite().host);
			if (name != null) {
				task.getDestSite().name = name;
			}
		}
	}

	/**
	 * Make an RPC call to the TG File History servlet and create a notificaiton
	 * event for the file transfer completion.
	 */
	public static void setNotification(final Long id,
			final NotificationType type, final boolean enable) {

		if (!ConfigOperation.isLoggingEnabled())
			return;

		SwingWorker worker = new SwingWorker() {
			@SuppressWarnings("unchecked")
			@Override
			public Object construct() {
				try 
				{	
					List<Long> ids = new ArrayList<Long>();
					ids.add(id);
					
					if (enable) {
						ClientResource clientResource = ServletUtil.getClient(ServletUtil.NOTIFICATIONS_ADD_SERVICE_URL);
						BulkAddNotificationsResource client = clientResource.wrap(BulkAddNotificationsResource.class);
						BulkNotificationRequest bulkNotificationRequest = new BulkNotificationRequest(ids, type);
						client.addAll(bulkNotificationRequest);
					} else {
						ClientResource clientResource = ServletUtil.getClient(ServletUtil.NOTIFICATIONS_DELETE_SERVICE_URL);
						BulkDeleteNotificationsResource client = clientResource.wrap(BulkDeleteNotificationsResource.class);
						BulkNotificationRequest bulkNotificationRequest = new BulkNotificationRequest(ids, type);
						client.removeAll(bulkNotificationRequest);
					}
				} 
				catch (Exception e) 
				{
					if (ConfigOperation.isLoggingEnabled()) {
//						AppMain.enableLogging(AppMain.Confirm(
//									AppMain.getFrame(),
//									"There was an error registering the\n" +
//									"notification. Would you like to work offline?",
//									"Notification Error") != 0);
						LogManager.error("Failed to update notification.", e);
					}
				}

				return null;
			}
		};

		worker.start();
	}

	/**
	 * Make an RPC call to the TG File History servlet and create a notificaiton
	 * event for the file transfers listed.
	 */
	public static void setNotification(final List<Long> ids,
			final NotificationType type, final boolean enable) {

		if (!ConfigOperation.isLoggingEnabled())
			return;

		SwingWorker worker = new SwingWorker() {
			@Override
			public Object construct() {
				try 
				{
					if (enable) {
						ClientResource clientResource = ServletUtil.getClient(ServletUtil.NOTIFICATIONS_ADD_SERVICE_URL);
						BulkAddNotificationsResource client = clientResource.wrap(BulkAddNotificationsResource.class);
						BulkNotificationRequest bulkNotificationRequest = new BulkNotificationRequest(ids, type);
						client.addAll(bulkNotificationRequest);
					} else {
						ClientResource clientResource = ServletUtil.getClient(ServletUtil.NOTIFICATIONS_DELETE_SERVICE_URL);
						BulkDeleteNotificationsResource client = clientResource.wrap(BulkDeleteNotificationsResource.class);
						BulkNotificationRequest bulkNotificationRequest = new BulkNotificationRequest(ids, type);
						client.removeAll(bulkNotificationRequest);
					}
				} 
				catch (Exception e) 
				{
					if (ConfigOperation.isLoggingEnabled()) {
//						AppMain
//							.enableLogging(AppMain.Confirm(
//									AppMain.getFrame(),
//									"There was an error registering the\n" +
//									"notification. Would you like to work offline?",
//									"Notification Error") != 0);
						LogManager.error("Failed to save the user's historical file transfer record.", e);
					}
				}
				return null;
			}
		};

		worker.start();
	}

	/**
	 * Make an RPC call to the TG File History servlet and create a log the
	 * transfer of the given file.
	 */
	public static void register(final List<FileTransferTask> tasks) {

		if (!ConfigOperation.isLoggingEnabled()) {
			updateTaskList(tasks);
			return;
		}

		SwingWorker worker = new SwingWorker() {
			@SuppressWarnings("unchecked")
			@Override
			public Object construct() {
				try 
				{
					List<Transfer> transfers = new ArrayList<Transfer>();

					for (FileTransferTask fileTransferTask : tasks) {
						transfers.add(fileTransferTask.toTransfer());
					}
					
					ClientResource clientResource = ServletUtil.getClient(ServletUtil.TRANSFERS_SERVICE_URL);
					BulkTransfersResource client = clientResource.wrap(BulkTransfersResource.class);
					NotificationType notificationType = null;
					try {
						notificationType = NotificationType.valueOf(ConfigOperation.getInstance().getConfigValue("notification"));
					} catch (Exception e) {
						notificationType = NotificationType.NONE;
					}
					BulkTransferRequest bulkTransferRequest = new BulkTransferRequest(transfers, "", notificationType);
					List<Long> transferIds = client.addMultipleTransfers(bulkTransferRequest);
					
					LogManager.debug("Sending transfers to service for logging");
					
					for (int i = 0; i < transferIds.size(); i++) {
						tasks.get(i).setId(transferIds.get(i));
					}

					return tasks;

				} 
				catch (Exception e) 
				{
					if (ConfigOperation.isLoggingEnabled())  {
//						AppMain
//							.enableLogging(AppMain.Confirm(
//											AppMain.getFrame(),
//											"There was an error connecting with the\n" +
//											"middleware. Would you like to work offline?",
//											"Transfer History Error") != 0);
						LogManager.error("Failed to register file transfer.", e);
					}
				} 
				
				return tasks;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void finished() {
				updateTaskList((List<FileTransferTask>) get());
			}

		};

		worker.start();
	}

	/**
	 * Make an RPC call to the TG File History servlet and clear the
	 * notifications for this file transfer
	 */
	public static void clearNotifications(final List<Long> ids) {

		if (!ConfigOperation.isLoggingEnabled())
			return;

		SwingWorker worker = new SwingWorker() {
			@Override
			public Object construct() 
			{
				try 
				{
					ClientResource clientResource = ServletUtil.getClient(ServletUtil.NOTIFICATIONS_DELETE_SERVICE_URL);
					BulkDeleteNotificationsResource client = clientResource.wrap(BulkDeleteNotificationsResource.class);
					BulkNotificationRequest bulkNotificationRequest = new BulkNotificationRequest(ids, NotificationType.EMAIL);
					client.removeAll(bulkNotificationRequest);
				} 
				catch (Exception e) 
				{
					if (e.getMessage().indexOf("No notification of type") > -1) {
						AppMain.Message(AppMain.getApplet(), e.getCause()
								.getMessage(), "Notification Error",
								JOptionPane.OK_OPTION);
					} else {
						if (ConfigOperation.isLoggingEnabled()) {
//							AppMain
//								.enableLogging(AppMain.Confirm(
//												AppMain.getFrame(),
//												"There was an error connecting with the\n" +
//												"middleware. Would you like to work offline?",
//												"Transfer History Error") != 0);
							LogManager.error("Failed to clear the user's file transfer history.", e);
						}
					}
				}
				return null;
			}

		};

		worker.start();

	}

	/**
	 * Make an RPC call to the TG File History servlet and clear the
	 * notifications for this file transfer
	 */
	public static void deregister(final List<Long> ids) {

		if (!ConfigOperation.isLoggingEnabled())
			return;
		
		SwingWorker worker = new SwingWorker() {
			@Override
			public Object construct() 
			{	
				try 
				{	
					LogManager.debug("ID's for deletion are: " + Arrays.toString(ids.toArray()));
					
					for(Long transferId: ids) 
					{
						ClientResource clientResource = ServletUtil.getClient(ServletUtil.TRANSFERS_SERVICE_URL + "/" + transferId);
						TransfersResource client = clientResource.wrap(TransfersResource.class);
						client.removeTransfer();
					}
				} 
				catch (Exception e) 
				{
					if (ConfigOperation.isLoggingEnabled()) {
//						AppMain.enableLogging(AppMain.Confirm(
//											AppMain.getFrame(),
//											"There was an error connecting with the\n" +
//											"middleware. Would you like to work offline?",
//											"Transfer History Error") != 0);
						LogManager.error(
									"Failed to deregister selected historical file transfer records",
									e);
					}
				}
		
				return null;
			}
		};
		
		worker.start();
		
	}

	public static void update(FileTransferTask task) {
		update(Arrays.asList(task));
	}

	/**
	 * Make an RPC call to the TG File History servlet and update the
	 * notification status for this file transfer
	 */
	public static void update(final List<FileTransferTask> tasks) {

		if (!ConfigOperation.isLoggingEnabled())
			return;
		
		if (tasks.size() == 0)
			return;
		
		SwingWorker worker = new SwingWorker() {
			@Override
			public Object construct() 
			{
				try
				{	
					List<Transfer> transfers = new ArrayList<Transfer>();

					for (FileTransferTask fileTransferTask : tasks) {
						transfers.add(fileTransferTask.toTransfer());
					}
					
					ClientResource clientResource = ServletUtil.getClient(ServletUtil.TRANSFERS_SERVICE_URL);
					BulkTransfersResource client = clientResource.wrap(BulkTransfersResource.class);
					NotificationType notificationType = null;
					
					BulkTransferRequest bulkTransferRequest = new BulkTransferRequest(transfers, "", notificationType);
					client.update(bulkTransferRequest);
					
					LogManager.debug("Sending transfers to service for logging");
					
					return tasks;
				} 
				catch (Exception e) 
				{
					if (ConfigOperation.isLoggingEnabled()) {
//						AppMain.enableLogging(AppMain.Confirm(
//											AppMain.getFrame(),
//											"There was an error connecting with the\n" +
//											"middleware. Would you like to work offline?",
//											"Transfer History Error") != 0);
						LogManager.error("Failed to update the transfer record.", e);
					}
				}
				
				return null;
			}
		};
		
		worker.start();
	}

	public static void clearHistory() {

		if (!ConfigOperation.isLoggingEnabled())
			return;
		
		SwingWorker worker = new SwingWorker() {
			@Override
			public Object construct() {
				try {
					ClientResource clientResource = ServletUtil.getClient(ServletUtil.TRANSFERS_SERVICE_URL);
					BulkTransfersResource client = clientResource.wrap(BulkTransfersResource.class);
					client.clearAllTransfers();
				} catch (Exception e) {
					if (ConfigOperation.isLoggingEnabled()) {
//						AppMain.enableLogging(AppMain.Confirm(
//							AppMain.getFrame(),
//							"There was an error connecting with the\n" +
//							"middleware. Would you like to work offline?",
//							"Transfer History Error") != 0);
						LogManager.error("Failed to clear the user's historical file transfer record.", e);
					}
				}
				return null;
			}
		};
		
		worker.start();
	}

}
