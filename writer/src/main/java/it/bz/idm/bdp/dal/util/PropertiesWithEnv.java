package it.bz.idm.bdp.dal.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.commons.text.StringSubstitutor;

public class PropertiesWithEnv extends Properties {

	private Map<String, String> localEnv = new HashMap<>();

	@Override
	public synchronized void load(InputStream inStream) throws IOException {
		super.load(inStream);
		substitueEnv();
	}

	public void substitueEnv() {
		Map<String, String> environment = new HashMap<>(localEnv);
		environment.putAll(System.getenv());
		StringSubstitutor sub = new StringSubstitutor(environment);
		sub.setEnableUndefinedVariableException(true);
		sub.setValueDelimiter(":");
		for (Entry<Object, Object> entry : super.entrySet()) {
			entry.setValue(sub.replace(entry.getValue()));
		}
	}

	public Map<String, String> getStringMap(final String prefix) {
		Map<String, String> result = new HashMap<>();
		for (final String name : super.stringPropertyNames()) {
			if (prefix == null || prefix.isEmpty() || name.startsWith(prefix)) {
				result.put(name, super.getProperty(name));
			}
		}
		return result;
	}

	public void addEnv(final String key, final String value) {
		localEnv.put(key, value);
	}
}
