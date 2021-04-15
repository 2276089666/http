import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Author ws
 * @Date 2021/4/15 11:15
 * @Version 1.0
 */
public class HttpURL {
    /**
     * 协议
     */
    private String protocol;
    private String host;
    /**
     * 内容
     */
    private String file;
    private int port;

    public HttpURL(String path) throws MalformedURLException {
        URL url = new URL(path);
        file=url.getFile();
        host=url.getHost();
        file = (file == null || file.length() == 0) ? "/" : file;
        protocol = url.getProtocol();
        port = url.getPort();
        port = port == -1 ? url.getDefaultPort() : port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getFile() {
        return file;
    }

    public int getPort() {
        return port;
    }
}
