package com.ceridwen.util.logging;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

public class RESTLogHandler extends AbstractLogHandler {
    private String baseUrl;
    
    public RESTLogHandler(String url) {
        this.baseUrl = url;
    }

    public synchronized void sendRest(String logger, String level, String message) { 
        StringBuffer restString = new StringBuffer();
        boolean first = true;
        if (!((logger==null)?"":logger).isEmpty()) {
            if (!first) {
                restString.append("&");
            }
            first = false;
            restString.append("logger=");
            restString.append(logger);
        }
        if (!((level==null)?"":level).isEmpty()) {
            if (!first) {
                restString.append("&");
            }
            first = false;
            restString.append("level=");
            restString.append(level);
        }
        if (!((message==null)?"":message).isEmpty()) {
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
            if  (params.length() > 0) {
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
