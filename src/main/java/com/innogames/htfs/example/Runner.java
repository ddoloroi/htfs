package com.innogames.htfs.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.innogames.htfs.Htfs;
import com.innogames.htfs.config.SimpleConfig;
import com.innogames.htfs.domain.servlet.GraphServlet;
import com.innogames.htfs.domain.task.DebugTask;
import com.innogames.htfs.domain.task.FileSequenceTask;
import com.innogames.htfs.domain.task.PurgeTask;

public class Runner {

	public static void main(String[] args) throws Exception {
		Htfs htfs = new Htfs(new SimpleConfig(
			"/tmp/sync", // HDFS directory
			"hdfs://172.16.23.3:8020", // Namenode URI
			64*1024*1024, // HDFS block size
			5*1024*1024, // HDFS block size buffer
			"/tmp/sync", // Local directory
			1, // System job interval
			60, // Write timeout for completed files
			1800 // Write timeout for incompleted files
		));

		htfs.addPreTask(FileSequenceTask.class);
		htfs.addPreTask(DebugTask.class);

		htfs.addPostTask(PurgeTask.class);
		htfs.addPostTask(DebugTask.class);

		htfs.run();

		Server server = new Server(8080);
		ServletContextHandler context = new ServletContextHandler();
		context.addServlet(new ServletHolder(new GraphServlet(htfs.getFileSystemMonitor())), "/");
		server.setHandler(context);
		server.start();

        htfs.join();
        server.join();
	}

}
