package com.quick.common.utils.flie.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Word 模板参数替换入参 表格对象
 *
 * @author Liujinxin
 */
public class TableAttr {

    /**
     * 表格头字段,该数据长度将决定表格列数
     */
    private final List<String> headRowNameList;

    /**
     * 表格主体内容
     */
    private final List<String[]> rowList;

    /**
     * 初始化对象
     *
     * @param headRowName 表格头字段
     */
    public TableAttr(String... headRowName) {
        this.headRowNameList = Arrays.asList(headRowName);
        this.rowList = new ArrayList<>();
    }

    /**
     * 设置指定行内容
     *
     * @param cell 当前行列内容
     */
    public void addRow(String... cell) {
        if (cell.length > headRowNameList.size()) {
            throw new ArrayIndexOutOfBoundsException(headRowNameList.size());
        }
        rowList.add(cell);
    }

    public List<String> getHeadRowNameList() {
        return headRowNameList;
    }

    public List<String[]> getRowList() {
        return rowList;
    }
}
