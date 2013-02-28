HTFS - Hadoop Transferable File System
======================================

The HTFS library provides a simple way to synchronize a local directory with a directory inside a HDFS. It can be integrated within a project or used as a stand alone executable JAR file. The HTFS will run within it's own thread and uses Quartz to schedule the synchronization process.

## Initialization

To use HTFS, just create a new instance, deliver a configuration, optionally do task customizations and run the HTFS thread.

Example:

```java
package com.innogames.htfs;

import com.innogames.htfs.config.SimpleConfig;
import com.innogames.htfs.domain.task.DebugTask;

public class Runner {

	public static void main(String[] args) throws Exception {
		Htfs htfs = new Htfs(new SimpleConfig(
			"/tmp/sync", // HDFS directory
			"hdfs://172.0.0.1:8020", // Namenode URI
			64*1024*1024, // HDFS block size
			1*1024, // HDFS block size buffer
			"/tmp/sync", // Local directory
			1, // System job interval
			60, // Write timeout for completed files
			1800 // Write timeout for incompleted files
		));

		htfs.run();
        htfs.join();
	}

}
```

## Configuration

There are two possible ways to configure HTFS - either via a file or using a `SimpleConfig` instance (like in the example above).

### FileConfig

In order to use the `FileConfig`, you need to provide a `.properties` file which contains the following values:

* __hdfs.directory__ : Target directory within the HDFS
* __hdfs.connection__ : Connection string for the namenode (e. g. `hdfs://172.0.0.1:8020`)
* __hdfs.blocksize__ : Configured block size within the HDFS
* __hdfs.blocksize.buffer__ : Buffer between block size and actual file size
* __system.directory__ : Source directory on local filesystem
* __system.job.interval__ : Interval in seconds for the sync process
* __file.complete.write.timeout__ : Seconds a file that reached block size must be unaltered before moving to HDFS
* __file.incomplete.write.timeout__ : Seconds a file that has not reached block size yet must be unaltered before moving to HDFS

Afterwards you can just create a new `FileConfig` using a `File` object, containing the `.properties` configuration:

```java
Htfs htfs = new Htfs(new FileConfig(new File("path/to/config.properties")));
```

### SimpleConfig

If you prefer inline configuration, the `SimpleConfig` can be used just by creating a new instance and deliver all configuration values:

```java
Htfs htfs = new Htfs(new SimpleConfig(
	"/tmp/sync", // HDFS directory
	"hdfs://172.0.0.1:8020", // Namenode URI
	64*1024*1024, // HDFS block size
	1*1024, // HDFS block size buffer
	"/tmp/sync", // Local directory
	1, // System job interval
	60, // Write timeout for completed files
	1800 // Write timeout for incompleted files
));
```

## Tasks

All operations within the HTFS are called tasks. __All__ known tasks are executed, __for each file__ that fulfills __at least one__ of the following conditions:

__File was not written for a specific time__:

    seconds since last modified >= file.incomplete.write.timeout

__File reached HDFS block size and is no longer written by another process__:

    ((file size % hdfs.blocksize) + hdfs.blocksize.buffer >= hdfs.blocksize) AND (seconds since last modified >= file.complete.write.timeout)

The tasks are divided into pre-, main- and posttasks. As default, there are no pre- or posttasks and only one maintask is executed: the `MoveToHdfsTask`.

This task moves the file from the local file system to the exact same location into the HDFS (the local file is deleted after successful transmition).

Additionally the library contains a `DebugTask` that will simply output the current file name that is processed with a logger on loglevel `debug`.

### FileSequenceTask

In general, the local HTFS directory will be synchronized with the HDFS 1:1 - so a file `/test.log` is moved to the HDFS to `/test.log` and if it is rewritten on the local system the file in the HDFS will be replaced. This behavior can be a problem if a process always writes continously into one file - so HTFS provides a pretask called `FileSequenceTask`.

