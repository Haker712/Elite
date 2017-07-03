package bean.classes;

public class SaleDataInfo 
{
	String invoiceID,saleDate,totalitemandvolDisAmt,totalAmtNoDiscount,totalAmt,netAmt,discountAmt,payAmount,recPersonName,signImg,salePersonID,dueDate,cashOrCredit,locationCode,devID,invoiceTime,customerName,address,refund;

	public static String customerID;

	public String getTotalitemandvolDisAmt() {
		return totalitemandvolDisAmt;
	}

	public void setTotalitemandvolDisAmt(String totalitemandvolDisAmt) {
		this.totalitemandvolDisAmt = totalitemandvolDisAmt;
	}

	public String getTotalAmtNoDiscount() {
		return totalAmtNoDiscount;
	}

	public void setTotalAmtNoDiscount(String totalAmtNoDiscount) {
		this.totalAmtNoDiscount = totalAmtNoDiscount;
	}

	public String getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(String invoiceID) {
		this.invoiceID = invoiceID;
	}

	public String getCustomerID() {
		return customerID;
	}

	public String getNetAmt() {
		return netAmt;
	}

	public void setNetAmt(String netAmt) {
		this.netAmt = netAmt;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getRefund() {
		return refund;
	}

	public void setRefund(String refund) {
		this.refund = refund;
	}

	public String getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(String saleDate) {
		this.saleDate = saleDate;
	}

	public String getTotalAmt()
	{
		return totalAmt;
	}

	public void setTotalAmt(String totalAmt) 
	{
		this.totalAmt = totalAmt;
	}

	public String getDiscountAmt()
	{
		return discountAmt;
	}

	public void setDiscountAmt(String discountAmt)
	{
		this.discountAmt = discountAmt;
	}

	public String getPayAmount() 
	{
		return payAmount;
	}

	public void setPayAmount(String payAmount)
	{
		this.payAmount = payAmount;
	}

	public String getRecPersonName()
	{
		return recPersonName;
	}

	public void setRecPersonName(String recPersonName)
	{
		this.recPersonName = recPersonName;
	}

	public String getSignImg()
	{
		return signImg;
	}

	public void setSignImg(String signImg)
	{
		this.signImg = signImg;
	}

	public String getSalePersonID() 
	{
		return salePersonID;
	}

	public void setSalePersonID(String salePersonID) 
	{
		this.salePersonID = salePersonID;
	}

	public String getDueDate()
	{
		return dueDate;
	}

	public void setDueDate(String dueDate)
	{
		this.dueDate = dueDate;
	}

	public String getCashOrCredit() 
	{
		return cashOrCredit;
	}

	public void setCashOrCredit(String cashOrCredit)
	{
		this.cashOrCredit = cashOrCredit;
	}

	public String getLocationCode() 
	{
		return locationCode;
	}

	public void setLocationCode(String locationCode) 
	{
		this.locationCode = locationCode;
	}

	public String getDevID()
	{
		return devID;
	}

	public void setDevID(String devID) 
	{
		this.devID = devID;
	}

	public String getInvoiceTime() 
	{
		return invoiceTime;
	}

	public void setInvoiceTime(String invoiceTime) {
		this.invoiceTime = invoiceTime;
	}
}
