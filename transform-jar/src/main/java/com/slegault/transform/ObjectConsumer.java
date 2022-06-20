package com.slegault.transform;

public interface ObjectConsumer {

	/**
	 * Implement this interface to consume the object produced by the producer.
	 * @param object Object produced by the producer.
	 * @throws Exception Do NOT catch exception unless you are adding value. By convention, the producer handles exceptions.
	 */
	void object(Object object) throws Exception;
	
}