If a is moved to the HDFS, the `FileSequenceTask` modifies the target filename and adds a sequencenumber to the filename, based on the amount of files with the exact same name in the HDFS target directory. So a file `/var/log/test.log` would be moved to `/var/log/test.log.1` - and if a file with the same name and path is moved again, it will be called `/var/log/test.log.2`.

To use this task, simply add it to your HTFS instance before running it:

```java
htfs.addPreTask(FileSequenceTask.class);
```

### PurgeTask

When a file is moved to the HDFS it is removed from the local file system but if there is an empty folder, it is not removed. To fulfill this requierment, HTFS provides the `PurgeTask`. It is a posttask that will recursively check the folder of the moved file backwards and deletes all empty folders.

To use this task, simply add it to your HTFS instance before running it:

```java
htfs.addPostTask(PurgeTask.class);
```

### Implement own tasks

To implement own tasks, just use the Task interface `com.innogames.htfs.domain.task.Task`.

Example:

```java
public class DebugTask extends Task {

	public DebugTask(ExecutionContext executionContext) {
		super(executionContext);
	}

	@Override
	public void execute() {
		LoggerFactory.getLogger(this.getClass()).debug(String.format("Processing: %s", this.getExecutionContext().get(ExecutionContext.KEY_FILE).toString()));
	}

}
```

### Add tasks to the system

To add an task to the system, so it will executed for each file that is fitting the conditions described above, just use the `addPreTask(task)`, `addMainTask(task)` or `addPostTask(task)` method.

Example:

```java
htfs.addPreTask(DebugTask.class);
```

## Monitoring

To monitor the HTFS, it provides a servlet that offers a JSON interface to retrieve all files within the HTFS and the so called __HTFS File Graph__.

Servlet location: `com.innogames.htfs.domain.servlet.GraphServlet`

### Jetty

To start a server and add the servlet based on Jetty (we used `8.1.9.v20130131`) you can use this snippet:

```java
Server server = new Server(8080);
ServletContextHandler context = new ServletContextHandler();
context.addServlet(new ServletHolder(new GraphServlet(htfs.getFileSystemMonitor())), "/");
server.setHandler(context);

server.start();
server.join();
```

### HTFS File Graph

According to the example above, the HTFS File Graph is reachable via port 8080.

It will display each file as a square. The more colorized a square gets, the more it is fulfilling the requirements to be moved to the HDFS. Since we have two conditions for moving to the HDFS, there are two colors:

* __green__: The file is close enough to HDFS block size for moving
* __blue__: The file is old enough for moving

If you hover a square, you will get additional information: filename, current filesize and seconds since last modification.

![File Graph](http://i.imgur.com/4UJedB9.png)

#### Customization

You can optional customize the HTFS File Graph with the following GET parameters:

* __columns__: Amount of columns displayed
* __size__: Size in px of each square
* __margin__: Margin in px between squares

`Hint: cat GraphServlet.java | grep Base64.decodeBase64 | sed 's/.*"\(.*\)".*/\1/g' | base64 --decode > template.html`

### JSON

The same servlet will recognize if the `Accept` header is set to `application/json`. In this case, it will respond with a valid JSON representation of all HTFS managed files.

![JSON](http://i.imgur.com/yUcZKgu.png)

## Full Example

```java
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
			1*1024, // HDFS block size buffer
			"/tmp/sync", // Local directory
			1, // System job interval
			60, // Write timeout for completed files
			1800 // Write timeout for incompleted files
		));

		htfs.addPreTask(FileSequenceTask.class);
		htfs.addPostTask(PurgeTask.class);

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
```

## Links

* [InnoGames](http://www.innogames.com)
* [Quartz](http://quartz-scheduler.org)
* [Cloudera CDH 4 VM](https://ccp.cloudera.com/display/SUPPORT/Cloudera's+Hadoop+Demo+VM+for+CDH4)