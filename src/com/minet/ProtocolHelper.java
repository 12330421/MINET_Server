package com.minet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class ProtocolHelper {
	private String inStr;
	private Map responseData;

	public ProtocolHelper() {

	}

	public void setInStr(String inStr) {
		this.inStr = inStr;
	}

	public String getPara(String str) {
		int start = inStr.indexOf(str) + str.length() + 1;
		int end = inStr.indexOf("|", start);
		return inStr.substring(start, end);
	}

	public Map getData() {
		int start = inStr.indexOf("Data") + 4 + 1;
		return Json2Map(inStr.substring(start));
	}

	public void setResponseData(Map data) {
		this.responseData = data;
	}

	public String generateOutStr() {
		String outStr = "From:Server|";
		outStr += "Method:Response|";
		outStr += "Action:" + this.getPara("Action") + "|";
		outStr += "Status:" + "OK|";
		outStr += "Data:" + (new JSONObject(this.responseData));
		return outStr;
	}
    
    public static String generateOutStr(String action, Map Data) {
		String outStr = "From:Server|";
		outStr += "Method:Response|";
		outStr += "Action:" + action+ "|";
		outStr += "Status:" + "OK|";
		outStr += "Data:" + (new JSONObject(Data));
		return outStr;
    }

	public static Map Json2Map(String jsonString) throws JSONException {

		JSONObject jsonObject = new JSONObject(jsonString);

		Map result = new HashMap();
		Iterator iterator = jsonObject.keys();
		String key = null;
		String value = null;

		while (iterator.hasNext()) {

			key = (String) iterator.next();
			value = jsonObject.getString(key);
			result.put(key, value);

		}
		return result;

	}
}