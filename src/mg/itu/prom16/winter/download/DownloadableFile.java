package mg.itu.prom16.winter.download;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@FunctionalInterface
public interface DownloadableFile {
    public void setResponse(HttpServletResponse response) throws IOException;
}
