import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author ws
 * @Date 2021/4/15 11:12
 * @Version 1.0
 */
public class HttpRequestBuilder {


    public static void buildRequest() throws IOException {
        final HttpURL httpURL = new HttpURL("http://restapi.amap.com/v3/weather/weatherInfo?city=长沙&key=13cb58f5884f9749287abbead9c658f2");
        StringBuffer stringBuffer = new StringBuffer();
        // 请求行  GET city=长沙&key=13cb58f5884f9749287abbead9c658f2 HTTP/1.1
        stringBuffer.append("GET");
        stringBuffer.append(" ");
        stringBuffer.append(httpURL.getFile());
        stringBuffer.append(" ");
        stringBuffer.append("HTTP/1.1");
        stringBuffer.append("\r\n");

        // 请求头
        stringBuffer.append("Host: ");
        stringBuffer.append(httpURL.getHost());
        stringBuffer.append("\r\n");

        // 中间得空行
        stringBuffer.append("\r\n");

        System.out.println("发送报文：\n" + stringBuffer);

        // 建立socket套接字并连接
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(httpURL.getHost(), httpURL.getPort()));

        // 编写请求
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(stringBuffer.toString().getBytes());
        outputStream.flush();

        final InputStream inputStream = socket.getInputStream();

        new Thread(){
            @Override
            public void run() {
                HttpCodec httpCodec = new HttpCodec();
                try {
                    //读一行  响应行
                    String responseLine = httpCodec.readLine(inputStream);
                    System.out.println("响应行：" + responseLine);

                    //读响应头
                    Map<String, String> headers = httpCodec.readHeaders(inputStream);
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }

                    //读响应体 ? 需要区分是以 Content-Length 还是以 Chunked分块编码
                    if (headers.containsKey("Content-Length")) {
                        int length = Integer.valueOf(headers.get("Content-Length"));
                        byte[] bytes = httpCodec.readBytes(inputStream, length);
                        System.out.println("响应:" + new String(bytes));
                    } else {
                        //分块编码
                        String response = httpCodec.readChunked(inputStream);
                        System.out.println("响应:" + response);
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }.start();


    }

    public static void main(String[] args) throws IOException {
        buildRequest();
    }
}


