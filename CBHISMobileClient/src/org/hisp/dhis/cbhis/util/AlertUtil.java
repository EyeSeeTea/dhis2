package org.hisp.dhis.cbhis.util;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;

public class AlertUtil {
	
	/**
	 * @author Ngo Thanh Long
	 * 
	 */
	
	public static Alert getErrorAlert(String msg){
		Alert alert = new Alert("Error");
		alert.setString(msg);
		alert.setType(AlertType.ERROR);
		alert.setTimeout(Alert.FOREVER);
		return alert;
	}
	
	public static Alert getInfoAlert(String msg){
		Alert alert = new Alert("Error");
		alert.setString(msg);
		alert.setType(AlertType.INFO);
		alert.setTimeout(Alert.FOREVER);
		return alert;
	}
}
