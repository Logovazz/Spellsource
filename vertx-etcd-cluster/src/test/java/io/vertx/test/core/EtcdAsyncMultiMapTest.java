package io.vertx.test.core;


import com.google.protobuf.ByteString;
import io.etcd.jetcd.api.DeleteRangeRequest;
import io.etcd.jetcd.launcher.junit4.EtcdClusterResource;
import io.vertx.core.shareddata.AsyncMultiMapTest;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.etcd.EtcdClusterManager;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:guoyu.511@gmail.com">Guo Yu</a>
 */
public class EtcdAsyncMultiMapTest extends AsyncMultiMapTest {

	private AtomicInteger prefix = new AtomicInteger();

	@ClassRule
	public static final EtcdClusterResource cluster = new EtcdClusterResource("test-etcd", 3, false, false);

	@Before
	@Override
	public void before() throws Exception {
		prefix.incrementAndGet();
		super.before();
	}

	@Override
	protected ClusterManager getClusterManager() {
		return new EtcdClusterManager(cluster.getClientEndpoints().get(0).getHost(), cluster.getClientEndpoints().get(0).getPort(), "vertx-test-" + prefix.get());
	}
}
