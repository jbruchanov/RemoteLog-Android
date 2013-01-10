package com.scurab.android.rlw;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.scurab.android.rlw.RootCheck.ExecShell.SHELL_CMD;

/**
 * Simple help class for getting probability of rooted device<br/>
 * Possible values [0-7], higher is higher probablity about rooted device 
 * @author Jiri Bruchanov
 *
 */
class RootCheck {
    
    /**
     * Test 3 methods about rooted device<br/>
     * Bit value for test<br/>
     * Higher value means higer probablity about rooted device
     * @return
     *  <ul>
     *  <li>
     *   1 aka xx1 - BuildTags contains test-keys, very simple test
     *  </li>
     *  <li>
     *   2 aka x1x - /system/app/Superuser.apk app exists
     *  </li>
     *  <li>
     *   4 aka 1xx - try run "/system/xbin/which", "su"
     *  </li>
     *  <ul>
     *  
     */
    public static int getDeviceRoot() {
	int result = 0;
	if (checkRootMethod1()) {
	    result = 1;
	}
	if (checkRootMethod2()) {
	    result += 2;
	}
	if (checkRootMethod3()) {
	    result += 4;
	}
	return result;
    }

    private static boolean checkRootMethod1() {
	String buildTags = android.os.Build.TAGS;

	if (buildTags != null && buildTags.contains("test-keys")) {
	    return true;
	}
	return false;
    }

    private static boolean checkRootMethod2() {
	try {
	    File file = new File("/system/app/Superuser.apk");
	    if (file.exists()) {
		return true;
	    }
	} catch (Exception e) {
	    //ignore error
	}

	return false;
    }

    private static boolean checkRootMethod3() {
	if (new ExecShell().executeCommand(SHELL_CMD.check_su_binary) != null) {
	    return true;
	} else {
	    return false;
	}
    }

    public static class ExecShell {

	public static enum SHELL_CMD {
	    check_su_binary(new String[] { "/system/xbin/which", "su" }), ;

	    String[] command;

	    SHELL_CMD(String[] command) {
		this.command = command;
	    }
	}

	public ArrayList<String> executeCommand(SHELL_CMD shellCmd) {
	    String line = null;
	    ArrayList<String> fullResponse = new ArrayList<String>();
	    Process localProcess = null;

	    try {
		localProcess = Runtime.getRuntime().exec(shellCmd.command);
	    } catch (Exception e) {
		return null;
		// e.printStackTrace();
	    }

	    BufferedReader in = new BufferedReader(new InputStreamReader(
		    localProcess.getInputStream()));

	    try {
		while ((line = in.readLine()) != null) {
		    fullResponse.add(line);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    return fullResponse;
	}
    }
}
