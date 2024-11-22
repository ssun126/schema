package com.dongwoo.SQM.common.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CommonDTO {
    private String storedQueryId = "";
    private String storedQueryVersion = "";
    private String storedQueryClassId = "";
    private String storedQueryName = "";
    private String queryType = "";
    private String queryString = "";
    private String activity = "";
    private String prevActivity = "";
    private String customActivity = "";
    private String prevCustomActivity = "";
    private String isUsable = "";
    private String siteId = "";
    private String description = "";
    private String reasonCode = "";
    private String comments = "";
    private String creator = "";
    private Date createTime = null;
    private String modifier = "";
    private Date modifyTime = null;
    private Date lastEventTime = null;
    private String tId = "";
}
