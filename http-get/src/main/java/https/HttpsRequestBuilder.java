package https;

import common.HttpCodec;
import common.HttpURL;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Map;

/**
 * @Author ws
 * @Date 2021/8/23 14:45
 */
public class HttpsRequestBuilder {
    public void testHttps() throws Exception {
        final HttpURL httpURL = new
                HttpURL("https://www.nowcoder.com/");


        System.out.println("protocol:" + httpURL.getProtocol());
        System.out.println("ip:" + httpURL.getHost());
        System.out.println("port:" + httpURL.getPort());
        System.out.println("path:" + httpURL.getFile());
        System.out.println("host:" + httpURL.getHost());


        //获得一个ssl上下文
        SSLContext sslContext = SSLContext.getInstance("TLS");
        //信任本地证书
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        sslContext.init(null, trustManagers, null);


        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        Socket socket = socketFactory.createSocket();
        socket.connect(new InetSocketAddress(httpURL.getHost(), httpURL.getPort()));
        //获得输入输出流
        final OutputStream os = socket.getOutputStream();
        final InputStream is = socket.getInputStream();

        new Thread() {
            @Override
            public void run() {
                HttpCodec httpCodec = new HttpCodec();

                try {
                    //读一行  响应行
                    String responseLine = httpCodec.readLine(is);
                    System.out.print("响应行：" + responseLine);

                    //读响应头
                    Map<String, String> headers = httpCodec.readHeaders(is);
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        System.out.print(entry.getKey() + ": " + entry.getValue());
                    }

                    //读响应体 ? 需要区分是以 Content-Length 还是以 Chunked分块编码
                    if (headers.containsKey("Content-Length")) {
                        String s = headers.get("Content-Length");
                        // 截掉 Content-Length: 451/r/n  后面的/r/n,拿到451
                        int length = Integer.valueOf(s.substring(0,s.length()-2));
                        byte[] bytes = httpCodec.readBytes(is, length);
                        System.out.print("响应:"+new String(bytes));
                    } else {
                        //分块编码
                        String response = httpCodec.readChunked(is);
                        System.out.print("响应:"+response);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();


        StringBuffer protocol = new StringBuffer();
        //请求行
        protocol.append("GET");
        protocol.append(" ");
        protocol.append(httpURL.getFile());
        protocol.append(" ");
        protocol.append("HTTP/1.1");
        protocol.append("\r\n");

        //http请求头
        protocol.append("Host: ");
        protocol.append(httpURL.getHost());
        protocol.append("\r\n");

        //空行
        protocol.append("\r\n");

        //请求体 GET没有


        System.out.println("发送报文：\n" + protocol);
        os.write(protocol.toString().getBytes());
        os.flush();

    }

    public static void main(String[] args) throws Exception {
        new HttpsRequestBuilder().testHttps();
    }
}
