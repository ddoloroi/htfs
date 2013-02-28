package com.innogames.htfs.config;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class FileConfig extends PropertiesConfiguration implements Config {

	public FileConfig(File file) throws ConfigurationException {
		this.load(file);
		this.setThrowExceptionOnMissing(true);
	}

	@Override
	public String getHdfsDirectory() {
		return this.getString("hdfs.directory");
	}

	@Override
	public String getHdfsConnection() {
		return this.getString("hdfs.connection");
	}

	@Override
	public Integer getHdfsBlocksize() {
		return this.getInteger("hdfs.blocksize", 0);
	}

	@Override
	public Integer getHdfsBlocksizeBuffer() {
		return this.getInteger("hdfs.blocksize.buffer", 0);
	}

	@Override
	public String getSystemDirectory() {
		return this.getString("system.directory");
	}

	@Override
	public Integer getSystemJobInterval() {
		return this.getInteger("system.job.interval", 0);
	}

	@Override
	public Integer getFileCompleteWriteTimeout() {
		return this.getInteger("file.complete.write.timeout", 0);
	}

	@Override
	public Integer getFileIncompleteWriteTimeout() {
		return this.getInteger("file.incomplete.write.timeout", 0);
	}

}
