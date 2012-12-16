package com.scurab.android.rlw;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.scurab.gwt.rlw.shared.model.LogItem;
import com.scurab.gwt.rlw.shared.model.LogItemBlobRequest;
import com.scurab.gwt.rlw.shared.model.LogItemRespond;
import com.scurab.java.rlw.ServiceConnector;

public class LogSender {

    private final BlockingQueue<LogItem> mItems = new ArrayBlockingQueue<LogItem>(128);
    private final HashMap<LogItem, LogItemBlobRequest> mCoData = new HashMap<LogItem, LogItemBlobRequest>();
    
    private Thread mWorkingThread;
    private boolean mIsRunning = true;
    private boolean mPause = false;
    
    private ServiceConnector mConnector;
    
    public LogSender(){
	mConnector = RemoteLog.getConnector();
	createWorkingThread();
    }
    
    private void createWorkingThread(){
	mWorkingThread = new Thread(new Runnable() {
	    @Override
	    public void run() {
		while(mIsRunning){
		    checkPause();
		    LogItem li = null;
		    try {
			li = mItems.take();
			LogItemRespond lir = mConnector.saveLogItem(li);
			//check if there is a blob for write
			LogItemBlobRequest blob = mCoData.get(li);
			if(blob != null){
			    //set logid for blob item
			    blob.setLogItemID(lir.getContext().getID());
			    byte[] data = blob.getData();
			    //save data
			    mConnector.saveLogItemBlob(blob, data);
			}
		    } catch (InterruptedException e) {
			e.printStackTrace();
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		    //dont forget to remove co-data
		    mCoData.remove(li);
		}
		mWorkingThread = null;
	    }
	},"LogSender");
	mWorkingThread.start();
    }
    
    private void checkPause() {
	if (mPause) {
	    synchronized (mWorkingThread) {
		try {
		    mWorkingThread.wait();
		} catch (InterruptedException e) {

		}
	    }
	}
    }
    
    public boolean addLogItem(LogItem item){
	return addLogItem(item, null);
    }
    
    public boolean addLogItem(LogItem item, LogItemBlobRequest data){
	try{
	    if(data != null){
		mCoData.put(item, data);
	    }
	    mItems.add(item);
	    return true;
	}catch(Exception e){
	    e.printStackTrace();
	    return false;
	}
    }
    
    public void pause(){
	mPause = true;
    }
    
    public void resume(){
	mPause = false;
	synchronized (mWorkingThread) {
	    mWorkingThread.notify();
	}
    }
    
//    public void stop(){
//	mPause = true;
//    }
    
    public void restart(){
	if(mWorkingThread != null){
	    throw new IllegalStateException("Another working thread is running!");
	}
	createWorkingThread();
    }
    
    public void waitForEmptyQueue(){
	while(mItems.size() > 0 || mCoData.size() > 0){
	    try {
		Thread.sleep(50);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }
    
//    public void start(){	
//	if(mWorkingThread)
//	mIsRunning = true;
//	if(mPause){
//	    mPause = false;
//	    synchronized (mWorkingThread) {
//		mWorkingThread.notify();
//	    }
//	}
//    }
}
