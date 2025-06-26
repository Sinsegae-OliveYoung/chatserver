
public class Payload {
	
	private String requestType;
	private Sender sender;
	private String data;
	private FileMeta fileMeta;
	
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public Sender getSender() {
		return sender;
	}
	public void setSender(Sender sender) {
		this.sender = sender;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public FileMeta getFileMeta() {
		return fileMeta;
	}
	public void setFileMeta(FileMeta fileMeta) {
		this.fileMeta = fileMeta;
	}
	
}

