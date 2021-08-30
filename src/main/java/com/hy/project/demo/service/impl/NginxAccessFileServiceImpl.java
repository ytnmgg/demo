package com.hy.project.demo.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hy.project.demo.exception.DemoException;
import com.hy.project.demo.model.file.NginxAccessFileLine;
import com.hy.project.demo.repository.NginxAccessLogRepository;
import com.hy.project.demo.service.NginxAccessFileService;
import com.hy.project.demo.util.AssertUtil;
import com.hy.project.demo.util.DateUtil;
import com.hy.project.demo.util.HttpClientUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.exception.DemoExceptionEnum.CONFIGURATION_EXCEPTION;
import static com.hy.project.demo.exception.DemoExceptionEnum.FILE_EXCEPTION;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
@Service
public class NginxAccessFileServiceImpl implements NginxAccessFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    public static final String FILE_ENCODING = "UTF-8";
    public static final String LOG_PATTERN_STR =
        "^(.*)\\s-\\s(.*)\\s\\[(.*)]\\s\"(.*)\"\\s(.*)\\s(.*)\\s\"(.*)\"\\s\"(.*)\"\\s\"(.*)\"\\s*$";
    public static final Pattern LOG_PATTERN = Pattern.compile(LOG_PATTERN_STR);

    @Autowired
    private Environment env;

    @Autowired
    NginxAccessLogRepository nginxAccessLogRepository;

    @Override
    public List<NginxAccessFileLine> listLines(Date gmtBegin, Date gmtEnd) {
        return nginxAccessLogRepository.list(gmtBegin, gmtEnd);
    }

    @Override
    public void readAndStoreLines() {
        String logPath = env.getProperty("host.log.path");
        AssertUtil.notBlank(logPath, CONFIGURATION_EXCEPTION, "failed to find log path");

        String path = logPath + "/nginx/access.log";
        long lineCount = 100L;

        LOGGER.info("read and store lines: #1 get latest line");
        String fileMarker = null;
        long startLine = 1;
        NginxAccessFileLine latest = nginxAccessLogRepository.getLatest();
        if (null != latest) {
            fileMarker = latest.getFileMarker();
            startLine = latest.getLineNumber() + 1;
        }
        LOGGER.info("read and store lines: fileMarker={}, startLine={}", fileMarker, startLine);

        LOGGER.info("read and store lines: #2 read lines");
        List<NginxAccessFileLine> lines = readAccessLogLines(path, fileMarker, startLine, lineCount);
        LOGGER.info("read and store lines: lines count={}", lines.size());

        if (CollectionUtils.isNotEmpty(lines)) {
            LOGGER.info("read and store lines: #3 insert lines");
            lines.forEach(line -> nginxAccessLogRepository.insert(line));
        }
    }

    private List<NginxAccessFileLine> readAccessLogLines(String filePath, String fileMarker, long startLine,
        long linesCount) {

        File file = new File(filePath);

        AssertUtil.isTrue(file.isFile() && file.exists(), CONFIGURATION_EXCEPTION, "文件不存在：%s",
            filePath);

        List<NginxAccessFileLine> lines = new ArrayList<>();

        try {
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file), FILE_ENCODING);
            BufferedReader bufferedReader = new BufferedReader(streamReader);

            String lineContent;
            long lineNumber = 0;
            long startFrom = startLine;
            String thisFileMarker = null;
            while ((lineContent = bufferedReader.readLine()) != null) {

                lineNumber++;

                NginxAccessFileLine firstLine = null;
                if (lineNumber == 1) {
                    firstLine = parseAccessLogLine(lineContent, lineNumber, null);
                    AssertUtil.notNull(firstLine, FILE_EXCEPTION, "the first line is empty: %s", filePath);
                    thisFileMarker = firstLine.getFileMarker();
                    if (!isTheSameFile(fileMarker, thisFileMarker)) {
                        // 如果是新开的文件，则强制从第一行开始
                        startFrom = 1;
                        LOGGER.info("new file detected, start to parse from the first line.");
                    }
                }

                if (lineNumber >= startFrom) {

                    if (lineNumber == 1) {
                        lines.add(firstLine);
                    } else {

                        NginxAccessFileLine line = parseAccessLogLine(lineContent, lineNumber, thisFileMarker);
                        if (null == line) {
                            LOGGER.warn("empty line detected, ignore and continue");
                            continue;
                        }

                        lines.add(line);
                    }

                    if ((lineNumber - startFrom + 1) == linesCount) {
                        break;
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.error(String.format("failed to parse file：%s", e.getMessage()), e);
            throw new DemoException(FILE_EXCEPTION, e, "failed to parse file");
        }

        return lines;
    }

    private NginxAccessFileLine parseAccessLogLine(String lineContent, long lineNumber, String fileMarker) {
        if (StringUtils.isBlank(lineContent)) {
            return null;
        }

        try {
            Matcher m = LOG_PATTERN.matcher(lineContent);
            AssertUtil.isTrue(m.find() && m.groupCount() == 9, FILE_EXCEPTION, "nginx file format incorrect");

            NginxAccessFileLine fileLine = new NginxAccessFileLine();
            fileLine.setRemoteAddress(m.group(1));

            String dateStr = m.group(3);
            if (StringUtils.isNotBlank(dateStr)) {
                fileLine.setTimeLocal(DateUtil.parseNginxDate(dateStr));
            }

            fileLine.setRequest(m.group(4));
            fileLine.setStatus(m.group(5));

            String bytesStr = m.group(6);
            if (StringUtils.isNotBlank(bytesStr)) {
                fileLine.setBodyBytes(Long.valueOf(bytesStr));
            }

            fileLine.setHttpReferer(m.group(7));
            fileLine.setHttpUserAgent(m.group(8));
            fileLine.setLineContent(lineContent);
            fileLine.setLineNumber(lineNumber);

            if (StringUtils.isBlank(fileMarker)) {
                fileLine.setFileMarker(dateStr);
            } else {
                fileLine.setFileMarker(fileMarker);
            }
            return fileLine;

        } catch (Throwable e) {
            LOGGER.error(String.format("file line parse error：[%s][%s][%s]", lineNumber, lineContent, e.getMessage()),
                e);
            return null;
        }
    }

    private boolean isTheSameFile(String fileMarker, String thisFileMarker) {
        return StringUtils.equals(fileMarker, thisFileMarker);
    }
}
