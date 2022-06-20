package com.slegault.transform;

import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.slegault.transform.test.AbstractTest;

public class ExcelInputTest extends AbstractTest implements ObjectConsumer, ExceptionConsumer {

	private Object object;
	private Exception exception;
	private int objectCount;
	private int exceptionCount;

	@BeforeEach
	void beforeEach() {
		objectCount = 0;
		exceptionCount = 0;
	}
	
	
	@Test
	void builder() throws Exception {
		ExcelInput excelInput = new ExcelInput.Builder(this, this).configUrl(getClassUrl(CONFIG_SUFFIX)).build();
		Assertions.assertNotNull(excelInput);
	}

	@Test
	void oneSheet() throws Exception {
		ExcelInput excelInput = new ExcelInput.Builder(this, this).configUrl(getClassUrl(CONFIG_SUFFIX)).build();
		URL xlsxUrl = this.getMethodUrl(XLSX_SUFFIX);
		excelInput.process(xlsxUrl);
		URL xlsUrl = this.getMethodUrl(XLS_SUFFIX);
		excelInput.process(xlsUrl);
		Assertions.assertEquals(8, objectCount);
		Assertions.assertEquals(0, exceptionCount);
	}

	@Test
	void missingMandatory() throws Exception {
		ExcelInput excelInput = new ExcelInput.Builder(this, this).configUrl(getClassUrl(CONFIG_SUFFIX)).build();
		URL xlsxUrl = this.getMethodUrl(XLSX_SUFFIX);
		excelInput.process(xlsxUrl);
		Assertions.assertEquals(2, objectCount);
		Assertions.assertEquals(3, exceptionCount);
	}

	// ExcelInput output will be streamed here.

	@Override
	public void object(Object object) throws Exception {
		this.object = object;
		this.objectCount ++;
		System.out.println(object);
	}

	@Override
	public void exception(Exception exception) {
		this.exception = exception;
		this.exceptionCount ++;
		exception.printStackTrace();
	}

}
