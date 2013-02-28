package com.innogames.htfs.domain.task;

import org.slf4j.LoggerFactory;

import com.innogames.htfs.domain.task.context.ExecutionContext;

public class DebugTask extends Task {

	public DebugTask(ExecutionContext executionContext) {
		super(executionContext);
	}

	@Override
	public void execute() {
		LoggerFactory.getLogger(this.getClass()).debug(String.format("Processing: %s", this.getHtfsFile().getFile().toString()));
	}

}
