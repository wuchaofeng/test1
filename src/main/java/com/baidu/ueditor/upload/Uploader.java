package com.baidu.ueditor.upload;

import java.io.File;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
//import com.aliyun.openservices.oss.OSSClient;
//import com.aliyun.openservices.oss.model.Bucket;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.qikemi.packages.alibaba.aliyun.oss.BucketService;
//import com.qikemi.packages.alibaba.aliyun.oss.OSSClientFactory;
import com.qikemi.packages.alibaba.aliyun.oss.properties.OSSClientProperties;
import com.qikemi.packages.baidu.ueditor.upload.AsynUploaderThreader;
import com.qikemi.packages.baidu.ueditor.upload.SynUploader;
import com.qikemi.packages.utils.SystemUtil;
import com.xnx3.j2ee.func.AttachmentFile;
import com.xnx3.j2ee.func.Log;
import com.xnx3.j2ee.shiro.ShiroFunc;

/**
 * 同步上传文件到阿里云OSS<br>
 * 
 * @create date : 2014年10月28日 上午22:15:00
 * @Author XieXianbin<a.b@hotmail.com>
 * @Source Repositories Address:
 *         <https://github.com/qikemi/UEditor-for-aliyun-OSS>
 */
public class Uploader {
	private HttpServletRequest request = null;
	private Map<String, Object> conf = null;

	public Uploader(HttpServletRequest request, Map<String, Object> conf) {
		this.request = request;
		this.conf = conf;
	}

	public State doExecss(){
		State state = null;
		state = BinaryUploader.save(this.request, this.conf);
		JSONObject stateJson = new JSONObject(state.toJSONString());
//		String bucketName = OSSClientProperties.bucketName;
//		OSSClient client = OSSClientFactory.createOSSClient();
		if (OSSClientProperties.useAsynUploader) {
			Log.debug(state.toJSONString());
			AsynUploaderThreader asynThreader = new AsynUploaderThreader();
			asynThreader.init(stateJson, this.request);
			Thread uploadThreader = new Thread(asynThreader);
			uploadThreader.start();
		} else {
			Log.debug(state.toJSONString());
			SynUploader synUploader = new SynUploader();
			synUploader.upload(stateJson, this.request);
		}
		if (false == OSSClientProperties.useLocalStorager) {
			String uploadFilePath = (String) this.conf.get("rootPath") + (String) stateJson.get("url");
			File uploadFile = new File(uploadFilePath);
			if (uploadFile.isFile() && uploadFile.exists()) {
				uploadFile.delete();
			}
		}
		state.putInfo("url", OSSClientProperties.ossEndPoint + stateJson.getString("url"));
		Log.debug("state.url : "+OSSClientProperties.ossEndPoint + stateJson.getString("url"));
		Log.debug("state.url : "+state.toJSONString());
		return state;
	}
	
	public final State doExec() {
		Log.debug("doExec--");
		State state = null;
		//是否有限制某用户上传
		if(OSSClientProperties.astrictUpload){
			if(!ShiroFunc.getUEditorAllowUpload()){
				return new BaseState(false, OSSClientProperties.astrictUploadMessage);
			}
		}
		
		String filedName = (String) this.conf.get("fieldName");
		if ("true".equals(this.conf.get("isBase64"))) {
			state = Base64Uploader.save(this.request.getParameter(filedName),
					this.conf);
			Log.debug("doExec--isBase64--"+state.toJSONString());
		} else {
			Log.debug("doExec--not isBase64");
			state = BinaryUploader.save(this.request, this.conf);
			JSONObject stateJson = new JSONObject(state.toJSONString());
			
			Log.debug("doExec--OSSClientProperties.useStatus: "+OSSClientProperties.useStatus);
			// 判别云同步方式
			if (OSSClientProperties.useStatus) {
				// upload type
				Log.debug("doExec--OSSClientProperties.useAsynUploader: "+OSSClientProperties.useAsynUploader);
				if (OSSClientProperties.useAsynUploader) {
					AsynUploaderThreader asynThreader = new AsynUploaderThreader();
					asynThreader.init(stateJson, this.request);
					Thread uploadThreader = new Thread(asynThreader);
					uploadThreader.start();
				} else {
					SynUploader synUploader = new SynUploader();
					synUploader.upload(stateJson, this.request);
				}

				// storage type
				if (false == OSSClientProperties.useLocalStorager) {
					String uploadFilePath = (String) this.conf.get("rootPath") + (String) stateJson.get("url");
					File uploadFile = new File(uploadFilePath);
					if (uploadFile.isFile() && uploadFile.exists()) {
						uploadFile.delete();
					}
				}
				
				String uploadPath = stateJson.getString("url"); //上传的路径，如  /site/219/news/20191119/2234234.png
				if(uploadPath.indexOf("/") == 0){
					//如果最开始是 / ，那么吧这个去掉
					uploadPath = uploadPath.substring(1, uploadPath.length());
				}
				state.putInfo("url", OSSClientProperties.ossEndPoint + uploadPath);
				Log.debug("doExec--上传到对象存储： "+OSSClientProperties.ossEndPoint + uploadPath);
			} else {
				//绝对路径，而非原本的相对路径
				String stateStr = stateJson.getString("state");
				if(stateStr.equals("SUCCESS")){
					state.putInfo("url",  AttachmentFile.netUrl() + SystemUtil.getProjectName() + stateJson.getString("url"));
					Log.debug("doExec--上传服务器磁盘： "+AttachmentFile.netUrl() + SystemUtil.getProjectName() + stateJson.getString("url"));
				}else{
					//不成功，忽略。自然会在客户端弹出提示
				}
			}
		}
		/*
		 * { "state": "SUCCESS", "title": "1415236747300087471.jpg", "original":
		 * "a.jpg", "type": ".jpg", "url":
		 * "/upload/image/20141106/1415236747300087471.jpg", "size": "18827" }
		 */
		Log.debug("doExec--最后返回："+state.toJSONString());
		return state;
	}
}
