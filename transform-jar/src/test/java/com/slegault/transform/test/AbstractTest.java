package com.slegault.transform.test;

import java.net.URL;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

public abstract class AbstractTest {

	private static final String SCRIPT_ENGINE_MIME_TYPE = "text/javascript";
	private TestInfo testInfo;

	protected static final String CONFIG_SUFFIX = ".config.xlsx";
	protected static final String XLS_SUFFIX = ".xls";
	protected static final String XLSX_SUFFIX = ".xlsx";
	protected static final String CSV_SUFFIX = ".csv";
	protected static final String JS_SUFFIX = ".js";
	/**
	 * By convention, a data file is prefixed with the classname of the test that uses it.
	 */
	protected final String classPrefix = getClass().getName().replace('.', '/');

	protected ScriptEngine jsEngine;

	protected AbstractTest() {
		jsEngine = new ScriptEngineManager().getEngineByMimeType(SCRIPT_ENGINE_MIME_TYPE);
	}

	@BeforeEach
	void beforeEach(TestInfo testInfo) {
		this.testInfo = testInfo;
	}

	/**
	 * Returns a URL for the file name prefixed with this class name.
	 */
	protected URL getClassUrl(String suffix) throws Exception {
		return getSiblingUrl(getClassFileName(suffix));
	}

	/** Returns a file name prefixed with this class name. */
	protected String getClassFileName(String suffix) {
		if (suffix == null) {
			suffix = XLSX_SUFFIX;
		}
		return classPrefix + suffix;
	}

	/**
	 * Returns a URL for the file name prefixed with the currently running method's qualified name.
	 */
	protected URL getMethodUrl(String suffix) throws Exception {
		return getSiblingUrl(getMethodFileName(suffix));
	}

	/**
	 * Returns a file name prefixed with the currently running method's qualified name.
	 */
	protected String getMethodFileName(String suffix) {
		if (suffix == null) {
			suffix = XLSX_SUFFIX;
		}
		String displayName = testInfo.getDisplayName();
		String methodName = displayName.substring(0, displayName.indexOf('('));
		return classPrefix + '.' + methodName + suffix;
	}

	private URL getSiblingUrl(String siblingFileName) throws Exception {
		URL result = Thread.currentThread().getContextClassLoader().getResource(siblingFileName);
		Assertions.assertNotNull(result, "Resource not found on classpath: " + siblingFileName);
		System.out.println("----------");
		System.out.println(siblingFileName);
		return result;
	}

	protected void assertTrue(Object actual, String expression) throws ScriptException {
		if (actual instanceof String) {
			// TODO: Ugly! Refactor.
			expression = "actual = " + actual + "; " + expression;
		} else {
			Bindings bindings = jsEngine.getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put("actual", actual);
		}
		Object result = jsEngine.eval(expression);
		assertInstanceOf(Boolean.class, result);
		Assertions.assertTrue((Boolean) result, expression);
	}

	/** Asserts that {@code actual} is an instance of the {@code expected} class. */
	public static void assertInstanceOf(Class<?> expected, Object actual) {
		if (expected == null) {
			Assertions.assertEquals(expected, actual);
		} else if (!expected.isInstance(actual)) {
			throw new RuntimeException(
					"Actual is not an instance of " + expected.toString() + ": " + actual == null ? "null" : actual.getClass().toString());
		}
	}

}
