package com.jadenyangca.exercise.component;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Jaden
 * @create 2018-09-24
 */
@Component
@ConfigurationProperties(prefix = "appconfig")
public class AppConfig {
    private String filePath;
    private String fileMaxSize;

    public String getFileMaxSize() {
        return fileMaxSize;
    }

    public void setFileMaxSize(String fileMaxSize) {
        this.fileMaxSize = fileMaxSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
