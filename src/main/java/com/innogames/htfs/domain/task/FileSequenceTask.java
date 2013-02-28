package com.innogames.htfs.domain.task;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

import com.innogames.htfs.domain.fs.HtfsFile;
import com.innogames.htfs.domain.task.context.ExecutionContext;

public class FileSequenceTask extends Task {

	public FileSequenceTask(ExecutionContext executionContext) {
		super(executionContext);
	}

	@Override
	public void execute() throws Exception {
		HtfsFile htfsFile = this.getHtfsFile();
		String relativePath = htfsFile.getRelativePath(this.getConfig().getSystemDirectory());
		Path hdfsPath = new Path(String.format("%s/%s", this.getConfig().getHdfsDirectory(), relativePath.substring(0, relativePath.lastIndexOf("/"))));
		FileSystem hdfs = this.getHdfs();

		int sequenceNumber = 0;

		if(hdfs.exists(hdfsPath)) {
			RemoteIterator<LocatedFileStatus> it = hdfs.listFiles(hdfsPath, false);

			while(it.hasNext()) {
				LocatedFileStatus file = it.next();
				if(file.isFile()) {
					String name = file.getPath().getName();
					int delimiterIndex = name.lastIndexOf(".");

					if(
						delimiterIndex > -1 &&
						name.length() - 1 != delimiterIndex &&
						name.substring(0, delimiterIndex).equals(htfsFile.getFile().getName()) &&
						NumberUtils.isDigits(name.substring(delimiterIndex + 1))
					) {
						sequenceNumber = Math.max(sequenceNumber, Integer.valueOf(name.substring(delimiterIndex + 1)));
					}
				}
			}
		}

		htfsFile.setTargetName(String.format("%s.%d", htfsFile.getTargetName(), sequenceNumber + 1));
	}

}
