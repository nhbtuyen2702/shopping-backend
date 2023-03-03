package com.shoppingcart.admin;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

public class AbstractExporter {

	public void setResponseHeader(HttpServletResponse response, String contentType, String extension, String prefix)
			throws IOException {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");//khai báo định dạng năm-tháng-ngày_giờ-phút-giây
		String timestamp = dateFormatter.format(new Date());//new Date() trả về ngày tháng năm giờ phút giây hiện tại
		String fileName = prefix + timestamp + extension;//users_2023-01-01_01-01-01.csv

		response.setContentType(contentType);//content Type của CSV: text/csv

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + fileName;
		response.setHeader(headerKey, headerValue);
	
	}
}
