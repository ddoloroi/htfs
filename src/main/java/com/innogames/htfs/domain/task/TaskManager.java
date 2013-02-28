package com.innogames.htfs.domain.task;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

	private final List<Class<? extends Task>> preTasks;
	private final List<Class<? extends Task>> mainTasks;
	private final List<Class<? extends Task>> postTasks;

	public TaskManager() {
		this.preTasks = new ArrayList<Class<? extends Task>>();
		this.mainTasks = new ArrayList<Class<? extends Task>>();
		this.postTasks = new ArrayList<Class<? extends Task>>();
	}

	public boolean addPreTask(Class<? extends Task> task) {
		return this.preTasks.add(task);
	}

	public boolean addMainTask(Class<? extends Task> task) {
		return this.mainTasks.add(task);
	}

	public boolean addPostTask(Class<? extends Task> task) {
		return this.postTasks.add(task);
	}

	public List<Class<? extends Task>> getPreTasks() {
		return this.preTasks;
	}

	public List<Class<? extends Task>> getMainTasks() {
		return this.mainTasks;
	}

	public List<Class<? extends Task>> getPostTasks() {
		return this.postTasks;
	}

	public List<Class<? extends Task>> getTasks() {
		List<Class<? extends Task>> tasks = new ArrayList<Class<? extends Task>>();

		tasks.addAll(this.getPreTasks());
		tasks.addAll(this.getMainTasks());
		tasks.addAll(this.getPostTasks());

		return tasks;
	}

}
