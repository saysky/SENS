package com.liuyanzhao.sens.config.elaticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ElasticSearch 配置类
 * 实例化 client
 *
 * @author 言曌
 * @date 2019/2/2 下午3:55
 */

@Configuration
public class ESConfig {


    @Bean
    public RestHighLevelClient client() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
        return client;
    }
}

