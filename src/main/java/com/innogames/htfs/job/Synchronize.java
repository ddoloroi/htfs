package com.innogames.htfs.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.innogames.htfs.Htfs;
import com.innogames.htfs.domain.fs.FileSystemMonitor;
import com.innogames.htfs.domain.task.Task;

@DisallowConcurrentExecution
public class Synchronize implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			for(Task task : ((FileSystemMonitor) context.getJobDetail().getJobDataMap().get(Htfs.KEY_MONITOR)).getTasks()) {
				task.execute();
			}
		} catch(Exception e) {
			throw new JobExecutionException(e);
		}
	}

}
