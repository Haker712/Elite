package bean.classes;

public class PaidCreditCollectionInfo 
{
	String customerID,payerName,payerSignImg,invoiceID,paidAmount;
	
	public PaidCreditCollectionInfo(String customerID,String payerName,String payerSignImg,String invoiceId,String paidAmount)
	{
		this.customerID=customerID;
		this.payerName=payerName;
		this.payerSignImg=payerSignImg;
		this.invoiceID=invoiceId;
		this.paidAmount=paidAmount;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	public String getPayerSignImg() {
		return payerSignImg;
	}

	public void setPayerSignImg(String payerSignImg) {
		this.payerSignImg = payerSignImg;
	}

	public String getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(String invoiceID) {
		this.invoiceID = invoiceID;
	}

	public String getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
	}
	
}
