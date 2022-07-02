package com.foundation.api.service.impl;

import com.foundation.api.domain.StudentMessage;
import com.foundation.api.service.IATestToolService;
import com.foundation.api.utils.DateUtils;
import com.foundation.api.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

@Slf4j
@Service
public class ATestToolServiceImpl implements IATestToolService {

    /**
     * 生成周报数据
     *
     * @param file
     * @throws Exception
     */
    @Override
    public String generateWeeklyPublication(MultipartFile file) throws Exception {
        StringBuilder stringBuilder = new StringBuilder("");
//        File file = new File("D:/文档/1对1考勤列表_20200221201433.xlsx");
        List<List<String>> lists = ExcelUtils.importExcel(file, -1, -1, 25);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<StudentMessage> studentMessages = new ArrayList<>();
        Map<String, Integer> rowsNameMap = new HashMap<>();
        for (int i = 0; i < lists.size(); i++) {
            if (i == 0) {
                List<String> rowNames = lists.get(i);
                for ( int k = 0 ; k < rowNames.size() ; k++) {
                    if (StringUtils.isNotBlank(rowNames.get(k))) {
                        rowsNameMap.put(rowNames.get(k), k);
                    }
                }
                continue;
            }
            if (StringUtils.isBlank(lists.get(i).get(0))) {
                continue;
            }
            StudentMessage studentMess = StudentMessage.createStudentMess(lists.get(i), rowsNameMap);
            studentMessages.add(studentMess);
//            System.out.println(JSONObject.toJSONString(studentMess));
        }

        //按照week区分数据
        Map<String, List<StudentMessage>> weekOfYearMap = new HashMap<>();
        for (StudentMessage sm : studentMessages) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(simpleDateFormat.parse(sm.getClassDate()));
            calendar.add(Calendar.DATE, -1);
            String fromToDate = DateUtils.getFromToDate(simpleDateFormat, calendar.getTime(), 1, 0, 0);
            String endToDate = DateUtils.getFromToDate(simpleDateFormat, calendar.getTime(), 1, 1, 0);
//            String targetDate = simpleDateFormat.format(calendar.getTime());
//            System.out.println(fromToDate + "/" + endToDate + "/" + targetDate);
            String timeDuring = fromToDate + "-" + endToDate;

//            int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
            List<StudentMessage> studentMessagesPart1 = weekOfYearMap.get(timeDuring);
            if (studentMessagesPart1 == null) {
                studentMessagesPart1 = new ArrayList<>();
            }
            studentMessagesPart1.add(sm);
            weekOfYearMap.put(timeDuring, studentMessagesPart1);
        }

