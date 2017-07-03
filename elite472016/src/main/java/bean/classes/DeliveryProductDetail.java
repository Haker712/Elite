package bean.classes;

public class DeliveryProductDetail
{
	String customerID,invoiceID,deliverDate,totalAmt,advancePay,volumeDiscount,remainingAmt;

	public String getCustomerID() {
		return customerID;
	}

	public String getRemainingAmt() {
		return remainingAmt;
	}

	public void setRemainingAmt(String remainingAmt) {
		this.remainingAmt = remainingAmt;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(String invoiceID) {
		this.invoiceID = invoiceID;
	}

	public String getDeliverDate() {
		return deliverDate;
	}

	public void setDeliverDate(String deliverDate) {
		this.deliverDate = deliverDate;
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

	
}
