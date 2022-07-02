package com.foundation.elasticsearch.retrieve.message;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.foundation.elasticsearch.api.retrieve.mess.CanalRetrieveMessApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author by jacksonz
 * @classname CanalRetrieveMessageService
 * @description 使用canal - client 连接获取binlog
 * @date 2020/6/7 10:50
 */
@Service
public class CanalClientRetrieveMessageService implements CanalRetrieveMessApi {

    @Value("${canal.port:11111}")
    private Integer port;

    @Value("${canal.destination:example}")
    private String destination;

    @Value("${canal.userName}")
    private String userName;

    @Value("${canal.password:}")
    private String password;

    @Value("${canal.batchSize:100}")
    private Integer batchSize;

    public void canalConnector() {
        this.canalConnector(port, destination, userName, password, batchSize);
    }

    public void canalConnector(Integer port, String canalDestination, String userName, String password, Integer batchSize) {
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(AddressUtils.getHostIp(), port), canalDestination, userName, password);
        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();

            while (true) {
                // 获取指定数量的数据
                Message message = connector.getWithoutAck(batchSize);
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    // System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                    printEntry(message.getEntries());
                }

                connector.ack(batchId); // 提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }
        } finally {
            connector.disconnect();
        }
    }

    private void printEntry(List<CanalEntry.Entry> entryList) {
        for (CanalEntry.Entry entry : entryList) {

            // 跳过事务开启和事务关闭的信息
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChange = null;
            try {
                // 解析 binlog-rowData
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(), e);
            }

            CanalEntry.EventType eventType = rowChange.getEventType();
            System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));

            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                if (eventType == CanalEntry.EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());

                } else if (eventType == CanalEntry.EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());

                } else if (eventType == CanalEntry.EventType.UPDATE) {
                    System.out.println("-------> before");
                    printColumn(rowData.getBeforeColumnsList());

                    System.out.println("-------> after");
                    printColumn(rowData.getAfterColumnsList());

                }
            }
        }
    }

    private void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "  update=" + column.getUpdated());
        }
    }
}
