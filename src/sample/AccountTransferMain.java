package sample;

import org.junit.Assert;

import sample.domain.BankAccount;
import sample.service.AccountService;

import com.jdon.controller.AppUtil;
import com.jdon.domain.message.DomainMessage;

public class AccountTransferMain {


	public static boolean testTransfer() {
		AppUtil appUtil = new AppUtil();
		AccountService accountService = (AccountService) appUtil
				.getComponentInstance("accountService");
		BankAccount bankAccountA = accountService.getBankAccount("11", 100);
		BankAccount bankAccountB = accountService.getBankAccount("22", 0);
		DomainMessage res = accountService.transfer(bankAccountA, bankAccountB,
				100);

		Boolean res1 = (Boolean) res.getBlockEventResult();

		Assert.assertEquals(0, bankAccountA.getAmount());
		Assert.assertEquals(100, bankAccountB.getAmount());

		return (bankAccountA.getAmount()==0 && bankAccountB.getAmount()==100);
	}


	public static void main(String[] args) {

		testTransfer();
	}

}
