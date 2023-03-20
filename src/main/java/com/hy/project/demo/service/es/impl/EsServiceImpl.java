package com.hy.project.demo.service.es.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.mapping.FieldType;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest.Builder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hy.project.demo.model.es.EsSearchRequest;
import com.hy.project.demo.model.es.EsSearchResult;
import com.hy.project.demo.service.es.AstService;
import com.hy.project.demo.service.es.EsService;
import com.hy.project.demo.util.AssertUtil;
import com.hy.project.demo.util.EnvUtil;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.exception.DemoExceptionEnum.CONFIGURATION_EXCEPTION;

/**
 * @author rick.wl
 * @date 2023/02/17
 */
@Service
public class EsServiceImpl implements EsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsServiceImpl.class);

    @Autowired
    AstService astService;

    @Override
    public Object search(EsSearchRequest request) throws Throwable {
        RestClient restClient = null;
        try {
            // 构建rest client
            restClient = buildRestClient();

            SearchRequest.Builder searchBuilder = new Builder();

            // TODO @RICK 先写死，后面用配置加白验证
            searchBuilder.index("demo-log-8.5.3");

            searchBuilder.from(request.getPageSize() * (request.getPageIndex() - 1));
            searchBuilder.size(request.getPageSize());

            searchBuilder.fields(f -> f.field(request.getFieldKey() + ".*"));
            //searchBuilder.source(sr -> sr.fetch(false));
            searchBuilder.sort(s -> s.field(f -> f.field(request.getTimestampKey()).order(SortOrder.Desc)
                .unmappedType(FieldType.Keyword)));

            //List<Query> musts = new ArrayList<>();
            //if (StringUtils.isNotBlank(request.getWildcardField()) && StringUtils.isNotBlank(
            //    request.getWildcardValue())) {
            //    Query wildcard = WildcardQuery.of(
            //        f -> f.field(request.getWildcardField()).value(request.getWildcardValue()))._toQuery();
            //    musts.add(wildcard);
            //}
            //
            //Query range = RangeQuery.of(f -> f.field("content.msec").gte(JsonData.of(request.getFrom()))
            //    .lt(JsonData.of(request.getTo())))._toQuery();
            //musts.add(range);
            //
            //Query query = BoolQuery.of(b -> b.must(musts))._toQuery();

            List<Query> musts = new ArrayList<>();

            // 查询范围
            Query rangeQuery = RangeQuery.of(f -> f.field(request.getTimestampKey()).gte(JsonData.of(request.getFrom()))
                .lt(JsonData.of(request.getTo())))._toQuery();
            musts.add(rangeQuery);

            // 查询日志文件类型
            Query logTypeQuery = TermQuery.of(
                f -> f.field("fields.fb_id.keyword").value(v -> v.stringValue(request.getLogType())))._toQuery();
            musts.add(logTypeQuery);

            // 用户输入的查询语句
            if (StringUtils.isNotBlank(request.getQuery())) {
                Query reqQuery = astService.buildEsQuery(request.getQuery());
                musts.add(reqQuery);
            }

            searchBuilder.query(BoolQuery.of(b -> b.must(musts))._toQuery());

            if (StringUtils.isNotBlank(request.getAfter())) {
                searchBuilder.searchAfter(FieldValue.of(request.getAfter()));
            }

            SearchRequest searchRequest = searchBuilder.build();

            LOGGER.info("final query request is: {}", searchRequest.toString());

            SearchResponse<ObjectNode> response = buildEsClient(restClient).search(searchRequest,
                ObjectNode.class);

            TotalHits total = response.hits().total();
            LOGGER.info("search result count: {}", total.value());

            List<JsonNode> resultData = new ArrayList<>();
            String nextAfter = null;
            if (null != response.hits() && null != response.hits().hits()) {
                for (Hit<ObjectNode> hit : response.hits().hits()) {
                    ObjectNode node = hit.source();
                    if (null == node) {
                        continue;
                    }

                    JsonNode jsonNode = node.get(request.getFieldKey());
                    if (null != jsonNode && !jsonNode.isNull()) {
                        resultData.add(jsonNode);
                    }
                }
            }

            if (resultData.size() > 0) {
                nextAfter = resultData.get(resultData.size() - 1).get("msec").asText();
            }

            return EsSearchResult.of(total.value(), resultData, nextAfter);

        } finally {
            if (null != restClient) {
                restClient.close();
            }
        }
    }

    private RestClient buildRestClient() throws Throwable {
        String certFilePath = EnvUtil.getEnvValue("es.cert.filePath");
        AssertUtil.notBlank(certFilePath, CONFIGURATION_EXCEPTION, "未找到配置：es.cert.filePath");

        String userName = EnvUtil.getEnvValue("es.user.name");
        AssertUtil.notBlank(userName, CONFIGURATION_EXCEPTION, "未找到配置：es.user.name");

        String pwdFilePath = EnvUtil.getEnvValue("es.user.pwdPath");
        AssertUtil.notBlank(pwdFilePath, CONFIGURATION_EXCEPTION, "未找到配置：es.user.pwdPath");

        // TODO @RICK 先从文件读，后面缓存到redis里面去
        String pwd = FileUtils.readFileToString(new File(pwdFilePath), "utf-8");
        AssertUtil.notBlank(pwd, CONFIGURATION_EXCEPTION, "未找到配置：es pwd");
        pwd = StringUtils.strip(pwd);

        // TODO @RICK 先从文件读，后面改成从配置中心读
        Configurations configs = new Configurations();
        String esHost = configs.properties("/var/host-info.config").getString("es_host");
        AssertUtil.notBlank(esHost, CONFIGURATION_EXCEPTION, "未找到配置：es_host");

        LOGGER.info("es rest client config: {}:{}:{}", userName, pwd, esHost);

        // Create the low-level client
        File certFile = new File(certFilePath);
        SSLContext sslContext = TransportUtils.sslContextFromHttpCaCrt(certFile);
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials(userName, pwd));

        RestClientBuilder builder = RestClient.builder(
            new HttpHost(esHost, 9200, "https"))
            .setHttpClientConfigCallback(hc -> hc
                .setSSLContext(sslContext)
                .setDefaultCredentialsProvider(credentialsProvider)
            );
        return builder.build();
    }

    private ElasticsearchClient buildEsClient(RestClient restClient) {
        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());

        // And create the API client
        return new ElasticsearchClient(transport);
    }
}
