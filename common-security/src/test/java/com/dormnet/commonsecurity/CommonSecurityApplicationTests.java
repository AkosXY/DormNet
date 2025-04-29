package com.dormnet.commonsecurity;

import com.dormnet.commonsecurity.helper.JwtConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = JwtConverter.class)
class CommonSecurityApplicationTests {

	@Test
	void contextLoads() {
	}

}
