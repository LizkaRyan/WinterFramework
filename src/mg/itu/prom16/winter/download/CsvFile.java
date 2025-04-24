package mg.itu.prom16.winter.download;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CsvFile implements DownloadableFile{
    private final String content;

    private final String fileName;

    public CsvFile(String fileName,String content){
        this.fileName = fileName;
        this.content = content;
    }

    @Override
    public void setResponse(HttpServletResponse response) throws IOException {
        // Configurer la réponse HTTP
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+".pdf\"");

        // Écrire le contenu dans le flux de réponse
        PrintWriter writer = response.getWriter();
        writer.write(content);
        writer.flush();
    }
}
