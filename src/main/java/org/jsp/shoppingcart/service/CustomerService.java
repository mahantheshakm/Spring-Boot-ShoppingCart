package org.jsp.shoppingcart.service;

import java.util.Random;

import org.jsp.shoppingcart.dao.CustomerDao;
import org.jsp.shoppingcart.dto.Customer;
import org.jsp.shoppingcart.helper.Emailverification;
import org.jsp.shoppingcart.helper.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
	@Autowired
	CustomerDao dao;

	@Autowired
	Emailverification emailverification;

	public ResponseStructure<Customer> saveCustomer(Customer customer) {
		Random random = new Random();
		int otp = random.nextInt(100000, 999999);
		customer.setOtp(otp);

		emailverification.sendVerification(customer);

		ResponseStructure<Customer> structure = new ResponseStructure<>();
		structure.setData(dao.saveCustomer(customer));
		structure.setMessage("Verify your Email");
		structure.setStatus(HttpStatus.PROCESSING.value());
		return structure;
	}

	public ResponseStructure<Customer> verifyCustomer(int id, int otp) {
		ResponseStructure<Customer> structure = new ResponseStructure<>();

		Customer customer = dao.findById(id);

		if (customer == null) {
			structure.setData(null);
			structure.setMessage("Id Not Found");
			structure.setStatus(HttpStatus.BAD_REQUEST.value());
		} else {
			if (otp == customer.getOtp()) {
				customer.setStatus(true);
				structure.setData(dao.saveCustomer(customer));
				structure.setMessage("Account created Succesfully");
				structure.setStatus(HttpStatus.CREATED.value());
			} else {
				structure.setData(null);
				structure.setMessage("OTP Missmatch");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
			}
		}

		return structure;
	}

}
