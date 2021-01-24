package com.example.demo.filters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.controller.Student;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class RequestResponseLoggersFilter implements Filter{
	
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		CustomRequestWapper requestMapper=new CustomRequestWapper((HttpServletRequest) request);
		log.info("Host name :{}",requestMapper.getServerName());
		log.info("port no:{}",requestMapper.getServerPort());
		log.info("HTTP request header :{}",requestMapper.getHeader("Authorization"));
				
		String uri=requestMapper.getRequestURI();
		log.info("HTTP request URI:{}",requestMapper.getRequestURI());
		log.info("HTTP request method :{}",requestMapper.getMethod());
		
		String  requestData=new String(requestMapper.getByteArray()).replace("\n", "");
		// masking or hiding request data for particular request
		if("/api/signUp".equalsIgnoreCase(uri)) {
			
			Student student=objectMapper.readValue(requestData, Student.class);
			student.setCourse("**************");
			requestData=objectMapper.writeValueAsString(student);
		}
		
		log.info("HTTP request body :{}",requestData);
		
		CustomResponseWapper responseMapper=new CustomResponseWapper((HttpServletResponse) response);
		chain.doFilter(requestMapper, responseMapper);
		log.info("HTTP status code :{}",responseMapper.getStatus());
		String responseData=new String(responseMapper.getBaos().toByteArray());

		// masking or hiding response data for particular request
		
		if("/api/signUp".equalsIgnoreCase(uri)) {
			Student student=objectMapper.readValue(responseData, Student.class);
			student.setCourse("*********");
			responseData=objectMapper.writeValueAsString(student);
		}
		log.info("HTTP response body :{}",responseData);
	}

	private class CustomRequestWapper extends HttpServletRequestWrapper{

		private byte[] byteArray;

		public CustomRequestWapper(HttpServletRequest request) {
			super(request);
			try {
				byteArray=IOUtils.toByteArray(request.getInputStream());
			} catch (IOException e) {
				throw new RuntimeException("error while reading stream of data");
			}
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			return new CustomDelegatingServletInputStream(new ByteArrayInputStream(byteArray));
		}

		public byte[] getByteArray() {
			return byteArray;
		}
	}

	private class CustomResponseWapper extends HttpServletResponseWrapper{

		public CustomResponseWapper(HttpServletResponse response) {
			super(response);
		}

		private ByteArrayOutputStream baos = new ByteArrayOutputStream();

		private PrintStream printStream = new PrintStream(baos);

		public ByteArrayOutputStream getBaos() {
			return baos;
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return new CustomDelegatingServletOutputStream(new TeeOutputStream(super.getOutputStream(), printStream));
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			return new PrintWriter(new TeeOutputStream(super.getOutputStream(), printStream));
		}
	}
}
