package com.slegault.transform;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.slegault.transform.test.AbstractTest;

public class JavaScriptMapperTest extends AbstractTest implements ObjectConsumer {

	private Object object;
	private Exception exception;

	@Test
	void oneObject() throws Exception {
		JavaScriptMapper jsMapper = new JavaScriptMapper.Builder(this).configUrl(this.getClassUrl(CONFIG_SUFFIX)).build();

		Map<String, Object> input = new HashMap<>();
		input.put("firstName", "Marilyn");
		input.put("lastName", "Monroe");
		input.put("birthDate", "1926-06-01");
		input.put("deathDate", "1962-08-05");
		input.put("civicNumber", "12305");
		input.put("street", "Fifth Helena Drive");
		input.put("municipality", "Los Angeles");
		input.put("state", "CA");
		input.put("zipCode", "90049");
		input.put("phone1", "310-652-0271");
		input.put("phone2", null);
		System.out.println(input);

		jsMapper.object(input);

		Assertions.assertNotNull(object);
		assertTrue(object, "actual.name === 'Marilyn Monroe'");
		assertTrue(object, "actual.life.dob === '1926-06-01'");
		assertTrue(object, "actual.life.dod === '1962-08-05'");
		assertTrue(object, "actual.address.civicNumber === '12305'");
		assertTrue(object, "actual.address.street === 'Fifth Helena Drive'");
		assertTrue(object, "actual.address.municipality === 'Los Angeles'");
		assertTrue(object, "actual.address.state === 'CA'");
		assertTrue(object, "actual.address.zipCode === '90049'");
		assertTrue(object, "actual.phones[0] === '310-652-0271'");
		assertTrue(object, "actual.phones[1] == null");
		// IMPORTANT: Nashorn returns JavaScript array as a Java Map, like any other object!!!
		Map<String, Object> output = (Map<String, Object>) object;
		Map<String, Object> phones = (Map<String, Object>) output.get("phones");
		Assertions.assertEquals("310-652-0271", phones.get("0"));
		Assertions.assertEquals(null, phones.get("1"));
	}

	@Test
	void outputAsJson() throws Exception {
		JavaScriptMapper jsMapper = new JavaScriptMapper.Builder(this).configUrl(this.getClassUrl(CONFIG_SUFFIX)).asJson().build();

		Map<String, Object> input = new HashMap<>();
		input.put("firstName", "Marilyn");
		input.put("lastName", "Monroe");
		input.put("birthDate", "1926-06-01");
		input.put("deathDate", "1962-08-05");
		input.put("civicNumber", "12305");
		input.put("street", "Fifth Helena Drive");
		input.put("municipality", "Los Angeles");
		input.put("state", "CA");
		input.put("zipCode", "90049");
		input.put("phone1", "310-652-0271");
		input.put("phone2", null);
		System.out.println(input);

		jsMapper.object(input);

		assertInstanceOf(String.class, object);
		System.out.println(object);

		assertTrue(object, "actual.name === 'Marilyn Monroe'");
		assertTrue(object, "actual.life.dob === '1926-06-01'");
		assertTrue(object, "actual.life.dod === '1962-08-05'");
		assertTrue(object, "actual.address.civicNumber === '12305'");
		assertTrue(object, "actual.address.street === 'Fifth Helena Drive'");
		assertTrue(object, "actual.address.municipality === 'Los Angeles'");
		assertTrue(object, "actual.address.state === 'CA'");
		assertTrue(object, "actual.address.zipCode === '90049'");
		assertTrue(object, "actual.phones[0] === '310-652-0271'");
		assertTrue(object, "actual.phones[1] == null");
	}

	@Override
	public void object(Object object) throws Exception {
		this.object = object;
	}

}
