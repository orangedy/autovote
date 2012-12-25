package httpclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * @author Administrator
 *
 */
public class AutoVote {
	
	/** 
	 * request type, true means get, flase means post 
	 */
	private boolean requestType = true;
	
	public boolean isRequestType() {
		return requestType;
	}

	public void setRequestType(boolean requestType) {
		this.requestType = requestType;
	}

	/**
	 * the url of vote
	 */
	private String voteUrl;
	
	public String getVoteUrl() {
		return voteUrl;
	}

	public void setVoteUrl(String voteUrl) {
		this.voteUrl = voteUrl;
	}

	/**
	 * the proxy address if need
	 * if the proxy is null, then need not proxy
	 */
	private String proxy;
	
	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	/**
	 * the port of proxy
	 */
	private int port = 0;
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * the parameters of the post request
	 */
	private Map<String, String> nameValuePair;
	
	public Map<String, String> getNameValuePair() {
		return nameValuePair;
	}

	public void setNameValuePair(Map<String, String> nameValuePair) {
		this.nameValuePair = nameValuePair;
	}

	/**
	 * the number of thread
	 */
	private int threadNum = 1;
	
	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	/**
	 * the total number of vote
	 */
	private int voteNum = 1;
	
	public int getVoteNum() {
		return voteNum;
	}

	public void setVoteNum(int voteNum) {
		this.voteNum = voteNum;
	}

	/**
	 * the sleepTime(millisecond) between two vote
	 */
	private int sleepTime = 0;

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}
	
	/**
	 * the log of run state
	 */
	private StringBuffer log;
	
	public StringBuffer getLog() {
		return log;
	}

	public void setLog(StringBuffer log) {
		this.log = log;
	}

	/**
	 * work thread group
	 */
	private ExcuteThread[] thread;
	
	/**
	 * the connection manager of 4.2, seems the same of ThreadSafeClientConnManager in 4.1
	 */
	private PoolingClientConnectionManager cm;
	
	/**
	 * use of DefaultHttpClient
	 */
	private HttpClient httpClient; 

	
	public AutoVote() {
		super();
		cm = new PoolingClientConnectionManager();
		cm.setDefaultMaxPerRoute(100);
		httpClient = new DefaultHttpClient(cm);
		log = new StringBuffer();
	}
	
	/**
	 * start vote process
	 */
	public void start(){
		init();
		HttpUriRequest request;
		if(requestType == true){
			request = useGetRequest();
		}else{
			request = usePostRequest();
		}
		createMultiThread(threadNum, request);
	}
	
	/**
	 * initialize the parameters
	 */
	public void init(){
		if(proxy != null && port != 0){
			HttpHost proxyServer = new HttpHost(proxy, port);
	        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyServer);
		}
	}
	
	/**
	 * vote by get method
	 * @return 
	 */
	public HttpUriRequest useGetRequest(){
		HttpGet httpGet = new HttpGet(voteUrl);
		return httpGet;
	}
	
	/**
	 * vote by post method
	 * @return 
	 */
	public HttpUriRequest usePostRequest(){
		HttpPost httpPost = new HttpPost(voteUrl);
		if(nameValuePair != null){
			List<NameValuePair>list=new ArrayList<NameValuePair>();
			for(String key : nameValuePair.keySet()){
		        list.add(new BasicNameValuePair(key, nameValuePair.get(key)));
			}
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e) {
				System.out.println("fail to encode the post parameters");
				log.append("post parameter:fail to encode the post parameters\r\n");
			}
		}
		return httpPost;
	}
	
	/**
	 * @param request contain get/post method
	 */
	public void excuteRequest(HttpUriRequest request){
		for(int i = 0; i < voteNum/threadNum; i++){
			HttpResponse response = null;
			try {
				response = httpClient.execute(request);
			} catch (ClientProtocolException e) {
				System.out.println("the proxy may not work");
				log.append("connection fail: the proxy may not work\r\n");
			} catch (IOException e) {
				System.out.println("the network stream fail");
				log.append("execute fail: ioexception\r\n");
			} finally{
				if(response != null){
					HttpEntity entity = response.getEntity();
					try {
						String strEntity = EntityUtils.toString(entity);
						System.out.print(strEntity + "\n");
						log.append(Thread.currentThread().getName() + " success:" + strEntity + "\n");
					} catch (ParseException e) {
						System.out.println("fail to parse the entity");
						log.append("fail to parse the entity\r\n");
					} catch (IOException e) {
						System.out.println("entity stream fail");
						log.append("entity stream fail\r\n");
					}
				}
			}
		}
	}
	
	/**
	 * @param num the number of thread
	 * @param request
	 */
	public void createMultiThread(int num, HttpUriRequest request){
		thread = new ExcuteThread[num];
		for(int i = 0; i < num; i++){
			thread[i]  = new ExcuteThread(request);
			thread[i].start();
		}
		System.out.println("start " + num + " thread");
		log.append("start " + num + " thread\r\n");
		/*for(int i = 0; i < num; i++){
			try {
				thread[i].join();
			} catch (InterruptedException e) {
				System.out.println("exception when thread join");
				log.append("exception when thread join\r\n");
			}
		}
		System.out.println("stop vote");
		log.append("stop vote\r\n");*/
	}
	
	public boolean isFinished(){
		if(thread != null){
			for(int i = thread.length-1; i >= 0; i--){
				if(thread[i].isAlive()){
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * @author Administrator
	 * the class of execute thread
	 */
	public class ExcuteThread extends Thread{
		
		HttpUriRequest request;
		
		public ExcuteThread(HttpUriRequest request) {
			super();
			this.request = request;
		}

		public void run(){
			excuteRequest(request);
		}
	}
	
	public static void main(String[] args) {
		AutoVote autoVote = new AutoVote();
		autoVote.setProxy("186.113.26.36");
		autoVote.setPort(3128);
		autoVote.setVoteUrl("http://www.cxecaf.com/index.php/2012-08-12-12-32-26?view=vote&format=raw&id=453");
		autoVote.setVoteNum(1000);
		autoVote.setRequestType(false);
		autoVote.setThreadNum(2);
		autoVote.setSleepTime(100);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("imgvote", "1");
		autoVote.setNameValuePair(map);
		autoVote.start();
	}
}
