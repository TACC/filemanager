/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package org.teragrid.portal.filebrowser.applet.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.teragrid.portal.filebrowser.applet.transfer.FTPSettings;
import org.teragrid.portal.filebrowser.applet.transfer.FileTransferTask;
import org.teragrid.portal.filebrowser.applet.util.ResourceName;
import org.teragrid.portal.filebrowser.applet.util.SGGCResourceBundle;

import edu.utexas.tacc.wcs.filemanager.common.model.Task;


class MyPoint {
    public int x, y;
    public MyPoint(int a, int b) {
        x = a;
        y = b;
    }
}

@SuppressWarnings({"unchecked"})
public class DrawState {
    public static int ARROW_LEN = 6;
    public static int STEP_LEN = 16;

    private MapInfo siteOne, siteTwo;
    private FTPSettings settingOne, settingTwo;

    private FileTransferTask task;
    private String siteArea;
    private int state;
    private boolean change = false;
    private int arrowLen;
    private int stepLen;
    private int offset;
    private double degree;
    private double matrix[][] = new double[2][2];

    public DrawState(MapInfo one, MapInfo two, int aLen, int sLen) {
    	this.siteOne = one;
    	this.siteTwo = two;
        this.offset = 0;
        this.degree = 0;
        initPara();
        this.arrowLen = aLen;
        this.stepLen = sLen;
    }

    public DrawState(FileTransferTask one, int aLen, int sLen) {
    	this.task = one;
        this.arrowLen = aLen;
        this.stepLen = sLen;

        this.settingOne = this.task.getSrcSite();
        this.settingTwo = this.task.getDestSite();
        if (mapMatcher()) {
        	this.offset = 0;
            this.degree = 0;
            initPara();
        } else {
        	this.offset = -1;
        	this.siteArea = "None";
        }
    }

    private boolean mapMatcher() {
        java.util.List mapInfoList = new ArrayList();
//        mapInfoList = ConfigOperation.getInstance().getMapInfo();
        boolean flag = false;

        Iterator it = mapInfoList.iterator();
        while(it.hasNext()) {
            MapInfo mapInfo = (MapInfo)it.next();
            if (mapInfo.getSite() == this.settingOne) {
            	this.siteOne = mapInfo;
                flag = true;
                break;
            }
        }
        if (!flag) {
            //System.err.println("map info does not exist for ftpsetting one.");
            return flag;
        }

        flag = false;
        it = mapInfoList.iterator();
        while(it.hasNext()) {
            MapInfo mapInfo = (MapInfo)it.next();
            if (mapInfo.getSite() == this.settingTwo) {
            	this.siteTwo = mapInfo;
                flag = true;
                break;
            }
        }
        if (!flag) {
            //System.err.println("map info does not exist for ftpsetting two.");
            return flag;
        }

        this.siteArea = this.siteOne.getArea();
        if (!this.siteArea.equalsIgnoreCase(this.siteTwo.getArea())) {
            flag = false;
            //System.err.println("map info is not in the same map.");
        }
        return flag;
    }

    /**
     * Calculate the degree and generate the matrix
     */
    private void initPara() {
        double divided, division;
        divided = this.siteTwo.getY() - this.siteOne.getY();
        division = this.siteTwo.getX() - this.siteOne.getX();

        if (division != 0) {
        	this.degree = Math.atan(divided/division);
            if (this.degree < 0 && this.siteOne.getY() < this.siteTwo.getY()) {
            	this.degree += Math.PI;
            } else if (this.degree < 0 && this.siteOne.getY() > this.siteTwo.getY()) {
            	this.degree += 0;
            } else if (this.degree > 0 && this.siteOne.getY() < this.siteTwo.getY()) {
            	this.degree += 0;
            } else if (this.degree > 0 && this.siteOne.getY() > this.siteTwo.getY()) {
            	this.degree += Math.PI;
            } else if (this.degree == 0 && this.siteOne.getY() > this.siteTwo.getY()) {
            	this.degree = Math.PI;
            }
        } else {
            if (this.siteOne.getY() < this.siteTwo.getY()) {
            	this.degree = Math.PI/2;
            } else {
            	this.degree = 3*Math.PI/2;
            }
        }

        this.degree = -this.degree;
        this.matrix[0][0] = Math.cos(this.degree);
        this.matrix[0][1] = -Math.sin(this.degree);
        this.matrix[1][0] = Math.sin(this.degree);
        this.matrix[1][1] = Math.cos(this.degree);
    }

    public boolean judgeObject() {
        return (this.offset != -1);
    }

    public void setState() {
        if (this.offset == -1) {
        	this.state = -1;
        } else {
        	this.state = this.task.getStatus();
        }
    }

    public int containsFTP(FTPSettings oneFtp) {
        if (this.settingOne.equals(oneFtp)) {
        	return 1;
        } else if (this.settingTwo.equals(oneFtp)) {
        	return 2;
        } else {
        	return 0;
        }
    }

