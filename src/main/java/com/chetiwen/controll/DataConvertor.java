package com.chetiwen.controll;

import com.chetiwen.object.antqueen.*;
import com.chetiwen.object.qucent.QucentOrderRecord;
import com.chetiwen.object.qucent.QucentOrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataConvertor {
    private static Logger logger = LoggerFactory.getLogger(DataConvertor.class);

    public static OrderReportResponse convertToAntQueenReport(QucentOrderResponse qucentOrder) throws ParseException {
        logger.info("Start to convert qucentOrder: {}", qucentOrder);
        OrderReportResponse antQueenReport = new OrderReportResponse();

        if (qucentOrder.getCode() == 0) {
            antQueenReport.setCode(qucentOrder.getCode());
            antQueenReport.setMsg(qucentOrder.getMsg());
        } else {
            antQueenReport.setCode(1107);
            antQueenReport.setMsg("服务异常");
        }

        OrderReportData orderReportData = new OrderReportData();
        antQueenReport.setData(orderReportData);

        orderReportData.setMakeReportDate((int) (System.currentTimeMillis()/1000));
        orderReportData.setBrandName(qucentOrder.getData().getBasic().getBrand());
        orderReportData.setReportNo(qucentOrder.getUserOrderId());
        orderReportData.setCarAge(qucentOrder.getData().getBasic().getYear());
        orderReportData.setLastMainTime(qucentOrder.getData().getResume().getLastdate());
        orderReportData.setVin(qucentOrder.getData().getBasic().getVin());
        orderReportData.setReportUrl(null);
        setMileageAndRepairRecords(qucentOrder, orderReportData);
        setReportDataState(qucentOrder, orderReportData);
        setPartDetail(orderReportData);
        setMileageEveryYearAndCarAge(orderReportData);

        logger.info("Converted antQueenReport : {}", antQueenReport);
        return antQueenReport;
    }

    private static void setMileageEveryYearAndCarAge(OrderReportData orderReportData) throws ParseException {
        if (orderReportData.getMileage_data() != null) {
            int maxMileage = Integer.valueOf(orderReportData.getMileage_data().get(0).getMileage());//定义最大值为该数组的第一个数
            int minMileage = Integer.valueOf(orderReportData.getMileage_data().get(0).getMileage());//定义最小值为该数组的第一个数

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date maxDate = simpleDateFormat.parse(orderReportData.getMileage_data().get(0).getDate());
            Date minDate = simpleDateFormat.parse(orderReportData.getMileage_data().get(0).getDate());

            for (OrderReportMileage mileage : orderReportData.getMileage_data()) {
                if (maxMileage < Integer.valueOf(mileage.getMileage())) {
                    maxMileage = Integer.valueOf(mileage.getMileage()).intValue();
                    maxDate = simpleDateFormat.parse(mileage.getDate());
                }
                if (minMileage > Integer.valueOf(mileage.getMileage())) {
                    minMileage = Integer.valueOf(mileage.getMileage()).intValue();
                    minDate = simpleDateFormat.parse(mileage.getDate());
                }
            }
            int daysDiff = (int) ((maxDate.getTime() - minDate.getTime()) / (1000 * 3600 * 24));
            int milsDiff = maxMileage - minMileage;
            if (daysDiff != 0) {
                orderReportData.setMileageEveryYear(String.valueOf(milsDiff / daysDiff * 365));
            } else {
                orderReportData.setMileageEveryYear(String.valueOf(maxMileage));
            }
            float carAge = (System.currentTimeMillis() - minDate.getTime()) / (1000 * 3600 * 24 * 365f) + 0.3f;//第一次保养预计为0.3年后
            BigDecimal b = new BigDecimal(carAge);
            carAge = b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
            orderReportData.setCarAge(String.valueOf(carAge));
        }
        logger.info("converted MileageEveryYear: {}", orderReportData.getMileageEveryYear());
        logger.info("converted CarAge : {}", orderReportData.getCarAge());
    }

    private static void setPartDetail(OrderReportData orderReportData) {
        OrderReportPartDetail partDetail = new OrderReportPartDetail();
        partDetail.setBody_part(new ArrayList<>());
        partDetail.setExterior(new ArrayList<>());
        partDetail.setFire(new ArrayList<>());
        partDetail.setImportant(new ArrayList<>());
        partDetail.setRecall(new ArrayList<>());
        partDetail.setScrapped(new ArrayList<>());
        partDetail.setWater(new ArrayList<>());

        orderReportData.setPart_detail(partDetail);
        logger.info("converted part_detail: {}", orderReportData.getPart_detail());
    }

    private static void setMileageAndRepairRecords(QucentOrderResponse qucentOrder, OrderReportData orderReportData) {
        if (qucentOrder.getData().getMc() != null) {
            List<OrderReportMileage> mileages = new ArrayList<>();
            List<OrderReportRepairDetail> reportRepairDetails = new ArrayList<>();
            for (QucentOrderRecord qucentOrderRecord : qucentOrder.getData().getMc()) {
                OrderReportMileage mileage = new OrderReportMileage();
                mileage.setMileage(qucentOrderRecord.getMn());
                mileage.setDate(qucentOrderRecord.getRd());
                mileage.setStatus(1-qucentOrderRecord.getIe());
                mileages.add(mileage);

                OrderReportRepairDetail repairDetail = new OrderReportRepairDetail();
                repairDetail.setOther(qucentOrderRecord.getRemark());
                repairDetail.setContent(qucentOrderRecord.getPt());
                repairDetail.setDate(qucentOrderRecord.getRd());
                repairDetail.setMaterial(qucentOrderRecord.getMaterial());
                repairDetail.setMileage(Integer.valueOf(qucentOrderRecord.getMn()));
                repairDetail.setType(qucentOrderRecord.getType());
                reportRepairDetails.add(repairDetail);
            }
            orderReportData.setMileage_data(mileages);
            orderReportData.setNormalRepairRecords(reportRepairDetails);
        }
        logger.info("converted mileage_data: {}", orderReportData.getMileage_data());
        logger.info("converted repair details: {}", orderReportData.getNormalRepairRecords());
    }

    private static void setReportDataState(QucentOrderResponse qucentOrder, OrderReportData orderReportData) {
        OrderReportState orderReportState = new OrderReportState();
        //车身部件
        if ("0".equals(qucentOrder.getData().getResume().getSc())) {
            orderReportState.setBodyPartState(1);
        } else {
            orderReportState.setBodyPartState(0);
        }
        //外观
        if ("0".equals(qucentOrder.getData().getResume().getWgj())) {
            orderReportState.setExteriorState(1);
        } else {
            orderReportState.setExteriorState(0);
        }
        //火烧
        if ("0".equals(qucentOrder.getData().getResume().getFr())) {
            orderReportState.setFireState(1);
        } else {
            orderReportState.setFireState(0);
        }
        //重要部件 (发动机和变速箱）
        if ("0".equals(qucentOrder.getData().getResume().getEn()) && "0".equals(qucentOrder.getData().getResume().getTc())) {
            orderReportState.setImportantState(1);
        } else {
            orderReportState.setImportantState(0);
        }
        //召回
        orderReportState.setRecallState(1);

        //泡水
        if ("0".equals(qucentOrder.getData().getResume().getBw())) {
            orderReportState.setWaterState(1);
        } else {
            orderReportState.setWaterState(0);
        }

        //报废
        if ("0".equals(qucentOrder.getData().getResume().getMa())) {
            orderReportState.setScrappedState(1);
        } else {
            orderReportState.setScrappedState(0);
        }

        orderReportData.setState(orderReportState);
        logger.info("converted data state : {}", orderReportData.getState());
    }

    public static AntOrderResponse convertToAntQueenOrder(QucentOrderResponse qucentOrder) {
        AntOrderResponse antQueenOrder = new AntOrderResponse();

        if (qucentOrder.getCode() == 0) {
            antQueenOrder.setCode(qucentOrder.getCode());
            antQueenOrder.setMsg(qucentOrder.getMsg());
        } else {
            antQueenOrder.setCode(1107);
            antQueenOrder.setMsg("服务异常");
        }

        AntOrderData antOrderData = new AntOrderData();
        antQueenOrder.setData(antOrderData);

        antOrderData.setOrderId(qucentOrder.getUserOrderId());

        if (qucentOrder.getData().getMc() != null) {
            List<AntOrderResult> antOrderResults = new ArrayList<>();
            for (QucentOrderRecord qucentOrderRecord : qucentOrder.getData().getMc()) {
                AntOrderResult antOrderResult = new AntOrderResult();
                antOrderResult.setOther(qucentOrderRecord.getRemark());
                antOrderResult.setContent(qucentOrderRecord.getPt());
                antOrderResult.setDate(qucentOrderRecord.getRd());
                antOrderResult.setMaterial(qucentOrderRecord.getMaterial());
                antOrderResult.setMileage(qucentOrderRecord.getMn());
                antOrderResult.setType(qucentOrderRecord.getType());
                antOrderResults.add(antOrderResult);
            }
            antOrderData.setRecords(antOrderResults);
        }
        logger.info("converted antQueenOrder : {}", antQueenOrder);
        return antQueenOrder;
    }

    public static String convertUnicode(String ori){
        char aChar;
        int len = ori.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = ori.charAt(x++);
            if (aChar == '\\') {
                aChar = ori.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = ori.charAt(x++);
                        switch (aChar) {
                            case '0': case '1': case '2': case '3': case '4':case '5': case '6': case '7': case '8': case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
}
