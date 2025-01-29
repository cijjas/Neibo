package ar.edu.itba.paw.webapp.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IndexServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexServlet.class);

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LOGGER.info("Index Servlet taking action");

        Path file = Paths.get(getServletContext().getRealPath("/static/browser/index.html"));
        resp.setContentType("text/html");
        Files.copy(file, resp.getOutputStream());
    }
}