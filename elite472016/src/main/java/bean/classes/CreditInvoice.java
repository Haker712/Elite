package bean.classes;

public class CreditInvoice 
{
	/*//match with InvoiceInfo
	
	String invoiceNo;
	String invoiceDate;
	String productReceiptPersonName;
	String totalAmt;
	String advancePay;
	String volumeDiscount;
	String netAmt;
    String customerID;   
    String creditTerm;
    String payAmount;*/
	
	String paidAmt,customerId,invoiceNo,invoiceDate,productReceiptPersonName,toalAmt,advancePay,volumeDiscount,netAmt,creditTerm;

	public String getCreditTerm() {
		return creditTerm;
	}

	public void setCreditTerm(String creditTerm) {
		this.creditTerm = creditTerm;
	}

	public String getPaidAmt() {
		return paidAmt;
	}

	public void setPaidAmt(String paidAmt) {
		this.paidAmt = paidAmt;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getProductReceiptPersonName() {
		return productReceiptPersonName;
	}

	public void setProductReceiptPersonName(String productReceiptPersonName) {
		this.productReceiptPersonName = productReceiptPersonName;
	}

	public String getToalAmt() {
		return toalAmt;
	}

	public void setToalAmt(String toalAmt) {
		this.toalAmt = toalAmt;
	}

	public String getAdvancePay() {
		return advancePay;
	}

	public void setAdvancePay(String advancePay) {
		this.advancePay = advancePay;
	}

	public String getVolumeDiscount() {
		return volumeDiscount;
	}

	public void setVolumeDiscount(String volumeDiscount) {
		this.volumeDiscount = volumeDiscount;
	}

	public String getNetAmt() {
		return netAmt;
	}

	public void setNetAmt(String netAmt) {
		this.netAmt = netAmt;
	}
	
	
}
