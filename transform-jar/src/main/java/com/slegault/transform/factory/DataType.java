package com.slegault.transform.factory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public enum DataType implements Creator {

	BIGDECIMAL(new Creator() {
		@Override
		public BigDecimal create(String inputValue) throws Exception {
			return inputValue.isEmpty() ? null : new BigDecimal(inputValue);
		}
	}), BIGINTEGER(new Creator() {
		@Override
		public BigInteger create(String inputValue) throws Exception {
			return inputValue.isEmpty() ? null : new BigInteger(inputValue);
		}
	}), BOOLEAN(new Creator() {
		@Override
		public Boolean create(String inputValue) throws Exception {
			Boolean result;
			if (inputValue.isEmpty()) {
				result = null;
			} else if ("false".equalsIgnoreCase(inputValue)) {
				result = Boolean.FALSE;
			} else if ("true".equalsIgnoreCase(inputValue)) {
				result = Boolean.TRUE;
			} else {
				throw new IllegalArgumentException(inputValue);
			}
			return result;
		}
	}), DATE(new Creator() {
		@Override
		public synchronized Date create(String inputValue) throws Exception {
			// TODO: Refactor.
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
			return format.parse(inputValue, new ParsePosition(0));
		}
	}), DOUBLE(new Creator() {
		@Override
		public Double create(String inputValue) throws Exception {
			return inputValue.isEmpty() ? null : Double.valueOf(inputValue);
		}
	}), FLOAT(new Creator() {
		@Override
		public Float create(String inputValue) throws Exception {
			return inputValue.isEmpty() ? null : Float.valueOf(inputValue);
		}
	}), INTEGER(new Creator() {
		@Override
		public Integer create(String inputValue) throws Exception {
			return inputValue.isEmpty() ? null : Integer.valueOf(inputValue);
		}
	}), LONG(new Creator() {
		@Override
		public Long create(String inputValue) throws Exception {
			return inputValue.isEmpty() ? null : Long.valueOf(inputValue);
		}
	}), SHORT(new Creator() {
		@Override
		public Short create(String inputValue) throws Exception {
			return inputValue.isEmpty() ? null : Short.valueOf(inputValue);
		}
	}), STRING(new Creator() {
		@Override
		public String create(String inputValue) throws Exception {
			return inputValue;
		}
	}), URL(new Creator() {
		@Override
		public URL create(String inputValue) throws Exception {
			return inputValue.isEmpty() ? null : new URL(inputValue);
		}
	});

	Locale l;
	private Creator creator;

	private DataType(Creator creator) {
		this.creator = creator;
	}

	@Override
	public Object create(String inputValue) throws Exception {
		return creator.create(inputValue);
	}

}
