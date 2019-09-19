package com.oculus.task2.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.pkts.buffer.Buffer;
import lombok.Data;
@Data
public class HTMLParser {
    	
	private static final Logger log = LoggerFactory.getLogger(HTMLParser.class);
	private Map<String, String> headers = new HashMap<>();
    	
	private byte[] body;

    	
	HTMLParser(final Buffer buffer) throws IOException{
		if(buffer == null){
			return;
		}
		headers = new HashMap<>();
		InputStream inputStream = new ByteArrayInputStream(buffer.getArray());
		parseHTTPHeaders(inputStream);
		parseBody(inputStream);
		
	}
	
	public boolean isImage(){
		String contentType = headers.get("Content-Type");
		return contentType != null && contentType.contains("image/");
	}
	
	public boolean isText(){
		String contentType = headers.get("Content-Type");
		return contentType != null && (contentType.contains("application/html") 
				|| contentType.contains("application/xml") 
				|| contentType.contains("application/json") 
				|| contentType.contains("application/pdf")
				|| contentType.contains("text/"));
	}
	
	public File createFileFromBody(String filePath) throws IOException{
		String fileExt = getFileExtension();
		if(body == null || body.length == 0 || fileExt == null || (!isImage() && !isText())){
			return null;
		}
		
		File file = new File(filePath + "." + fileExt);
		if(file.createNewFile()){
    		OutputStream os = new FileOutputStream(file);
    		os.write(body);
    		os.close();
    		log.info("file created from content body <{}>",file.getAbsolutePath());
		}else{
			log.error("error while creating file <{}>",file.getName());
		}
		return file;
	}
	
	private void parseHTTPHeaders(InputStream inputStream)
            throws IOException {
        int charRead;
        StringBuffer sb = new StringBuffer();
        while (inputStream.available() > 0) {
            sb.append((char) (charRead = inputStream.read()));
            if ((char) charRead == '\r') {            // if we've got a '\r'
                sb.append((char) inputStream.read()); // then write '\n'
                charRead = inputStream.read();        // read the next char;
                if (charRead == '\r') {                  // if it's another '\r'
                    sb.append((char) inputStream.read());// write the '\n'
                    break;
                } else {
                    sb.append((char) charRead);
                }
            }
        }

        String[] headersArray = sb.toString().split("\r\n");
        for (int i = 1; i < headersArray.length - 1; i++) {
        	if(headersArray[i].contains(": ")){
        		headers.put(headersArray[i].split(": ")[0],
                    headersArray[i].split(": ")[1]);
        	}
        }
    }
	
	private void parseBody(InputStream inputStream) throws IOException{
        body = new byte[inputStream.available()];
        inputStream.read(body,0,inputStream.available());
    }
	
	private String getFileExtension(){
		String contentType = headers.get("Content-Type");
		if(contentType == null){
			return null;
		}
		String ext = null;
		String[] contentSplit = contentType.split("\\+|,|;");
		for (String content : contentSplit) {
			if(content.contains("application/") || content.contains("text/")){
				ext = content.substring(content.indexOf("/") + 1, content.length());
				break;
			}else if(content.contains("image/")){
				ext = content.substring(content.indexOf("/") + 1, content.length());
				break;
			}
		}
		return ext;

	}
}