package com.hy.project.demo.monitor.core.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.hy.project.demo.common.exception.DemoException;
import com.hy.project.demo.common.model.PageResult;
import com.hy.project.demo.common.util.AssertUtil;
import com.hy.project.demo.common.util.DateUtil;
import com.hy.project.demo.monitor.core.mybatis.entity.NginxAccessLogPointDO;
import com.hy.project.demo.monitor.core.mybatis.entity.NginxAccessLogStatusCountDO;
import com.hy.project.demo.monitor.core.repository.NginxAccessLogRepository;
import com.hy.project.demo.monitor.facade.model.file.NginxAccessFileLine;
import com.hy.project.demo.monitor.facade.model.nginx.NginxAccessLogPointModel;
import com.hy.project.demo.monitor.facade.model.nginx.NginxAccessLogStatusCount;
import com.hy.project.demo.monitor.facade.model.nginx.NginxAccessLogStatusCountModel;
import com.hy.project.demo.monitor.facade.service.NginxAccessFileService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.common.exception.DemoExceptionEnum.CONFIGURATION_EXCEPTION;
import static com.hy.project.demo.common.exception.DemoExceptionEnum.FILE_EXCEPTION;
import static com.hy.project.demo.common.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;
import static com.hy.project.demo.common.exception.DemoExceptionEnum.UNEXPECTED;
import static com.hy.project.demo.common.util.DateUtil.addHours;
import static com.hy.project.demo.common.util.DateUtil.dayDiff;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
@Service
public class NginxAccessFileServiceImpl implements NginxAccessFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NginxAccessFileServiceImpl.class);

    public static final String FILE_ENCODING = "UTF-8";
    public static final String LOG_PATTERN_STR =
        "^(.*)\\s-\\s(.*)\\s\\[(.*)]\\s\"(.*)\"\\s(.*)\\s(.*)\\s\"(.*)\"\\s\"(.*)\"\\s\"(.*)\"\\s*$";
    public static final Pattern LOG_PATTERN = Pattern.compile(LOG_PATTERN_STR);
    public static final String STATUS_200 = "200";
    public static final String STATUS_OTHER = "其它";

    @Autowired
    private Environment env;

    @Autowired
    NginxAccessLogRepository nginxAccessLogRepository;

    @Override
    public PageResult<List<NginxAccessFileLine>> listLines(Date gmtBegin, Date gmtEnd, int pageIndex, int pageSize) {
        return nginxAccessLogRepository.list(gmtBegin, gmtEnd, pageIndex, pageSize);
    }

    @Override
    public void readAndStoreLines() {
        String logPath = env.getProperty("host.nginx.log.path");
        AssertUtil.notBlank(logPath, CONFIGURATION_EXCEPTION, "failed to find log path");

        String path = logPath + "/access.log";
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

            for (NginxAccessFileLine line : lines) {
                try {
                    nginxAccessLogRepository.insert(line);

                } catch (Exception e) {
                    if (e instanceof DataIntegrityViolationException) {
                        LOGGER.error("insert exception of DataIntegrityViolationException: {}, line: {}",
                            e.getMessage(), line);
                    } else {
                        LOGGER.error("insert exception of {}, line: {}", e.getMessage(), line);
                        throw e;
                    }
                }
            }
        }
    }

    @Override
    public NginxAccessLogStatusCount countStatus() {
        NginxAccessLogStatusCount result = new NginxAccessLogStatusCount();

        Date today = new Date();
        Date startOfToday = DateUtil.getStartOfDate(today);
        Date endOfToday = DateUtil.getEndOfDate(today);

        List<NginxAccessLogStatusCountDO> todayCounts = nginxAccessLogRepository.countStatus(startOfToday, endOfToday);
        parseCount(todayCounts, result.getToday());

        Date yesterday = DateUtil.addDays(today, -1);
        Date startOfYesterday = DateUtil.getStartOfDate(yesterday);
        Date endOfYesterday = DateUtil.getEndOfDate(yesterday);

        List<NginxAccessLogStatusCountDO> yesterdayCount = nginxAccessLogRepository.countStatus(startOfYesterday,
            endOfYesterday);
        parseCount(yesterdayCount, result.getYesterday());

        return result;
    }

    @Override
    public List<NginxAccessLogPointModel> listPoints(Date gmtBegin, Date gmtEnd) {
        if (null == gmtBegin || null == gmtEnd || dayDiff(gmtBegin, gmtEnd) > 3) {
            throw new DemoException(INVALID_PARAM_EXCEPTION, "入参错误");
        }

        // 拉取全量
        List<NginxAccessLogPointDO> pointsOfAll = nginxAccessLogRepository.listPoints(gmtBegin, gmtEnd, null);
        Map<String, Integer> pointsOfAllMap = Optional.ofNullable(pointsOfAll).orElse(Lists.newArrayList()).stream()
            .collect(
                Collectors.toMap(NginxAccessLogPointDO::getTime, NginxAccessLogPointDO::getCount, (v1, v2) -> v2));

        // 拉取状态为200的
        List<NginxAccessLogPointDO> pointsOf200 = nginxAccessLogRepository.listPoints(gmtBegin, gmtEnd, "200");
        Map<String, Integer> pointsOf200Map = Optional.ofNullable(pointsOf200).orElse(Lists.newArrayList()).stream()
            .collect(
                Collectors.toMap(NginxAccessLogPointDO::getTime, NginxAccessLogPointDO::getCount, (v1, v2) -> v2));

        // 组装
        Date date = gmtBegin;
        List<NginxAccessLogPointModel> result = new ArrayList<>();
        while (date.compareTo(gmtEnd) <= 0) {
            String dateStr = DateUtil.format(date, "yyyy-MM-dd HH");
            Integer all = pointsOfAllMap.get(dateStr);
            Integer s200 = pointsOf200Map.get(dateStr);

            if (null == all && null == s200) {
                result.add(buildModel(dateStr, STATUS_200, 0));
                result.add(buildModel(dateStr, STATUS_OTHER, 0));
            } else if (null != all && null == s200) {
                result.add(buildModel(dateStr, STATUS_200, 0));
                result.add(buildModel(dateStr, STATUS_OTHER, all));
            } else if (null != all && null != s200) {
                result.add(buildModel(dateStr, STATUS_200, s200));
                result.add(buildModel(dateStr, STATUS_OTHER, all - s200));
            } else {
                throw new DemoException(UNEXPECTED, "非预期逻辑");
            }

            date = addHours(date, 1);
        }

        return result;
    }

    private NginxAccessLogPointModel buildModel(String dateStr, String status, int count) {
        NginxAccessLogPointModel model = new NginxAccessLogPointModel();
        model.setTime(dateStr);
        model.setStatus(status);
        model.setCount(count);
        return model;
    }

    private void parseCount(List<NginxAccessLogStatusCountDO> countDos, NginxAccessLogStatusCountModel model) {
        if (CollectionUtils.isEmpty(countDos)) {
            return;
        }
        long countOf200 = 0;
        long countOf3xx = 0;
        long countOf4xx = 0;
        long countOf5xx = 0;
        long countOfOthers = 0;
        for (NginxAccessLogStatusCountDO countDo : countDos) {
            if ("200".equals(countDo.getStatus())) {
                countOf200 = countDo.getCount();
            } else if (countDo.getStatus().startsWith("3")) {
                countOf3xx += countDo.getCount();
            } else if (countDo.getStatus().startsWith("4")) {
                countOf4xx += countDo.getCount();
            } else if (countDo.getStatus().startsWith("5")) {
                countOf5xx += countDo.getCount();
            } else {
                countOfOthers += countDo.getCount();
            }
        }

        model.setCountOf200(countOf200);
        model.setCountOf3xx(countOf3xx);
        model.setCountOf4xx(countOf4xx);
        model.setCountOf5xx(countOf5xx);
        model.setCountOfOthers(countOfOthers);
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
