package com.innogames.htfs;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.innogames.htfs.config.Config;
import com.innogames.htfs.domain.fs.FileSystemMonitor;
import com.innogames.htfs.domain.task.MoveToHdfsTask;
import com.innogames.htfs.domain.task.Task;
import com.innogames.htfs.domain.task.TaskManager;
import com.innogames.htfs.job.Synchronize;

public class Htfs extends Thread {

	private final Config configuration;
	private final Scheduler scheduler;
	private final TaskManager taskManager;
	private final FileSystemMonitor fileSystemMonitor;

	public static final String KEY_MONITOR = "fsmon";

	public Htfs(Config configuration) throws SchedulerException {
		this.configuration = configuration;
		this.scheduler = StdSchedulerFactory.getDefaultScheduler();
		this.taskManager = new TaskManager();

		this.taskManager.addMainTask(MoveToHdfsTask.class);

		this.fileSystemMonitor = new FileSystemMonitor(this.getConfiguration(), this.getTaskManager());
	}

	public boolean addPreTask(Class<? extends Task> task) {
		return this.taskManager.addPreTask(task);
	}

	public boolean addMainTask(Class<? extends Task> task) {
		return this.taskManager.addMainTask(task);
	}

	public boolean addPostTask(Class<? extends Task> task) {
		return this.taskManager.addPostTask(task);
	}

	@Override
	public void run() {
		JobDetail job = newJob(Synchronize.class).build();
		job.getJobDataMap().put(Htfs.KEY_MONITOR, this.getFileSystemMonitor());

		Trigger trigger = newTrigger()
			.startNow()
			.withSchedule(simpleSchedule().withIntervalInSeconds(this.getConfiguration().getSystemJobInterval()).repeatForever())
			.build();

		try {
			this.getScheduler().scheduleJob(job, trigger);
			this.getScheduler().start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public void stopScheduler() throws SchedulerException {
		this.getScheduler().shutdown();
	}

	public Config getConfiguration() {
		return this.configuration;
	}

	public TaskManager getTaskManager() {
		return this.taskManager;
	}

	public Scheduler getScheduler() {
		return this.scheduler;
	}

	public FileSystemMonitor getFileSystemMonitor() {
		return this.fileSystemMonitor;
	}
}
