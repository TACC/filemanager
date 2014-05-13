/*
 * Portions of this file Copyright 2004-2007 Shanghai Jiaotong University
 * 
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/legal/4.0/
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */

package edu.utexas.tacc.wcs.filemanager.client.ui;

import edu.utexas.tacc.wcs.filemanager.client.transfer.FTPSettings;

/**
 * <p>Title: CGFtp</p>
 *
 * <p>Description: Con GridFtp</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: SJTU</p>
 *
 * @author yanghuan
 * @version 1.0
 */
public class MapInfo {
    public static String[] AREA_LISTS = new String[]{"Custom","Shanghai","China","Global"};

    private FTPSettings site = null;
    private int x = 0;
    private int y = 0;
    private String area = null;

    public MapInfo() {
    }

    public MapInfo(FTPSettings site, String area, int x, int y){
        this.site = site;
        this.x = x;
        this.y = y;
        this.area = area;
    }

    public FTPSettings getSite(){
        return this.site;
    }

    public void setSite(FTPSettings site){
        this.site = site;
    }

    public int getX(){
        return this.x;
    }

    public void setX(int x){
        this.x = x;
    }

    public int getY(){
        return this.y;
    }

    public void setY(int y){
        this.y = y;
    }

    public String getArea(){
        return this.area;
    }

    public void setArea(String area){
        this.area = area;
    }
}
