/**
* Copyright 2015 Microsoft Open Technologies, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*	 http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package com.interopbridges.tools.windowsazure;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JSONHelper {

	/**
	 * This API will create a JSON Object
	 * 
	 * @param name
	 * @param backup
	 * @param defaultTTL
	 * @param expType
	 * @return
	 * @throws WindowsAzureInvalidProjectOperationException
	 */
	public static String createObject()
			throws WindowsAzureInvalidProjectOperationException {
		JSONObject cacheObj = new JSONObject();
		try {
			cacheObj.put("caches", new JSONArray());
			return encodeHTML(cacheObj.toString());
		} catch (JSONException ex) {
			throw new WindowsAzureInvalidProjectOperationException(
					WindowsAzureConstants.EXCP, ex);
		}

	}

	public static String addCache(String encCompCache, String name, int backup,
			int defaultTTL, int expType)
			throws WindowsAzureInvalidProjectOperationException {
		String cache = "";
		boolean isExpirable = true;
		try {
			String decodedCache = decodeHTML(encCompCache);

			JSONObject oriCacheObj = new JSONObject(decodedCache);
			JSONArray cachesArr = (JSONArray) oriCacheObj.get("caches");
			JSONObject newCacheObj = new JSONObject();
			if (expType == 0) {
				isExpirable = false;
			}
			newCacheObj.put("name", name);
			newCacheObj.put("secondaries", Integer.valueOf(backup));

			JSONObject isEnbaled = new JSONObject();
			isEnbaled.put("isEnabled", false);

			JSONObject exp = new JSONObject();
			exp.put("defaultTTL", Integer.valueOf(defaultTTL));

			exp.put("isExpirable", Boolean.valueOf(isExpirable));
			exp.put("type", Integer.valueOf(expType));

			JSONObject eviction = new JSONObject();
			eviction.put("type", Integer.valueOf(0));

			JSONObject policy = new JSONObject();
			policy.put("serverNotification", isEnbaled);
			policy.put("eviction", eviction);
			policy.put("expiration", exp);
			newCacheObj.put("policy", policy);
			cachesArr.put(newCacheObj);
			oriCacheObj.put("caches", cachesArr);
			cache = encodeHTML(oriCacheObj.toString());
			return cache;
		} catch (Exception ex) {
			throw new WindowsAzureInvalidProjectOperationException(
					WindowsAzureConstants.EXCP, ex);
		}
	}

	protected static Map<String, String> getCaches(String encodedCacheStr)
			throws WindowsAzureInvalidProjectOperationException {
		Map<String, String> cacheMap = new HashMap<String, String>();
		String decodedCache = decodeHTML(encodedCacheStr);
		JSONObject oriCacheObj;
		try {
			oriCacheObj = new JSONObject(decodedCache);
			JSONArray cachesArr = (JSONArray) oriCacheObj.get("caches");
			for (int i = 0; i < cachesArr.length(); i++) {
				JSONObject cacheObj = (JSONObject) cachesArr.get(i);
				String name = getParamVal(cacheObj.toString(), "name");
				cacheMap.put(name, encodeHTML(cacheObj.toString()));
			}
			return cacheMap;
		} catch (JSONException ex) {
			throw new WindowsAzureInvalidProjectOperationException(
					WindowsAzureConstants.EXCP, ex);
		}

	}

	protected static String setCache(String encodedCaches, String name,
			String newCache)
			throws WindowsAzureInvalidProjectOperationException {
		try {
			Map<String, String> cacheMap = getCaches(encodedCaches);
			if (newCache.isEmpty()) {
				// remove the cache from arraylist and map
				cacheMap.remove(name);
			} else {
				cacheMap.put(name, newCache);
			}

			String decodedCache = decodeHTML(encodedCaches);
			JSONObject oriCacheObj = new JSONObject(decodedCache);
			JSONArray cachesArr = new JSONArray();
			Set<Entry<String, String>> set = cacheMap.entrySet();
			for (Entry<String, String> entry : set) {
				cachesArr.put(new JSONObject(decodeHTML(entry.getValue())));
			}

			oriCacheObj.put("caches", cachesArr);
			return oriCacheObj.toString();

		} catch (Exception ex) {
			throw new WindowsAzureInvalidProjectOperationException(
					WindowsAzureConstants.EXCP, ex);
		}
	}

	/**
	 * This API will encode the given string in HTML.
	 * 
	 * @param str
	 * @return
	 */
	public static String encodeHTML(String str) {
		String encoded = StringEscapeUtils.escapeHtml3(str);
		return encoded;
	}

	/**
	 * This API will decode the given HTML encoded string.
	 * 
	 * @param str
	 * @return
	 */
	public static String decodeHTML(String str) {
		String decoded = StringEscapeUtils.unescapeHtml3(str);
		return decoded;
	}

	/**
	 * Sets the given parameter with given value.
	 * 
	 * @param cache
	 * @param param
	 * @param val
	 * @return
	 * @throws WindowsAzureInvalidProjectOperationException
	 */
	public static String setParamValue(String cache, String param, String val)
			throws WindowsAzureInvalidProjectOperationException {
		try {
			String decodedCache = decodeHTML(cache);
			JSONObject cacheObj = new JSONObject(decodedCache);
			if (!cacheObj.has(param) && param.contains(".")) {
				String[] params = param.split("\\.");
				JSONObject newobj = cacheObj;
				for (int i = 0; i < params.length; i++) {
					if (params.length - i == 1) {
						if (StringUtils.isNumeric(val)) {
							if (params[i].equalsIgnoreCase("isExpirable")) {
								newobj.put(params[i], Boolean.valueOf(val));
							} else {
								newobj.put(params[i], Integer.valueOf(val));
							}
						} else {
							newobj.put(params[i], val);
						}
						return encodeHTML(cacheObj.toString());
					}
					newobj = (JSONObject) newobj.get(params[i]);
				}
			} else {
				if (StringUtils.isNumeric(val)) {
					cacheObj.put(param, Integer.valueOf(val));
				} else {
					cacheObj.put(param, val);
				}
			}
			return encodeHTML(cacheObj.toString());
		} catch (Exception ex) {
			throw new WindowsAzureInvalidProjectOperationException(
					WindowsAzureConstants.EXCP, ex);
		}
	}

	/**
	 * Returns the value of given parameter from encoded string.
	 * 
	 * @param cache
	 * @param param
	 * @return
	 * @throws WindowsAzureInvalidProjectOperationException
	 */
	public static String getParamVal(String cache, String param)
			throws WindowsAzureInvalidProjectOperationException {
		try {
			JSONObject cacheObj = new JSONObject(decodeHTML(cache));
			if (!cacheObj.has(param) && param.contains(".")) {
				String[] params = param.split("\\.");
				for (int i = 0; i < params.length; i++) {
					if (params.length - i == 1) {
						return cacheObj.get(params[i]).toString();
					}
					cacheObj = (JSONObject) cacheObj.get(params[i]);
				}
			}
			return cacheObj.get(param).toString();
		} catch (Exception ex) {
			throw new WindowsAzureInvalidProjectOperationException(
					WindowsAzureConstants.EXCP, ex);
		}
	}
}
