package com.innogames.htfs.domain.task;

import java.io.IOException;

import org.apache.hadoop.fs.Path;

import com.innogames.htfs.domain.task.context.ExecutionContext;

public class MoveToHdfsTask extends Task {

	public enum Reason {
		TOO_OLD,
		SIZE_REACHED
	};

	public MoveToHdfsTask(ExecutionContext executionContext) throws IOException {
		super(executionContext);
	}

	@Override
	public void execute() throws IOException {
		Path src = new Path(this.getHtfsFile().getFile().getAbsolutePath());
		Path dst = new Path(String.format("%s/%s", this.getConfig().getHdfsDirectory(), this.getHtfsFile().getRelativePath(this.getConfig().getSystemDirectory())));

		this.getHdfs().moveFromLocalFile(src, dst);
	}

}
