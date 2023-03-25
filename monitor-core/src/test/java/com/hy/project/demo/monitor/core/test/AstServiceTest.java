package com.hy.project.demo.monitor.core.test;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.hy.project.demo.monitor.core.MonitorCoreApplication;
import com.hy.project.demo.monitor.core.service.AstService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author rick.wl
 * @date 2021/11/05
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MonitorCoreApplication.class)
@TestPropertySource("classpath:test.properties")
public class AstServiceTest {

    @Autowired
    AstService astService;

    @Test
    public void simple01Test() {
        String sql = "(id=3 or id=4) and name=xxx and (time<'331' or !(month > 2 and age != 10))";
        Query query = astService.buildEsQuery(sql);
        System.out.println(query.toString());
    }

    @Test
    public void simple02Test() {
        String sql = "content.uri='/auth/role/list.json' AND content.request_time<='0.063'";
        Query query = astService.buildEsQuery(sql);
        System.out.println(query.toString());
    }

}
