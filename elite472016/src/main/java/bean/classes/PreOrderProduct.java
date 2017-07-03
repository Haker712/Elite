package bean.classes;

public class PreOrderProduct 
{

	String categoryId, categoryName, groupId, productId, productName, totalQty = "0", sellingPrice, purchasePrice, groupName, discount, caldiscount= "0", totalAmt="0",startDisQty,endDisQty,discountType,remainingQty,orderQty="0", tempOrderQty ="0";


	public String getOrderQty()
	{
		return orderQty;
	}

	public void setOrderQty(String orderQty)
	{
		this.orderQty = orderQty;
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

	public String getTempOrderQty() {
		return tempOrderQty;
	}

	public void setTempOrderQty(String tempOrderQty) {
		this.tempOrderQty = tempOrderQty;
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

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(String totalAmt) {
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
