package it.bz.idm.bdp.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

	private Utils() {}

	public static Map<String, Object> mapOf(Object... params) {
		Map<String, Object> result = new HashMap<>();
		for (int i = 0; i < params.length; i += 2) {
			if (params[i+1] != null) {
				result.put(params[i].toString(), params[i+1]);
			}
		}
		return result;
	}

	public static List<Object> listOf(Object... params) {
		return Arrays.asList(params);
	}
}
