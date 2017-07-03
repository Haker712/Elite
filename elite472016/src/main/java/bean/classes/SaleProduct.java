package bean.classes;

public class SaleProduct 
{
	String categoryId, categoryName, groupId, productId, productName, totalQty = "0", sellingPrice,sellingPrice2, purchasePrice, groupName,caldiscount= "0",startDisQty,endDisQty,discountType,remainingQty,saleQty="0", tempSaleQty ="0",deliverQty ="0",isFoc="false";
	
	int tmpAvailableQty;
	
	String discountPercent;
	double discount;
	double totalAmt;
	String totalAmtNoDiscount="0";
	
	String remark;
	
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getTmpAvailableQty() {
		return tmpAvailableQty;
	}

	public void setTmpAvailableQty(int tmpAvailableQty) {
		this.tmpAvailableQty = tmpAvailableQty;
	}

	public String getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(String discountPercent) {
		this.discountPercent = discountPercent;
	}

	public String getSellingPrice2() {
		return sellingPrice2;
	}

	public void setSellingPrice2(String sellingPrice2) {
		this.sellingPrice2 = sellingPrice2;
	}

	public String getIsFoc() {
		return isFoc;
	}

	public void setIsFoc(String isFoc) {
		this.isFoc = isFoc;
	}

	public String getSaleQty() {
		return saleQty;
	}

	public String getDeliverQty() {
		return deliverQty;
	}

	public void setDeliverQty(String deliverQty) {
		this.deliverQty = deliverQty;
	}

	public String getTotalAmtNoDiscount() {
		return totalAmtNoDiscount;
	}

	public void setTotalAmtNoDiscount(String totalAmtNoDiscount) {
		this.totalAmtNoDiscount = totalAmtNoDiscount;
	}

	public void setSaleQty(String saleQty) {
		this.saleQty = saleQty;
	}

	public String getRemainingQty() {
		return remainingQty;
	}

	public void setRemainingQty(String remainingQty) {
		this.remainingQty = remainingQty;
	}

	public String getDiscountType() {
		return discountType;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public String getTempSaleQty() {
		return tempSaleQty;
	}

	public void setTempSaleQty(String tempSaleQty) {
		this.tempSaleQty = tempSaleQty;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(String totalQty) {
		this.totalQty = totalQty;
	}

	public String getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(String sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public String getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(String purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(double totalAmt) {
		this.totalAmt = totalAmt;
	}
	
	public String getcalculatediscount()
	{
		return caldiscount;
	}
	
	public void setcalculatediscount(String caldiscount)
	{
		this.caldiscount=caldiscount;
	}
	
	public String getStartDisQty()
	{
		return startDisQty;
	}
	
	public void setStartDisQty(String startDisQty)
	{
		this.startDisQty=startDisQty;
	}
	
	public String getEndDisQty()
	{
		return endDisQty;
	}
	
	public void setEndDisQty(String endDisQty)
	{
		this.endDisQty= endDisQty;
	}

	
}