        //遍历week-Map
        for (String key : weekOfYearMap.keySet()) {
            List<StudentMessage> studentMessages1 = weekOfYearMap.get(key);

            //按照年级区分数据
            Map<String, List<StudentMessage>> gradeMap = new HashMap<>();
            for (StudentMessage sm : studentMessages1) {
                List<StudentMessage> studentMessages2 = gradeMap.get(sm.getGrade());
                if (studentMessages2 == null) {
                    studentMessages2 = new ArrayList<>();
                }
                studentMessages2.add(sm);
                gradeMap.put(sm.getGrade(), studentMessages2);
            }

            BigDecimal allTotalActualStudy = BigDecimal.valueOf(0.0d);
            BigDecimal allTotalPlanStudy = BigDecimal.valueOf(0.0d);
            BigDecimal allTotalActualStudyWorkDayStudy = BigDecimal.valueOf(0.0d);
            BigDecimal allTotalPlanStudyStudyWorkDayStudy = BigDecimal.valueOf(0.0d);

            BigDecimal allTotalActualStudyWeekendDayStudy = BigDecimal.valueOf(0.0d);
            BigDecimal allTotalPlanStudyWeekendDayStudy = BigDecimal.valueOf(0.0d);

            int allStudentCount = 0;

            //遍历年级-Map
            for (String gradeKey : gradeMap.keySet()) {
                List<StudentMessage> studentMessagesPart2 = gradeMap.get(gradeKey);

                BigDecimal totalActualStudy = BigDecimal.valueOf(0.0d);
                BigDecimal totalPlanStudy = BigDecimal.valueOf(0.0d);

                BigDecimal totalActualStudyWorkDayStudy = BigDecimal.valueOf(0.0d);
                BigDecimal totalPlanStudyStudyWorkDayStudy = BigDecimal.valueOf(0.0d);

                BigDecimal totalActualStudyWeekendDayStudy = BigDecimal.valueOf(0.0d);
                BigDecimal totalPlanStudyWeekendDayStudy = BigDecimal.valueOf(0.0d);

                Set<String> studentNameSet = new HashSet<>();

                for (StudentMessage studentMessage : studentMessagesPart2) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(simpleDateFormat.parse(studentMessage.getClassDate()));
                    studentNameSet.add(studentMessage.getName());

                    BigDecimal actualStudy = BigDecimal.valueOf(Double.valueOf(StringUtils.isBlank(studentMessage.getActualStudy()) ? "0" : studentMessage.getActualStudy()));
                    BigDecimal planStudy = BigDecimal.valueOf(Double.valueOf(StringUtils.isBlank(studentMessage.getPlanToStudy()) ? "0" : studentMessage.getPlanToStudy()));

                    totalActualStudy = totalActualStudy.add(actualStudy);
                    totalPlanStudy = totalPlanStudy.add(planStudy);

                    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                        totalActualStudyWeekendDayStudy = totalActualStudyWeekendDayStudy.add(actualStudy);
                        totalPlanStudyWeekendDayStudy = totalPlanStudyWeekendDayStudy.add(planStudy);
                    } else {
                        totalActualStudyWorkDayStudy = totalActualStudyWorkDayStudy.add(actualStudy);
                        totalPlanStudyStudyWorkDayStudy = totalPlanStudyStudyWorkDayStudy.add(planStudy);
                    }
                }

                allTotalActualStudy = allTotalActualStudy.add(totalActualStudy);
                allTotalPlanStudy = allTotalPlanStudy.add(totalPlanStudy);
                allTotalActualStudyWorkDayStudy = allTotalActualStudyWorkDayStudy.add(totalActualStudyWorkDayStudy);
                allTotalPlanStudyStudyWorkDayStudy = allTotalPlanStudyStudyWorkDayStudy.add(totalPlanStudyStudyWorkDayStudy);
                allTotalActualStudyWeekendDayStudy = allTotalActualStudyWeekendDayStudy.add(totalActualStudyWeekendDayStudy);
                allTotalPlanStudyWeekendDayStudy = allTotalPlanStudyWeekendDayStudy.add(totalPlanStudyWeekendDayStudy);

                BigDecimal totalActualStudyKeHaoXiShu = BigDecimal.ZERO;
                if (!BigDecimal.ZERO.equals(totalActualStudy)) {
                    BigDecimal totalActualStudyBigDecimal = BigDecimal.valueOf(totalActualStudy.doubleValue());
                    totalActualStudyKeHaoXiShu = (totalActualStudyBigDecimal.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)).divide(BigDecimal.valueOf(studentNameSet.size()), 4, ROUND_HALF_DOWN);
                }

                BigDecimal totalPlanStudyKeHaoXiShu = BigDecimal.ZERO;
                if (!BigDecimal.ZERO.equals(totalPlanStudy)) {
                    BigDecimal totalPlanStudyBigDecimal = BigDecimal.valueOf(totalPlanStudy.doubleValue());
                    totalPlanStudyKeHaoXiShu = (totalPlanStudyBigDecimal.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)).divide(BigDecimal.valueOf(studentNameSet.size()), 4, ROUND_HALF_DOWN);
                }

                allStudentCount = allStudentCount + studentNameSet.size();

                stringBuilder.append(" 班级 : ").append(gradeKey).append(", 学生人数 : ").append(studentNameSet.size()).append("<br/>");
                stringBuilder.append("总实际:").append(totalActualStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN))
                        .append(", 工作日-实际: ").append(totalActualStudyWorkDayStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN))
                        .append(", 周末-实际: ").append(totalActualStudyWeekendDayStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN))
                        .append(", 课耗系数-总实际:").append(totalActualStudyKeHaoXiShu).append("<br/>");

