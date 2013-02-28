package com.innogames.htfs.domain.task.context;

import java.util.HashMap;

public class ExecutionContext extends HashMap<String, Object> {

	public static final String KEY_CONFIG = "config";
	public static final String KEY_FILE = "file";
	public static final String KEY_REASON = "reason";

	private static final long serialVersionUID = -8207107338803198841L;

	public ExecutionContext(ExecutionParameter... params) {
		for(ExecutionParameter param : params) {
			this.put(param.getKey(), param.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getParameter(String key) {
		return (T) this.get(key);
	}

}
