package com.innogames.htfs.domain.task.context;

import java.util.Map;

public class ExecutionParameter implements Map.Entry<String, Object> {
	private final String key;
	private Object value;

	public ExecutionParameter(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	public Object setValue(Object value) {
		Object old = this.value;
		this.value = value;
		return old;
	}
}
