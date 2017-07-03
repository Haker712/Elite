package bean.classes;

public class InvoiceInfo {
	
	String invoiceNo;
	String invoiceDate;
	String productReceiptPersonName;
	String totalAmt;
	String advancePay;
	String volumeDiscount;
	String netAmt;
    String customerID;   
    String dueDate;
    public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	String payAmount;
    
 
public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
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
	public String getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(String totalAmt) {
		this.totalAmt = totalAmt;
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
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
}
