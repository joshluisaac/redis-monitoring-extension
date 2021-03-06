/*
 * Copyright 2013. AppDynamics LLC and its affiliates.
 *  * All Rights Reserved.
 *  * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *  * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.redis.metrics;

import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.metrics.Metric;
import com.appdynamics.extensions.yml.YmlReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.*;


public class InfoMetricsTest {


    @Test
    public void infoMetricsTest() throws IOException{
        ArgumentCaptor<List> pathCaptor = ArgumentCaptor.forClass(List.class);
        MonitorContextConfiguration monitorContextConfiguration = mock(MonitorContextConfiguration.class);
        Map<String, ?> rootElem = YmlReader.readFromFileAsMap(new File("src/test/resources/conf/config.yml"));
        doReturn(rootElem).when(monitorContextConfiguration).getConfigYml();
        when(monitorContextConfiguration.getMetricPrefix()).thenReturn("Server|Component:AppLevels|Custom Metrics|Redis");
        MetricWriteHelper metricWriteHelper = mock(MetricWriteHelper.class);
        JedisPool jedisPool = mock(JedisPool.class);
        Jedis jedis = mock(Jedis.class);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        when(jedisPool.getResource()).thenReturn(jedis);
        String info = FileUtils.readFileToString(new File("src/test/resources/info.txt"));
        when(jedis.info()).thenReturn(info);
        Map<String, String> server = Maps.newHashMap();
        server.put("host", "localhost");
        server.put("port", "6379");
        server.put("name", "Server1");
        InfoMetrics redisMetrics = new InfoMetrics(monitorContextConfiguration, server, metricWriteHelper,jedisPool, countDownLatch);
        redisMetrics.run();
        verify(metricWriteHelper).transformAndPrintMetrics(pathCaptor.capture());
        List<String> metricPathsList = Lists.newArrayList();
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Clients|connected_clients");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Clients|client_longest_output_list");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Clients|client_biggest_input_buf");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Memory|used_memory");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Memory|used_memory_rss");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Memory|used_memory_peak");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Memory|used_memory_lua");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Memory|mem_fragmentation_ratio");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Persistence|rdb_changes_since_last_save");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Persistence|rdb_last_bgsave_time_sec");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Persistence|rdb_current_bgsave_time_sec");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Persistence|aof_last_rewrite_time_sec");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Persistence|aof_current_rewrite_time_sec");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Stats|total_connections_received");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Stats|total_commands_processed");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Stats|instantaneous_ops_per_sec");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Stats|rejected_connections");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Stats|expired_keys");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Stats|evicted_keys");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Stats|keyspace_hits");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Stats|keyspace_misses");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Stats|pubsub_channels");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Stats|pubsub_patterns");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Stats|latest_fork_usec");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|Replication|connected_slaves");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|CPU|used_cpu_sys");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|CPU|used_cpu_user");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|CPU|used_cpu_sys_children");
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|CPU|used_cpu_user_children");
        for (Metric metric : (List<Metric>)pathCaptor.getValue()){
            org.junit.Assert.assertTrue(metricPathsList.contains(metric.getMetricPath()));
        }
    }


    @Test
    public void whenConnectionFailsShouldReturnFailedHeartBeatTest() throws IOException{
        ArgumentCaptor<List> pathCaptor = ArgumentCaptor.forClass(List.class);
        MonitorContextConfiguration monitorContextConfiguration = mock(MonitorContextConfiguration.class);
        Map<String, ?> rootElem = YmlReader.readFromFileAsMap(new File("src/test/resources/conf/config.yml"));
        doReturn(rootElem).when(monitorContextConfiguration).getConfigYml();
        when(monitorContextConfiguration.getMetricPrefix()).thenReturn("Server|Component:AppLevels|Custom Metrics|Redis");
        MetricWriteHelper metricWriteHelper = mock(MetricWriteHelper.class);
        JedisPool jedisPool = mock(JedisPool.class);
        Jedis jedis = mock(Jedis.class);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        when(jedisPool.getResource()).thenReturn(jedis);
        when(jedis.info()).thenThrow(new RuntimeException());
        Map<String, String> server = Maps.newHashMap();
        server.put("host", "localhost");
        server.put("port", "6379");
        server.put("name", "Server1");
        InfoMetrics redisMetrics = new InfoMetrics(monitorContextConfiguration, server, metricWriteHelper,jedisPool, countDownLatch);
        redisMetrics.run();
        verify(metricWriteHelper).transformAndPrintMetrics(pathCaptor.capture());
        List<String> metricPathsList = Lists.newArrayList();
        metricPathsList.add("Server|Component:AppLevels|Custom Metrics|Redis|Server1|HeartBeat");
        for (Metric metric : (List<Metric>)pathCaptor.getValue()){
            org.junit.Assert.assertTrue(metricPathsList.contains(metric.getMetricPath()));
        }
    }
}
