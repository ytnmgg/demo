package com.hy.project.demo.model.nginx;

import com.hy.project.demo.model.ToString;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
public class NginxAccessLogStatusCountModel extends ToString {

    private static final long serialVersionUID = 2630516143139827414L;

    private long countOf200;

    private long countOf3xx;

    private long countOf4xx;

    private long countOf5xx;

    private long countOfOthers;

    public long getCountOf200() {
        return countOf200;
    }

    public void setCountOf200(long countOf200) {
        this.countOf200 = countOf200;
    }

    public long getCountOf3xx() {
        return countOf3xx;
    }

    public void setCountOf3xx(long countOf3xx) {
        this.countOf3xx = countOf3xx;
    }

    public long getCountOf4xx() {
        return countOf4xx;
    }

    public void setCountOf4xx(long countOf4xx) {
        this.countOf4xx = countOf4xx;
    }

    public long getCountOf5xx() {
        return countOf5xx;
    }

    public void setCountOf5xx(long countOf5xx) {
        this.countOf5xx = countOf5xx;
    }

    public long getCountOfOthers() {
        return countOfOthers;
    }

    public void setCountOfOthers(long countOfOthers) {
        this.countOfOthers = countOfOthers;
    }
}
