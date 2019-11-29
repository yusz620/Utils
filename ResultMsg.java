import java.io.Serializable;

/**
 * @Author: Andy Yu
 * @Version: Created Date：2019年11月25日 下午1:39:52
 * @Description: code: 200:success 500:error
 */
public class ResultMsg<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private String code;
	private String message;
	private T data;

	public ResultMsg(){}

	/**
	 * Constructor for error
	 *
	 * @param message
	 */
	public ResultMsg(String message) {
		super();
		this.code = "500";
		this.message = message;
	}

	public static <T> ResultMsg<T> success(){
		return new ResultMsg(null);
	}

	public static <T> ResultMsg success(T data){
		return new ResultMsg(data);
	}


	public static ResultMsg error(String message){
		return new ResultMsg(message);
	}

	public ResultMsg(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}


	public ResultMsg(String code, String message, T data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}


	/**
	 * Constructor for success
	 *
	 * @param message
	 */
	public ResultMsg(T data) {
		super();
		this.code = "200";
		this.data = data;
	}



	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}


}
