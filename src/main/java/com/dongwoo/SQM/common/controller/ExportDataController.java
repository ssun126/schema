package com.dongwoo.SQM.common.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;

@Controller
@RequestMapping("/common")
public class ExportDataController {
    @RequestMapping(value = "/exportData", method = RequestMethod.POST)
    public @ResponseBody
    String exportData(@RequestParam("pq_data") String pq_data,@RequestParam("pq_ext") String pq_ext,@RequestParam("pq_decode")  Boolean pq_decode, @RequestParam("pq_filename") String pq_filename, HttpServletRequest request) throws IOException {

        String[] arr = new String[] {"csv", "xlsx", "htm", "zip", "json"};
        String filename = pq_filename + "." + pq_ext;

        if(Arrays.asList(arr).contains(pq_ext)){
            HttpSession ses = request.getSession(true);
            ses.setAttribute("pq_data", pq_data);
            ses.setAttribute("pq_decode", pq_decode);
            ses.setAttribute("pq_filename", filename);
        }
        return filename;
    }

    @RequestMapping(value = "/exportData", method = RequestMethod.GET)
    public void exportData(@RequestParam("pq_filename") String pq_filename, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession ses = request.getSession(true);
        if ( ((String)ses.getAttribute("pq_filename")).equals(pq_filename) ) {

            String contents = (String) ses.getAttribute("pq_data");
            Boolean pq_decode = (Boolean) ses.getAttribute("pq_decode");

            byte[] bytes = pq_decode?    Base64.getDecoder().decode(contents): contents.getBytes(Charset.forName("UTF-8"));

            response.setContentType("application/octet-stream");

            response.setHeader("Content-Disposition",
                    "attachment;filename=" + pq_filename);
            response.setContentLength(bytes.length);
            ServletOutputStream out = response.getOutputStream();
            out.write(bytes);

            out.flush();
            out.close();
        }
    }
}
