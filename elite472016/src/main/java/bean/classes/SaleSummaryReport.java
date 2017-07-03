package bean.classes;

public class SaleSummaryReport 
{

	String productID, productName, totalSaleQty,totalSalePrice, totalAmt,totalFocQty;
	double totalNetSaleAmt;
	double totalDiscount;

	public String getTotalFocQty() {
		return totalFocQty;
	}

	public void setTotalFocQty(String totalFocQty) {
		this.totalFocQty = totalFocQty;
	}

	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getTotalSaleQty() {
		return totalSaleQty;
	}

	public void setTotalSaleQty(String totalSaleQty) {
		this.totalSaleQty = totalSaleQty;
	}

	public String getTotalSalePrice() {
		return totalSalePrice;
	}

	public void setTotalSalePrice(String totalSalePrice) {
		this.totalSalePrice = totalSalePrice;
	}

	public String getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(String totalAmt) {
		this.totalAmt = totalAmt;
	}

	public double getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(double totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public double getTotalNetSaleAmt() {
		return totalNetSaleAmt;
	}

	public void setTotalNetSaleAmt(double totalNetSaleAmt) {
		this.totalNetSaleAmt = totalNetSaleAmt;
	}
	
}
