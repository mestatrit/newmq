package com.elex.newmq.share;

import java.util.concurrent.atomic.AtomicInteger;

import com.elex.newmq.constant.DBServiceStatus;
import com.elex.newmq.service.DataSourceManager;

/**
 * ����һЩ������ñ���
 * @author qxc
 *
 */
public class ShareVariable {
	//���ݿ����ĵ�ǰ״̬
	public static DBServiceStatus dbServiceStatus = DBServiceStatus.idle;
	//�־û��ļ�ָ�뵱ǰ��λ��
	public static AtomicInteger curFileOffset = new AtomicInteger(0);
	public static DataSourceManager dataSource = new DataSourceManager();
}
