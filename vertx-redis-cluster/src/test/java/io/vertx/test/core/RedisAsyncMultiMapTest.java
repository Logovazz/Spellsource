package io.vertx.test.core;


import com.hiddenswitch.containers.RedisContainer;
import io.vertx.core.shareddata.AsyncMultiMapTest;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.redis.RedisClusterManager;
import org.junit.Before;
import org.junit.ClassRule;

/**
 * @author <a href="mailto:guoyu.511@gmail.com">Guo Yu</a>
 */
public class RedisAsyncMultiMapTest extends AsyncMultiMapTest {

	@ClassRule
	public static RedisContainer redisContainer = new RedisContainer();

	@Override
	@Before
	public void before() throws Exception {
		redisContainer.clear();
		super.before();
	}

	@Override
	protected ClusterManager getClusterManager() {
		return new RedisClusterManager(redisContainer.getRedisUrl());
	}
}
