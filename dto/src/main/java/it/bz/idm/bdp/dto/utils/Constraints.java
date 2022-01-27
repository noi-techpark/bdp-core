package it.bz.idm.bdp.dto.utils;

import java.util.regex.Pattern;

public class Constraints {

	// Utility class, no need for a constructor
	private Constraints() {}

	public static boolean isEmpty(String value) {
		if (value == null || value.isEmpty())
			return true;
		return false;
	}

	public static boolean isUUID(String value) {
		if (isEmpty(value))
			return false;
		Pattern p = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
		return p.matcher(value).matches();
	}

	public static boolean someEmpty(String... values) {
		for (String value : values) {
			if (isEmpty(value))
				return true;
		}
		return false;
	}

}
