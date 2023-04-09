package com.truenine.component.rds.util;

import cn.hutool.core.lang.Snowflake;

/**
 * @author <a href="https://github.com/TAKETODAY">Harry Yang</a>
 * @since 1.0 2023/4/9 20:11
 */
public final class SnowflakeId {
	public static final String WORKER_ID = "snowflake.worker.id";
	public static final String DATA_CENTER_ID = "snowflake.data.center.id";

	private static final Snowflake snowflake = new Snowflake(
					Integer.getInteger(WORKER_ID, 1),
					Integer.getInteger(DATA_CENTER_ID, 1)
	);

	public static long nextId() {
		return snowflake.nextId();
	}

}
