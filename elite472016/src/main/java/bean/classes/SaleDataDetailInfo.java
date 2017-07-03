package bean.classes;

public class SaleDataDetailInfo
{
	 String invoiceID,productID,saleQty,salePrice,purchasePrice,discountAmt,disPercent,productName;
	 double totalAmt;
		
	String remark;
		
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	  
	public String getDisPercent() {
		return disPercent;
	}

	public void setDisPercent(String disPercent) {
		this.disPercent = disPercent;
	}

	public String getInvoiceID() {
		return invoiceID;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setInvoiceID(String invoiceID) {
		this.invoiceID = invoiceID;
	}

	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public String getSaleQty() {
		return saleQty;
	}

	public void setSaleQty(String saleQty) {
		this.saleQty = saleQty;
	}

	public String getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}

	public String getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getDiscountAmt() {
		return discountAmt;
	}

	public void setDiscountAmt(String discountAmt) {
		this.discountAmt = discountAmt;
	}

	public double getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(double totalAmt) {
		this.totalAmt = totalAmt;
	}
}
