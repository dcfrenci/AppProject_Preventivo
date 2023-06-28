package com.preventivoapp.appproject_preventivo.classes;

public class Setting {
    private final String path;
    private final String pathSettingJson;
    private String pathQuote;

    public Setting(String path) {
        this.path = path;
        this.pathSettingJson = path + "\\setting";
        this.pathQuote = "";
    }

    public String getPath() {
        return path;
    }

    public String getPathSetting() {
        return pathSettingJson;
    }

    public String getPathQuote() {
        return pathQuote;
    }

    public void setPathQuote(String pathQuote) {
        this.pathQuote = pathQuote;
    }
}