//                System.out.println(
////                        key +
//                        " 班级 : " + gradeKey + ", 学生人数 : " + studentNameSet.size());
//                System.out.println("总实际:" + totalActualStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)
////                        + ", 总预计: " + totalPlanStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)
//                        + ", 工作日-实际: " + totalActualStudyWorkDayStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)
////                        + ", 工作日-预计: " + totalPlanStudyStudyWorkDayStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)
//                        + ", 周末-实际: " + totalActualStudyWeekendDayStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)
////                        + ", 周末-预计: " + totalPlanStudyWeekendDayStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)
//                        + ", 课耗系数-总实际:" + totalActualStudyKeHaoXiShu);
////                        + ", 课耗系数-总预计:" + totalPlanStudyKeHaoXiShu);
            }

            BigDecimal allTotalActualStudyKeHaoXiShu = BigDecimal.ZERO;
            if (!BigDecimal.ZERO.equals(allTotalActualStudy)) {
                BigDecimal totalActualStudyBigDecimal = BigDecimal.valueOf(allTotalActualStudy.doubleValue());
                allTotalActualStudyKeHaoXiShu = (totalActualStudyBigDecimal.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)).divide(BigDecimal.valueOf(allStudentCount), 4, ROUND_HALF_DOWN);
            }

            BigDecimal allTotalPlanStudyKeHaoXiShu = BigDecimal.ZERO;
            if (!BigDecimal.ZERO.equals(allTotalPlanStudy)) {
                BigDecimal totalPlanStudyBigDecimal = BigDecimal.valueOf(allTotalPlanStudy.doubleValue());
                allTotalPlanStudyKeHaoXiShu = (totalPlanStudyBigDecimal.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)).divide(BigDecimal.valueOf(allStudentCount), 4, ROUND_HALF_DOWN);
            }

            stringBuilder.append("------------------------------------------汇总------------------------------------------").append("<br/>");
            stringBuilder.append("学生人数: ").append(allStudentCount).append("<br/>");
            stringBuilder.append("总实际:").append(allTotalActualStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN))
                    .append(", 工作日-实际: ").append(allTotalActualStudyWorkDayStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN))
                    .append(", 周末-实际: ").append(allTotalActualStudyWeekendDayStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN))
                    .append(", 课耗系数-总实际:").append(allTotalActualStudyKeHaoXiShu);
//            System.out.println("------------------------------------------汇总------------------------------------------");
//            System.out.println("学生人数: " + allStudentCount);
//            System.out.println("总实际:" + allTotalActualStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)
////                    + ", 总预计: " + allTotalPlanStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)
//                    + ", 工作日-实际: " + allTotalActualStudyWorkDayStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)
////                    + ", 工作日-预计: " + allTotalPlanStudyStudyWorkDayStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)
//                    + ", 周末-实际: " + allTotalActualStudyWeekendDayStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)
////                    + ", 周末-预计: " + allTotalPlanStudyWeekendDayStudy.divide(BigDecimal.valueOf(1.5d), 4, ROUND_HALF_DOWN)
//                    + ", 课耗系数-总实际:" + allTotalActualStudyKeHaoXiShu);
////                    + ", 课耗系数-总预计:" + allTotalPlanStudyKeHaoXiShu);
        }
