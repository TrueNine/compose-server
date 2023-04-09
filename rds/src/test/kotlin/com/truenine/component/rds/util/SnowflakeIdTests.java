package com.truenine.component.rds.util;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 * @since 1.0 2023/4/9 20:12
 */
public class SnowflakeIdTests {

	@Test
	void nextId() {
		ArrayList<Long> list = new ArrayList<>();

		for (int i = 0; i < 1000_00; i++) {
			long id = SnowflakeId.nextId();
			list.add(id);
		}

		assertThat(list.size()).isEqualTo(new HashSet<>(list).size());
	}

}