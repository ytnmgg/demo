package com.hy.project.demo.monitor.core.mybatis.entity;

import java.util.Date;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
public class NginxAccessLogDO {

    private Date gmtCreate;

    private Date gmtModified;

    private Long id;

    private String remoteAddress;

    private Date timeLocal;

    private String request;

    private String status;

    private Long bodyBytes;

    private String httpReferer;

    private String httpUserAgent;

    private String fileMarker;

    private Long lineNumber;

    private String lineContent;

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public Date getTimeLocal() {
        return timeLocal;
    }

    public void setTimeLocal(Date timeLocal) {
        this.timeLocal = timeLocal;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getBodyBytes() {
        return bodyBytes;
    }

    public void setBodyBytes(Long bodyBytes) {
        this.bodyBytes = bodyBytes;
    }

    public String getHttpReferer() {
        return httpReferer;
    }

    public void setHttpReferer(String httpReferer) {
        this.httpReferer = httpReferer;
    }

    public String getHttpUserAgent() {
        return httpUserAgent;
    }

    public void setHttpUserAgent(String httpUserAgent) {
        this.httpUserAgent = httpUserAgent;
    }

    public String getFileMarker() {
        return fileMarker;
    }

    public void setFileMarker(String fileMarker) {
        this.fileMarker = fileMarker;
    }

    public Long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Long lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getLineContent() {
        return lineContent;
    }

    public void setLineContent(String lineContent) {
        this.lineContent = lineContent;
    }
}
