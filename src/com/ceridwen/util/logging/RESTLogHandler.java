package com.ceridwen.util.logging;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.StringUtils;

public class RESTLogHandler extends AbstractLogHandler {
    private String baseUrl;
    
    public RESTLogHandler(String url) {
        this.baseUrl = url;
    }

    public synchronized void sendRest(String logger, String level, String message) { 
        StringBuffer restString = new StringBuffer();
        boolean first = true;
        if (StringUtils.isNotEmpty(logger)) {
            if (!first) {
                restString.append("&");
            }
            first = false;
            restString.append("logger=");
            restString.append(logger);
        }
        if (StringUtils.isNotEmpty(level)) {
            if (!first) {
                restString.append("&");
            }
            first = false;
            restString.append("level=");
            restString.append(level);
        }
        if (StringUtils.isNotEmpty(message)) {
            if (!first) {
                restString.append("&");
            }
            first = false;
            restString.append("message=");
            try {
                restString.append(java.net.URLEncoder.encode(message, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
            }
        }    
        try {
            // Send data
            String urlStr = this.baseUrl;
            String params = restString.toString();
            if  (StringUtils.isNotEmpty(params)) {
                 urlStr += "?" + params;
            }
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
        } catch (Exception e) {
        }
    }

    @Override
    protected void sendMessage(String logger, String level, String message) {
        this.sendRest(logger, level, message);
    }

    @Override
    public void close() throws SecurityException {
    }

    @Override
    public void flush() {
    }

}