//        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }


    public static void main(String[] args) throws Exception {
        ATestToolServiceImpl.test1();
    }

    public static void test1() throws Exception {
        StringBuilder stringBuilder = new StringBuilder("");
        File file = new File("D:/微信备份/WeChat Files/xj18825080524/FileStorage/File/2020-09/LC021重新阅片-黄健垣.xlsx");
        List<List<String>> lists = ExcelUtils.importExcel(file, -1, -1, 40);
        // 处理title
        Map<String, Map<String, String>> dataMap1 = getData(lists, 0,3, 260);
        Map<String, Map<String, String>> dataMap2 = getData(lists, 260, 3, 520);

        List<String> firstNoExist = new ArrayList<>();
        List<String> secondNoExist = new ArrayList<>();

        Set<String> row1 = dataMap1.keySet();
        int num = 0;
        for (String key : row1) {
            Map<String, String> rowData1 = dataMap1.get(key);
            Map<String, String> rowData2 = dataMap2.get(key);

            if (rowData2 == null) {
                secondNoExist.add(key);
                continue;
            }
            StringBuilder messBuilder = new StringBuilder();
            messBuilder.append("编号: ").append(key).append("\n");
            boolean isNotMath = false;
            // 进行比对
            Set<String> dataKeys = rowData1.keySet();
            for (String dataKey : dataKeys) {
                String value1 = rowData1.get(dataKey);
                String value2 = rowData2.get(dataKey);

                if (StringUtils.isBlank(value1) && StringUtils.isBlank(value2)) {
                    continue;
                }

                if (StringUtils.isBlank(value1) && StringUtils.equals("无", value2)) {
                    continue;
                }

                if (StringUtils.isBlank(value2) && StringUtils.equals("无", value1)) {
                    continue;
                }

                if (!StringUtils.equals(value2,value1)) {
                    isNotMath = true;
                    messBuilder.append(dataKey).append(" 不一样").append("\n").append("第一份:").append(value1).append("\n").append("第二份:").append(value2).append("\n").append("\n");
                }
            }
            if (isNotMath) {
                System.out.println(messBuilder.toString());
                System.out.println("------------------------------------------------------");
            }
            dataMap2.remove(key);
            num++;
        }

        if (!dataMap2.keySet().isEmpty()) {
            firstNoExist.addAll(new ArrayList<>(dataMap2.keySet()));
        }

        System.out.println("匹配命中：" + num);
        System.out.println("第一份存在, 第二份不存在数量" + secondNoExist.size());
        System.out.println("第一份不存在, 第二份存在数量" + firstNoExist.size());

        System.out.println("\n--------------不存在--------------\n");
        System.out.println("第一份不存在: " + StringUtils.join(firstNoExist, ","));
        System.out.println("第二份不存在: " + StringUtils.join(secondNoExist, ","));

    }

    public static Map<String, Map<String, String>> getData (List<List<String>> lists, Integer startRowNum, Integer titleRowNum, Integer dataRowNum) {
        List<String> titleRows = lists.get(startRowNum);
        for (int i = startRowNum ; i < startRowNum + titleRowNum; i++) {
            if (i == startRowNum) {
                for (int j = 0 ; j < titleRows.size() ; j++) {
                    String s = titleRows.get(j);
                    if (StringUtils.isBlank(s)) {
                        titleRows.set(j, titleRows.get(j-1));
                    }
                }
            } else {
                List<String> rows2 = lists.get(i);
                for (int i1 = 0; i1 < rows2.size(); i1++) {
                    String subTitle = rows2.get(i1);
                    if (StringUtils.isNotBlank(subTitle)) {
                        titleRows.set(i1,titleRows.get(i1) + "-" + subTitle);
                    }
                }
            }
        }
        Map<String, Map<String, String>> compareDataMap = new LinkedHashMap<>();
        for (int i = startRowNum + titleRowNum ; i < dataRowNum; i++) {
            List<String> datas = lists.get(i);
            String key = datas.get(0);
//            key = key.replaceAll("-","");
            Map<String, String> dataMap = new LinkedHashMap<>();
            for (int i1 = 1; i1 < datas.size(); i1++) {
                dataMap.put(titleRows.get(i1).replaceAll("（", "(").replaceAll("）",")"), datas.get(i1));
            }
            compareDataMap.put(key, dataMap);
        }
        return compareDataMap;
    }
}