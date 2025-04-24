package mg.itu.prom16.winter.download;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PdfFile implements DownloadableFile{

    private final String fileName;

    private final byte[] bytes;

    public PdfFile(String fileName,byte[] bytes){
        this.fileName = fileName;
        this.bytes = bytes;
    }

    @Override
    public void setResponse(HttpServletResponse response)throws IOException {
        // Préparer la réponse HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\""+this.fileName+".pdf\"");

        // Envoyer le contenu du PDF au client
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
    }
}
