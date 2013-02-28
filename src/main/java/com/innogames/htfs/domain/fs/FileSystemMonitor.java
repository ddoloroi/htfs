package com.innogames.htfs.domain.fs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.innogames.htfs.config.Config;
import com.innogames.htfs.domain.task.MoveToHdfsTask;
import com.innogames.htfs.domain.task.MoveToHdfsTask.Reason;
import com.innogames.htfs.domain.task.Task;
import com.innogames.htfs.domain.task.TaskManager;
import com.innogames.htfs.domain.task.context.ExecutionContext;
import com.innogames.htfs.domain.task.context.ExecutionParameter;

public class FileSystemMonitor {

	private final Config config;
	private final TaskManager taskManager;

	public FileSystemMonitor(Config config, TaskManager taskManager) {
		this.config = config;
		this.taskManager = taskManager;
	}

	public List<Task> getTasks() {
		List<Task> tasks = new ArrayList<Task>();

		for(final HtfsFile htfsFile : this.getHtfsFiles()) {
			if(htfsFile.getIncompleteWriteFactor() >= 1f) {
				tasks.addAll(this.getTasks(this.getContext(this.getConfig(), htfsFile, MoveToHdfsTask.Reason.TOO_OLD)));
			}

			if(htfsFile.getCompleteWriteFactor() >= 1f) {
				tasks.addAll(this.getTasks(this.getContext(this.getConfig(), htfsFile, MoveToHdfsTask.Reason.SIZE_REACHED)));
			}
		}

		return tasks;
	}

	public List<HtfsFile> getHtfsFiles() {
		List<HtfsFile> htfsFiles = new ArrayList<HtfsFile>();

		File dir = new File(this.getConfig().getSystemDirectory());
		List<File> files = (List<File>) FileUtils.listFilesAndDirs(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

		for(final File file : files) {
			if(file.isFile()) {
				htfsFiles.add(new HtfsFile(
					file,
					this.getConfig().getFileIncompleteWriteTimeout(),
					this.getConfig().getFileCompleteWriteTimeout(),
					this.getConfig().getHdfsBlocksize(),
					this.getConfig().getHdfsBlocksizeBuffer())
				);
			}
		}

		return htfsFiles;
	}

	private List<Task> getTasks(ExecutionContext context) {
		List<Task> tasks = new ArrayList<Task>();

		for(Class<? extends Task> task : this.getTaskManager().getTasks()) {
			try {
				tasks.add(task.getConstructor(ExecutionContext.class).newInstance(context));
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}

		return tasks;
	}

	private ExecutionContext getContext(Config config, HtfsFile htfsFile, Reason reason) {
		return new ExecutionContext(
			new ExecutionParameter(ExecutionContext.KEY_CONFIG, config),
			new ExecutionParameter(ExecutionContext.KEY_FILE, htfsFile),
			new ExecutionParameter(ExecutionContext.KEY_REASON, reason)
		);
	}

	private Config getConfig() {
		return this.config;
	}

	private TaskManager getTaskManager() {
		return this.taskManager;
	}

}
