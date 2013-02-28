package com.innogames.htfs.domain.fs;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.google.gson.Gson;

public class HtfsFile {

	private final File file;
	private final int incompleteWriteTimeout;
	private final int completeWriteTimeout;
	private final int hdfsBlockSize;
	private final int hdfsBlocksizeBuffer;
	private String targetName;

	public HtfsFile(File file, int incompleteWriteTimeout, int completeWriteTimeout, int hdfsBlockSize, int hdfsBlocksizeBuffer) {
		this.file = file;
		this.incompleteWriteTimeout = incompleteWriteTimeout;
		this.completeWriteTimeout = completeWriteTimeout;
		this.hdfsBlockSize = hdfsBlockSize;
		this.hdfsBlocksizeBuffer = hdfsBlocksizeBuffer;
		this.targetName = file.getName();
	}

	public float getIncompleteWriteFactor() {
		return Math.min((float) this.getSecondsSinceLastModified() / (float) this.getIncompleteWriteTimeout(), 1f);
	}

	public float getCompleteWriteFactor() {
		float timeoutFactor = Math.min((float) this.getSecondsSinceLastModified() / (float) this.getCompleteWriteTimeout(), 1f);
		float sizeFactor = Math.min((((this.getLength() - 1) % this.getHdfsBlockSize()) + this.getHdfsBlocksizeBuffer()) / (float) this.getHdfsBlockSize(), 1f);

		return sizeFactor * .98f + timeoutFactor * .02f;
	}

	public long getLength() {
		return this.file.length();
	}

	public long getLastModified() {
		return this.file.lastModified();
	}

	public long getSecondsSinceLastModified() {
		return new Duration(new DateTime(this.getLastModified()), new DateTime()).getStandardSeconds();
	}

	public File getFile() {
		return this.file;
	}

	public int getIncompleteWriteTimeout() {
		return this.incompleteWriteTimeout;
	}

	public int getCompleteWriteTimeout() {
		return this.completeWriteTimeout;
	}

	public int getHdfsBlockSize() {
		return this.hdfsBlockSize;
	}

	public int getHdfsBlocksizeBuffer() {
		return this.hdfsBlocksizeBuffer;
	}

	public String getRelativePath(String systemPath) {
		return
			this.getFile().getParent().startsWith(systemPath) ?
			this.getFile().getAbsolutePath().replace(systemPath, "").replace(this.getFile().getName(), this.getTargetName()) :
			this.getFile().getAbsolutePath().replace(this.getFile().getName(), this.getTargetName());
	}

	public String getIdentifier() {
		try {
			return new String(Hex.encodeHexString(MessageDigest.getInstance("SHA-1").digest(this.getFile().getAbsolutePath().getBytes())));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return (new Gson()).toJson(this.toMap());
	}

	public Map<String, String> toMap() {
		Map<String, String> attributes = new HashMap<String, String>();

		attributes.put("identifier", this.getIdentifier());
		attributes.put("name", this.getFile().getAbsolutePath());
		attributes.put("size", String.valueOf(this.getLength()));
		attributes.put("seconds_since_last_modified", String.valueOf(this.getSecondsSinceLastModified()));
		attributes.put("last_modified", String.valueOf(this.getLastModified()));
		attributes.put("complete_write_timeout", String.valueOf(this.getCompleteWriteTimeout()));
		attributes.put("incomplete_write_timeout", String.valueOf(this.getIncompleteWriteTimeout()));
		attributes.put("hdfs_block_size", String.valueOf(this.getHdfsBlockSize()));
		attributes.put("hdfs_block_size_buffer", String.valueOf(this.getHdfsBlocksizeBuffer()));
		attributes.put("complete_write_factor", this.format(this.getCompleteWriteFactor()));
		attributes.put("incomplete_write_factor", this.format(this.getIncompleteWriteFactor()));

		return attributes;
	}

	private String format(float number) {
		DecimalFormat format = new DecimalFormat("#.#####");
		DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(Locale.getDefault());
		decimalSymbols.setDecimalSeparator('.');
		format.setDecimalFormatSymbols(decimalSymbols);

		return format.format(number);
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

}
