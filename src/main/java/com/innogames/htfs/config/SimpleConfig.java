package com.innogames.htfs.config;

public class SimpleConfig implements Config {

	private final String hdfsDirectory;
	private final String hdfsConnection;
	private final Integer hdfsBlocksize;
	private final Integer hdfsBlocksizeBuffer;
	private final String systemDirectory;
	private final Integer systemJobInterval;
	private final Integer fileCompleteWriteTimeout;
	private final Integer fileIncompleteWriteTimeout;

	public SimpleConfig(String hdfsDirectory, String hdfsConnection, Integer hdfsBlocksize, Integer hdfsBlocksizeBuffer, String systemDirectory, Integer systemJobInterval, Integer fileCompleteWriteTimeout, Integer fileIncompleteWriteTimeout) {
		this.hdfsDirectory = hdfsDirectory;
		this.hdfsConnection = hdfsConnection;
		this.hdfsBlocksize = hdfsBlocksize;
		this.hdfsBlocksizeBuffer = hdfsBlocksizeBuffer;
		this.systemDirectory = systemDirectory;
		this.systemJobInterval = systemJobInterval;
		this.fileCompleteWriteTimeout = fileCompleteWriteTimeout;
		this.fileIncompleteWriteTimeout = fileIncompleteWriteTimeout;
	}

	@Override
	public String getHdfsDirectory() {
		return this.hdfsDirectory;
	}

	@Override
	public String getHdfsConnection() {
		return this.hdfsConnection;
	}

	@Override
	public Integer getHdfsBlocksize() {
		return this.hdfsBlocksize;
	}

	@Override
	public Integer getHdfsBlocksizeBuffer() {
		return this.hdfsBlocksizeBuffer;
	}

	@Override
	public String getSystemDirectory() {
		return this.systemDirectory;
	}

	@Override
	public Integer getSystemJobInterval() {
		return this.systemJobInterval;
	}

	@Override
	public Integer getFileCompleteWriteTimeout() {
		return this.fileCompleteWriteTimeout;
	}

	@Override
	public Integer getFileIncompleteWriteTimeout() {
		return this.fileIncompleteWriteTimeout;
	}

}
