package com.dongwoo.SQM.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Data
public class DataSetDTO {
    LinkedHashMap<String, Object> columns = null;

    int records = 0;

    ArrayList<LinkedHashMap<Object, Object>> rows = null;

    @JsonProperty("Columns")
    public LinkedHashMap<String, Object> getColumns() {
        return columns;
    }

    public void setColumns(LinkedHashMap<String, Object> columns) {
        this.columns = columns;
    }

    @JsonProperty("Records")
    public int getRecords() {
        return records;
    }

    public void setRecords(int records) {
        this.records = records;
    }

    @JsonProperty("Rows")
    public ArrayList<LinkedHashMap<Object, Object>> getRows() {
        return rows;
    }

    public void setRows(ArrayList<LinkedHashMap<Object, Object>> rows) {
        this.rows = rows;
    }
}
