package com.hy.project.demo.model.docker;

import java.util.Map;

import com.hy.project.demo.model.ToString;

/**
 * @author rick.wl
 * @date 2021/08/28
 */
public class ContainerBase extends ToString {

    private static final long serialVersionUID = -3175151640318133878L;

    private String id;

    private String name;

    private String imageName;

    private String imageId;

    private String gmtCreate;

    private String command;

    private Map<String, String> status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Map<String, String> getStatus() {
        return status;
    }

    public void setStatus(Map<String, String> status) {
        this.status = status;
    }
}


