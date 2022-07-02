package com.foundation.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentMessage {

    private String schoolArea;

    private String classDate;

    private String week;

    private String classTime;

    private String classMinute;

    private String teacherName;

    private String teacherType;

    private String teacherArea;

    private String liveTeacher;

    private String grade;

    private String className;

    private String classRoom;

    private String name;

    private String arrangement;

    private String courageType;

    private String projectType;

    private String pageId;

    private String price;

    private String canToStudy;

    private String planToStudy;

    private String actualStudy;

    private String totalPrice;

    private String status;

    private String label;

    private String note;

    public static StudentMessage createStudentMess(List<String> dataStrList, Map<String, Integer> rowNameMap) throws IllegalAccessException {
        StudentMessage studentMessage = new StudentMessage();
        for (String rowName : rowNameMap.keySet()) {
            Integer index;
            switch (rowName) {
                case "校区":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setSchoolArea(dataStrList.get(index));
                    }
                    break;
                case "上课日期":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setClassDate(dataStrList.get(index));
                    }
                    break;
                case "星期":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setWeek(dataStrList.get(index));
                    }
                    break;
                case "上课时间":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setClassTime(dataStrList.get(index));
                    }
                    break;
                case "课时时长（分）":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setClassMinute(dataStrList.get(index));
                    }
                    break;
                case "老师":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setTeacherName(dataStrList.get(index));
                    }
                    break;
                case "老师类型":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setTeacherType(dataStrList.get(index));
                    }
                    break;
                case "老师校区":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setTeacherArea(dataStrList.get(index));
                    }
                    break;
                case "学管师":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setLiveTeacher(dataStrList.get(index));
                    }
                    break;
                case "年级":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setGrade(dataStrList.get(index));
                    }
                    break;
                case "科目":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setClassName(dataStrList.get(index));
                    }
                    break;
                case "教室":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setClassRoom(dataStrList.get(index));
                    }
                    break;
                case "学员":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setName(dataStrList.get(index));
                    }
                    break;
                case "排课课程":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setArrangement(dataStrList.get(index));
                    }
                    break;
                case "课程分类":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setCourageType(dataStrList.get(index));
                    }
                    break;
                case "项目分类":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setProjectType(dataStrList.get(index));
                    }
                    break;
                case "纸质编号":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setPageId(dataStrList.get(index));
                    }
                    break;
                case "单价":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setPrice(dataStrList.get(index));
                    }
                    break;
                case "剩余课时":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setCanToStudy(dataStrList.get(index));
                    }
                    break;
                case "计划课时":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setPlanToStudy(dataStrList.get(index));
                    }
                    break;
                case "实际课时":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setActualStudy(dataStrList.get(index));
                    }
                    break;
                case "金额":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setTotalPrice(dataStrList.get(index));
                    }
                    break;
                case "状态":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setStatus(dataStrList.get(index));
                    }
                    break;
                case "标签":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setLabel(dataStrList.get(index));
                    }
                    break;
                case "备注":
                    index = rowNameMap.get(rowName);
                    if (index != null) {
                        studentMessage.setNote(dataStrList.get(index));
                    }
                    break;
                default:
                    break;
            }

        }
        return studentMessage;
    }

}