    public void siteModified(MapInfo map, int num) {
        if (num != 1 && num != 2) {
        	return;
        }
        else if (num == 1) {
        	this.siteOne = map;
            java.util.List mapInfoList = new ArrayList();
//            mapInfoList = ConfigOperation.getInstance().getMapInfo();
            Iterator it = mapInfoList.iterator();
            while(it.hasNext()) {
                MapInfo mapInfo = (MapInfo)it.next();
                if (mapInfo.getSite() == this.settingTwo) {
                	this.siteTwo = mapInfo;
                    this.siteArea = this.siteOne.getArea();
                    if (this.siteArea.equalsIgnoreCase(this.siteTwo.getArea())) {
                    	this.offset = 0;
                        this.degree = 0;
                        initPara();
                    }
                    break;
                }
            }
        } else {
        	this.siteTwo = map;
            if (this.siteOne != null) {
            	this.siteArea = this.siteOne.getArea();
                if (this.siteArea.equalsIgnoreCase(this.siteTwo.getArea())) {
                    offset = 0;
                    this.degree = 0;
                    initPara();
                }
            }
        }
    }

    public void drawFig(Graphics g) {
        double all, len;
        int pointer;
        double one = this.siteTwo.getX() - this.siteOne.getX();
        double two = this.siteTwo.getY() - this.siteOne.getY();
        all = one*one + two*two;
        len = Math.sqrt(all);

        pointer = offset;
        for (int limit = (int)len; pointer < limit  - this.arrowLen;) {
                drawUp(new MyPoint(limit-pointer,0),
                       new MyPoint(limit-pointer-this.arrowLen,this.arrowLen),
                       new MyPoint(limit-pointer-this.arrowLen,-this.arrowLen),g);
            pointer += this.stepLen;
        }

        this.change = !this.change;
        if(this.change) {
        	offset = this.stepLen/2;
        } else {
        	offset = 0;
        }
    }

    private void drawUp(MyPoint center, MyPoint up, MyPoint down, Graphics g) {
        java.util.List temp = new ArrayList();
        temp.add(center);
        temp.add(up);
        temp.add(down);
        double calX, calY;

        Iterator iter = temp.iterator();
        while (iter.hasNext()) {
            MyPoint target = (MyPoint) iter.next();
            calX = target.x * this.matrix[0][0] + target.y * this.matrix[1][0]+ this.siteOne.getX();
            calY = target.x * this.matrix[0][1] + target.y * this.matrix[1][1]+ this.siteOne.getY();

            target.x = (int) calX;
            target.y = (int) calY;
        }

        g.drawLine(center.x+5, center.y+5, up.x+5, up.y+5);
        g.drawLine(center.x+5, center.y+5, down.x+5, down.y+5);
    }

    public void paint(Graphics g, String sArea) {
        setState();
        if (!this.siteArea.equalsIgnoreCase(sArea)) {
        	this.state = -1;
        }
        
        ((Graphics2D)g).setStroke(new BasicStroke(3));
        g.setFont(new Font("Arial",Font.PLAIN,11));
        switch (this.state) {
        case -1: {
            //g.setColor(Color.gray);
            break;
        }
        case Task.WAITING: {
            g.setColor(Color.gray);
            g.drawLine(this.siteOne.getX()+5, this.siteOne.getY()+5, this.siteTwo.getX()+5, this.siteTwo.getY()+5);
            g.drawString(SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DRAWSTATE_WAITING),
            		(this.siteOne.getX() + this.siteTwo.getX())/2+10, (this.siteOne.getY() + this.siteTwo.getY())/2+10);
            break;
        }
        case Task.ONGOING: {
            g.setColor(Color.red);
            drawFig(g);
            g.setColor(Color.black);
            String info;
            info = MessageFormat.format(
            		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DRAWSTATE_TRANSFERINFO), 
            		new Object[] {this.task.getSpeedString()});
            g.drawString(info, (this.siteOne.getX() + this.siteTwo.getX())/2+10, (this.siteOne.getY() + this.siteTwo.getY())/2+10);
            
            info = MessageFormat.format(
            		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DRAWSTATE_TIMEUSEDINFO), 
            		new Object[] {this.task.getTotalTimeString()});
            g.drawString(info, (this.siteOne.getX() + this.siteTwo.getX())/2+10, (this.siteOne.getY() + this.siteTwo.getY())/2+23);
            
            info = MessageFormat.format(
            		SGGCResourceBundle.getResourceString(ResourceName.KEY_DISPLAY_DRAWSTATE_TIMELEFTINFO), 
            		new Object[] {this.task.getLeftTimeString()});
            g.drawString(info, (this.siteOne.getX() + this.siteTwo.getX())/2+10, (this.siteOne.getY() + this.siteTwo.getY())/2+38);
            break;
        }
        case Task.DONE: {
            g.setColor(Color.blue);
            break;
        }
        }
    }
}
