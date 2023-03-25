package com.hy.project.demo.monitor.facade.model.file;

import com.hy.project.demo.common.model.ToString;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
public class FileLine extends ToString {

    private static final long serialVersionUID = -6080614964825294683L;

    private String lineContent;

    private long lineNumber;

    public String getLineContent() {
        return lineContent;
    }

    public void setLineContent(String lineContent) {
        this.lineContent = lineContent;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }
}
