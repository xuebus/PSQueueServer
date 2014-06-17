package com.leansoft.bigqueue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wjw.psqueue.msg.ResQueueStatus;
import wjw.psqueue.msg.ResSubStatus;
import wjw.psqueue.msg.ResultCode;
import wjw.psqueue.server.App;

import com.leansoft.bigqueue.utils.FileUtil;

public class FanOutQueueImplEx extends FanOutQueueImpl {
	String _queueName;

	public FanOutQueueImplEx(String queueDir, String queueName) throws IOException {
		super(queueDir, queueName);
		this._queueName = queueName;
	}

	public FanOutQueueImplEx(String queueDir, String queueName, int pageSize) throws IOException {
		super(queueDir, queueName, pageSize);
		this._queueName = queueName;
	}

	public long getNumberOfBackFiles() { //��FanOutQueueImpl.getBackFileSize()��������:������index�ļ�!
		return super.innerArray.dataPageFactory.getBackPageFileSet().size();
	}

	public void erase() throws IOException {
		String name = super.innerArray.arrayDirectory;

		super.removeAll();
		super.close();

		FileUtil.deleteDirectory(new File(name));
	}

	public void removeFanout(String fanoutId) throws IOException {
		this.queueFrontMap.remove(fanoutId).indexPageFactory.deleteAllPages();

		String dirName = innerArray.arrayDirectory + QUEUE_FRONT_INDEX_PAGE_FOLDER_PREFIX + fanoutId;
		FileUtil.deleteDirectory(new File(dirName));
	}

	public List<String> getAllFanoutNames() {
		List<String> result = new ArrayList<String>(this.queueFrontMap.size());
		for (QueueFront qf : this.queueFrontMap.values()) {
			result.add(qf.fanoutId);
		}

		return result;
	}

	public void initQueueFront(String fanoutId) throws IOException {
		super.getQueueFront(fanoutId);
	}

	public ResQueueStatus getQueueInfo() throws IOException {
		return new ResQueueStatus(ResultCode.SUCCESS, _queueName.substring(App.PREFIX_QUEUE.length()), super.size(), this.getRearIndex(), this.getFrontIndex());
	}

	public ResSubStatus getFanoutInfo(String fanoutId) throws IOException {
		return new ResSubStatus(ResultCode.SUCCESS,
		    _queueName.substring(App.PREFIX_QUEUE.length()),
		    fanoutId.substring(App.PREFIX_SUB.length(), fanoutId.lastIndexOf(_queueName)),
		    super.size(fanoutId),
		    this.getRearIndex(),
		    this.getFrontIndex(fanoutId));
	}

}