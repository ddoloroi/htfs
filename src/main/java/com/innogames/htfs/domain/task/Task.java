package com.innogames.htfs.domain.task;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import com.innogames.htfs.config.Config;
import com.innogames.htfs.domain.fs.HtfsFile;
import com.innogames.htfs.domain.task.MoveToHdfsTask.Reason;
import com.innogames.htfs.domain.task.context.ExecutionContext;

public abstract class Task {

	private final ExecutionContext executionContext;

	public Task(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	protected ExecutionContext getExecutionContext() {
		return this.executionContext;
	}

	abstract public void execute() throws Exception;

	protected FileSystem getHdfs() throws IOException {
		final Configuration configuration = new Configuration();
		configuration.set("fs.default.name", this.getConfig().getHdfsConnection());
		configuration.set("fs.file.impl", "org.apache.hadoop.fs.LocalFileSystem");
		return FileSystem.get(configuration);
	}

	protected Config getConfig() {
		return this.getExecutionContext().<Config>getParameter(ExecutionContext.KEY_CONFIG);
	}

	protected HtfsFile getHtfsFile() {
		return this.getExecutionContext().<HtfsFile>getParameter(ExecutionContext.KEY_FILE);
	}

	protected Reason getReason() {
		return this.getExecutionContext().<Reason>getParameter(ExecutionContext.KEY_REASON);
	}

}
