package com.simple_cabinet_medical.Backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest(properties = {
		"management.endpoint.hawtio.enabled=true",
		"management.endpoint.jolokia.enabled=true",
		"spring.jmx.enabled=true"
})
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
