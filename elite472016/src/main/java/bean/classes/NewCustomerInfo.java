package bean.classes;

public class NewCustomerInfo 
{
	public String id,name, phone, address, contactPerson,zone,customerCategory;
	
	public NewCustomerInfo(String id,String name, String phone,
			String address, String contactPerson,String zone,String customerCategory) {
		super();
		this.id= id;
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.contactPerson = contactPerson;
		this.zone=zone;
		this.customerCategory=customerCategory;
	}

	public String getCustomerCategory() {
		return customerCategory;
	}

	public void setCustomerCategory(String customerCategory) {
		this.customerCategory = customerCategory;
	}

	public String getid()
	{
		return id;
	}
	public void setid(String id)
	{
		this.id= id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String info) {
		this.phone = info;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

}
