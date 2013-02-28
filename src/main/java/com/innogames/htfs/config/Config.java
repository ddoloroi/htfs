package com.innogames.htfs.config;

public interface Config {

	public String getHdfsDirectory();
	public String getHdfsConnection();
	public Integer getHdfsBlocksize();
	public Integer getHdfsBlocksizeBuffer();

	public String getSystemDirectory();
	public Integer getSystemJobInterval();

	public Integer getFileCompleteWriteTimeout();
	public Integer getFileIncompleteWriteTimeout();

}
