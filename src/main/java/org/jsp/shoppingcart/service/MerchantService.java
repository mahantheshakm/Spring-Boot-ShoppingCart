package org.jsp.shoppingcart.service;

import java.util.Random;

import org.jsp.shoppingcart.dao.MerchantDao;
import org.jsp.shoppingcart.dto.Merchant;
import org.jsp.shoppingcart.helper.Emailverification;
import org.jsp.shoppingcart.helper.Login;
import org.jsp.shoppingcart.helper.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class MerchantService 
{
@Autowired
MerchantDao dao;

@Autowired
Emailverification emailverification;

public ResponseStructure<Merchant> saveMerchant(Merchant merchant)
{
	Random random=new Random();
	int otp=random.nextInt(100000,999999);
	merchant.setOtp(otp);
	
	emailverification.sendVerification(merchant);
	
	ResponseStructure<Merchant> structure=new ResponseStructure<>();
	structure.setData(dao.savemerchant(merchant));
	structure.setMessage("Verify your Email");
	structure.setStatus(HttpStatus.PROCESSING.value());
	return structure;
}

public ResponseStructure<Merchant> verifyMerchant(int id, int otp) {
	ResponseStructure<Merchant> structure=new ResponseStructure<>();

	Merchant merchant=dao.findById(id);
	
	if(merchant==null)
	{
		structure.setData(null);
		structure.setMessage("Id Not Found");
		structure.setStatus(HttpStatus.BAD_REQUEST.value());		
	}
	else {
		if(otp==merchant.getOtp())
		{
			merchant.setStatus(true);
			structure.setData(dao.savemerchant(merchant));
			structure.setMessage("Account created Succesfully");
			structure.setStatus(HttpStatus.CREATED.value());
		}
		else {
			structure.setData(null);
			structure.setMessage("OTP Missmatch");
			structure.setStatus(HttpStatus.BAD_REQUEST.value());
		}
	}
	
	return structure;
}

public ResponseStructure<Merchant> merchantLogin(Login login) {
	ResponseStructure<Merchant> structure=new ResponseStructure<>();
	
	String email=login.getEmail();
	String password=login.getPassword();
	
	Merchant merchant=dao.findByEmail(email);
	if(merchant==null)
	{
		structure.setData(null);
		structure.setMessage("Email Not Found");
		structure.setStatus(HttpStatus.NOT_FOUND.value());
	}
	else {
		if(merchant.isStatus())
		{
			if(merchant.getPassword().equals(password)) {
				structure.setData(merchant);
				structure.setMessage("Login Success");
				structure.setStatus(HttpStatus.FOUND.value());
			}
			else {
				structure.setData(null);
				structure.setMessage("Password MissMatch");
				structure.setStatus(HttpStatus.BAD_REQUEST.value());
			
			}
		}
		else {
			structure.setData(null);
			structure.setMessage("Verify your Email First");
			structure.setStatus(HttpStatus.BAD_REQUEST.value());
		}
	}
	
	return structure;
}

}
