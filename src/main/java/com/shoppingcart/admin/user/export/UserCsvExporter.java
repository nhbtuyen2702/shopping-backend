package com.shoppingcart.admin.user.export;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shoppingcart.admin.AbstractExporter;
import com.shoppingcart.admin.entity.User;

public class UserCsvExporter extends AbstractExporter {

	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv", "users_");//các tham số là các định dạng mong muốn

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		String[] csvHeader = { "User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled" };//phần header hiển thị
		String[] fieldMapping = { "id", "email", "firstName", "lastName", "roles", "enabled" };//khai báo các thuộc tính trong entity

		csvWriter.writeHeader(csvHeader);//write header

		for (User user : listUsers) {
			csvWriter.write(user, fieldMapping);//write rows
		}

		csvWriter.close();
	}
}
