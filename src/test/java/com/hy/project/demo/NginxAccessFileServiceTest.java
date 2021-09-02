package com.hy.project.demo;

import java.util.List;

import com.hy.project.demo.model.file.NginxAccessFileLine;
import com.hy.project.demo.model.nginx.NginxAccessLogStatusCount;
import com.hy.project.demo.service.NginxAccessFileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class NginxAccessFileServiceTest {

    @Autowired
    NginxAccessFileService nginxAccessFileService;

    @Test
    public void listLinesTest() {
        List<NginxAccessFileLine> lines =  nginxAccessFileService.listLines(null, null);
        System.out.println(lines);
    }

    @Test
    public void readAndStoreLinesTest() {
        nginxAccessFileService.readAndStoreLines();
    }

    @Test
    public void countStatusTest() {
        NginxAccessLogStatusCount count = nginxAccessFileService.countStatus();
        System.out.println(count);
    }
}
