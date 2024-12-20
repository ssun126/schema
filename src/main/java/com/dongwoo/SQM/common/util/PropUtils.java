package com.dongwoo.SQM.common.util;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

public class PropUtils {

	private static PropUtils propUtils = null;

	private Properties prop = null;

	public static PropUtils getInstance() {
		if (null == propUtils) {
			propUtils =  new PropUtils();
			propUtils.setProp();
		}
		return propUtils;
	}

	public static String get(String key) {
		return PropUtils.getInstance().getProp().getProperty(key);
	}

	private Properties getProp() {
		return prop;
	}

	private void setProp() {
		prop = new Properties();
		try {
			File dir = new File(getClass().getResource(Const.PROPERTIES_PATH).getFile());
			File[] fileList = dir.listFiles();
			for (int i = 0 ; i < fileList.length ; i++) {
				if (fileList[i].isFile() && fileList[i].getName().indexOf(".properties") > 0) {
					InputStream is = getClass().getResourceAsStream(Const.PROPERTIES_PATH + fileList[i].getName());
					prop.load(is);
					is.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
