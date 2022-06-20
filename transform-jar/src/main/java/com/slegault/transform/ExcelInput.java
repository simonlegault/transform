package com.slegault.transform;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.slegault.transform.factory.DataType;

public class ExcelInput {

	private static final String SHEET_NAME = "input";

	private ObjectConsumer objectConsumer;

	private ExceptionConsumer exceptionConsumer;

	private List<InputColumn> columns;

	private DataFormatter dataFormatter;

	// private int rowNumber;
	// private int columnNumber;

	private class InputColumn {
		private String columnName;
		private String varName;
		private DataType varType;
		private String format;
		private boolean mandatory;
	}

	public static class Builder extends AbstractBuilder<Builder> {
		ExceptionConsumer exceptionConsumer;

		public Builder(ObjectConsumer consumer, ExceptionConsumer exceptionConsumer) {
			super(consumer);
			this.exceptionConsumer = exceptionConsumer;
		}

		public ExcelInput build() throws Exception {
			return new ExcelInput(this);
		}
	}

	public ExcelInput(Builder builder) throws IOException {
		this.objectConsumer = builder.consumer;
		this.exceptionConsumer = builder.exceptionConsumer;
		columns = new ArrayList<InputColumn>();
		dataFormatter = new DataFormatter(Locale.CANADA, true);
		try (Workbook workbook = WorkbookFactory.create(builder.configStream)) {
			Sheet sheet = workbook.getSheet(SHEET_NAME);
			if (sheet == null) {
				sheet = workbook.getSheetAt(0);
			}
			Iterator<Row> iterator = sheet.iterator();
			Row row = iterator.next();
			// TODO: Process header row - to skip optional rows
			while (iterator.hasNext()) {
				row = iterator.next();
				InputColumn column = new InputColumn();
				column.columnName = getCellString(row, 0);
				column.varName = getCellString(row, 1);
				// TODO: Generate standard var name, if none.
				String varTypeName = getCellString(row, 2);
				column.varType = varTypeName.isEmpty() ? DataType.STRING : DataType.valueOf(varTypeName.toUpperCase());
				column.format = getCellString(row, 3);
				// TODO: Do something with the format.
				column.mandatory = Boolean.parseBoolean(getCellString(row, 4));
				columns.add(column);
			}
		}
	}

	private String getCellString(Row row, int no) {
		Cell cell = row.getCell(no);
		String result = dataFormatter.formatCellValue(cell);
		return result;
	}

	public void process(URL fileUrl) throws IOException {
		try (InputStream inputStream = fileUrl.openStream()) {
			try (Workbook workbook = WorkbookFactory.create(inputStream)) {
				for (Sheet sheet : workbook) {
					Iterator<Row> rowIterator = sheet.iterator();
					Row row = rowIterator.next();
					// TODO: Process header row - if present, reorder columns, matching actual column names.
					while (rowIterator.hasNext()) {
						row = rowIterator.next();
						int columnIndex = -1;
						// Process one row.
						try {
							Map<String, Object> object = new HashMap<String, Object>();
							for (columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
								InputColumn column = columns.get(columnIndex);
								Cell cell = row.getCell(columnIndex);
								String stringValue = dataFormatter.formatCellValue(cell);
								if (column.mandatory && stringValue.isEmpty()) {
									throw new Exception(column.varName + " is mandatory");
								}
								Object value = column.varType.create(stringValue);
								object.put(column.varName, value);
							}
							objectConsumer.object(object);
						} catch (Exception x) {
							// Adding exception context. IMPORTANT: Row & Column are ONE-based.
							String messaqe = "File: " + fileUrl + "; Sheet: " + sheet.getSheetName() + "; Row: " + (row.getRowNum() + 1)
									+ "; Column: " + (columnIndex + 1) + "; Cause: " + x.getMessage();
							exceptionConsumer.exception(new Exception(messaqe, x));
						}
					}
				}
			}
		}
	}

}
