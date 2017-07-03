package bean.classes;

public class Discount 
{
	String stockNo;
	String discountPercent, startDisQty, endDisQty;
	
	public String getstockNo()
	{
		return stockNo;
	}
	public void setstockNo(String stockNo) 
	{
		this.stockNo = stockNo;
	}
	public String getdiscountPercent() 
	{
		return discountPercent;
	}
	public void setdiscountPercent(String discountPercent)
	{
		this.discountPercent = discountPercent;
	}
	public String getstartDisQty() 
	{
		return startDisQty;
	}
	public void setstartDisQty(String startDisQty) 
	{
		this.startDisQty = startDisQty;
	}
	public String getendDisQty() 
	{
		return endDisQty;
	}
	public void setendDisQty(String endDisQty)
	{
		this.endDisQty = endDisQty;
	}
	
	
}
