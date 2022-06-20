package com.slegault.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class AbstractBuilder<T> {
	protected InputStream configStream;
	protected ObjectConsumer consumer;

	protected AbstractBuilder(ObjectConsumer consumer) {
		this.consumer = consumer;
	}

	public T configFile(File configFile) throws FileNotFoundException {
		this.configStream = new FileInputStream(configFile);
		return (T) this;
	}

	public T configUrl(URL configUrl) throws IOException {
		this.configStream = configUrl.openStream();
		return (T) this;
	}

	public T configStream(InputStream configStream) throws IOException {
		this.configStream = configStream;
		return (T) this;
	}

}
