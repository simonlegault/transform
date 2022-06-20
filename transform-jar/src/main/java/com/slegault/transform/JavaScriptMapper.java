package com.slegault.transform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class JavaScriptMapper implements ObjectConsumer {

	private static final String SHEET_NAME = "transform";

	private static final String SCRIPT_ENGINE_MIME_TYPE = "text/javascript";

	private ObjectConsumer consumer;
	private String inputName;
	private String outputName;

	private ScriptEngine jsEngine;
	private DataFormatter dataFormatter;
	private List<Transformation> transformations;

	private class Transformation {
		private String statement;
		private CompiledScript compiledStatement;
	}

	public static class Builder extends AbstractBuilder<Builder> {
		private String inputName = "input";
		private String outputName = "output";
		private boolean outputAsJson = false;

		public Builder(ObjectConsumer consumer) {
			super(consumer);
		}

		public Builder inputName(String inputName) {
			this.inputName = inputName;
			return this;
		}

		public Builder outputName(String outputName) {
			this.outputName = outputName;
			return this;
		}

		public Builder asJson() {
			this.outputAsJson = true;
			return this;
		}

		public JavaScriptMapper build() throws Exception {
			return new JavaScriptMapper(this);
		}
	}

	public JavaScriptMapper(Builder builder) throws Exception {
		this.consumer = builder.consumer;
		this.inputName = builder.inputName;
		this.outputName = builder.outputName;
		jsEngine = new ScriptEngineManager().getEngineByMimeType(SCRIPT_ENGINE_MIME_TYPE);
		transformations = new ArrayList<Transformation>();
		dataFormatter = new DataFormatter(Locale.CANADA, true);
		try (Workbook workbook = new XSSFWorkbook(builder.configStream)) {
			Sheet sheet = workbook.getSheet(SHEET_NAME);
			if (sheet == null) {
				if (workbook.getNumberOfSheets() > 1) {
					throw new Exception("Transformation configuration must be the only sheet in the workbook or the sheet named " + SHEET_NAME);
				}
				sheet = workbook.getSheetAt(0);
			}
			Iterator<Row> iterator = sheet.iterator();
			Row row = iterator.next();
			// TODO: Process header row - to skip optional rows
			while (iterator.hasNext()) {
				row = iterator.next();
				addTransformation(getCellString(row, 0));
			}
			addTransformation(builder.outputAsJson ? "JSON.stringify(" + outputName + ")" : outputName);
		}
	}

	private void addTransformation(String statement) throws ScriptException {
		Transformation transformation = new Transformation();
		transformation.statement = statement;
		transformation.compiledStatement = ((Compilable) jsEngine).compile(transformation.statement);
		transformations.add(transformation);

	}

	private String getCellString(Row row, int no) {
		Cell cell = row.getCell(no);
		String result = dataFormatter.formatCellValue(cell);
		return result;
	}

	@Override
	public void object(Object input) throws Exception {
		Bindings bindings = jsEngine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put(inputName, input);
		Object output = null;
		for (Transformation transformation : transformations) {
			try {
				output = transformation.compiledStatement.eval(bindings);
			} catch (Exception x) {
				throw new Exception(transformation.statement, x);
			}
		}
		consumer.object(output);
	}

}
