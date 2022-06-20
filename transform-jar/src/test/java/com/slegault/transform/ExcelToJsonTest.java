package com.slegault.transform;

import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.slegault.transform.test.AbstractTest;

public class ExcelToJsonTest extends AbstractTest implements ObjectConsumer, ExceptionConsumer {

	private Object object;
	private Exception exception;

	@Test
	void builder() throws Exception {
		URL configUrl = this.getClassUrl(CONFIG_SUFFIX);
		JavaScriptMapper jsMapper = new JavaScriptMapper.Builder(this).configUrl(configUrl).asJson().build();
		ExcelInput excelInput = new ExcelInput.Builder(jsMapper, this).configUrl(configUrl).build();
		Assertions.assertNotNull(excelInput);
	}

	@Test
	void twoSheets() throws Exception {
		URL configUrl = this.getClassUrl(CONFIG_SUFFIX);
		JavaScriptMapper jsMapper = new JavaScriptMapper.Builder(this).configUrl(configUrl).asJson().build();
		ExcelInput excelInput = new ExcelInput.Builder(jsMapper, this).configUrl(configUrl).build();
		URL dataUrl = this.getMethodUrl(XLSX_SUFFIX);
		excelInput.process(dataUrl);
	}

	@Override
	public void object(Object object) throws Exception {
		this.object = object;
		System.out.println(object);
	}

	@Override
	public void exception(Exception exception) {
		this.exception = exception;
		exception.printStackTrace();
	}

}
