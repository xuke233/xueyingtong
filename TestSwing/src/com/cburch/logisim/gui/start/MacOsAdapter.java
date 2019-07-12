/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.start;



class MacOsAdapter { //MAC extends ApplicationAdapter {
	

	

	
	/* MAC
	public void handleOpenFile(com.apple.eawt.ApplicationEvent event) {
		Startup.doOpen(new File(event.getFilename()));
	}
	
	public void handlePrintFile(com.apple.eawt.ApplicationEvent event) {
		Startup.doPrint(new File(event.getFilename()));
	}
	
	public void handlePreferences(com.apple.eawt.ApplicationEvent event) {
		PreferencesFrame.showPreferences();
	}
	*/
	
	public static void register() {
		//MAC Application.getApplication().addApplicationListener(new MacOsAdapter());
	}
}