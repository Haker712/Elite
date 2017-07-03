package bean.classes;

public class SaleReturnProduct 
{
	String customerID, productID, deliverQty, deliverDate, productName, returnDeliverDate, returnQty;

	public SaleReturnProduct(String cusidStr, String proidStr,
			String productStr, String delQtyStr, String delDateStr,
			String returnQtyStr, String returnDelDateStr) 
	{
		super();
		this.customerID = cusidStr;
		this.productID = proidStr;
		this.deliverQty = delQtyStr;
		this.deliverDate = delDateStr;
		this.productName = productStr;
		this.returnDeliverDate = returnDelDateStr;
		this.returnQty = returnQtyStr;
	}

	public SaleReturnProduct() {
		
	}

	public String getReturnDeliverDate() {
		return returnDeliverDate;
	}

	public void setReturnDeliverDate(String returnDeliverDate) {
		this.returnDeliverDate = returnDeliverDate;
	}

	public String getReturnQty() {
		return returnQty;
	}

	public void setReturnQty(String returnQty) {
		this.returnQty = returnQty;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public String getDeliverQty() {
		return deliverQty;
	}

	public void setDeliverQty(String deliverQty) {
		this.deliverQty = deliverQty;
	}

	public String getDeliverDate() {
		return deliverDate;
	}

	public void setDeliverDate(String deliverDate) {
		this.deliverDate = deliverDate;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
}
