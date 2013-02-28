package com.innogames.htfs.domain.task;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.hadoop.fs.Path;

import com.innogames.htfs.domain.task.context.ExecutionContext;

public class PurgeTask extends Task {

	public PurgeTask(ExecutionContext executionContext) {
		super(executionContext);
	}

	@Override
	public void execute() throws Exception {
		this.purge(this.getHtfsFile().getFile().getParentFile());
	}

	public void purge(File file) {
		List<File> files = (List<File>) FileUtils.listFilesAndDirs(file, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

		if(files.size() <= 1 && !(new Path(this.getConfig().getSystemDirectory()).equals(new Path(file.getAbsolutePath())))) {
			file.delete();
			this.purge(file.getParentFile());
		}
	}

}
