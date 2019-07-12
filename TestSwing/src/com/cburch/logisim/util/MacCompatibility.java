/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.util;

import java.io.File;
import java.io.IOException;

import javax.swing.JMenuBar;



public class MacCompatibility {
	private MacCompatibility() { }


	
	static {
		double versionValue;


	}
	
	public static boolean isAboutAutomaticallyPresent() {

			return false;

	}
	
	public static boolean isPreferencesAutomaticallyPresent() {

			return false;

	}
	
	public static boolean isQuitAutomaticallyPresent() {

			return false;

	}
	
	public static boolean isSwingUsingScreenMenuBar() {

			return false;

	}
	
	public static void setFramelessJMenuBar(JMenuBar menubar) {

	}
	
	public static void setFileCreatorAndType(File dest, String app, String type)
			throws IOException {

	}

}
