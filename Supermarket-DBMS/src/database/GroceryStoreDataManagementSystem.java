package database;

import java.util.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.FileInputStream;

public class GroceryStoreDataManagementSystem {

	public static void main(String[] args) throws Exception
	{
		// Load database configuration
		Properties props = new Properties();
		FileInputStream fis = new FileInputStream("config.properties");
		props.load(fis);
		
		String dbUrl = props.getProperty("db.url");
		String dbUser = props.getProperty("db.user");
		String dbPassword = props.getProperty("db.password");
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
		CallableStatement cst;
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		int ch = 0;
		Scanner sc = new Scanner(System.in);
		System.out.println("**WELCOME TO DS-MART GROCERY STORE**");
		while(ch!=10)
		{
		System.out.println("Choose an option:");
		System.out.println("1. Place Order");
		System.out.println("2. Order Stock from Supplier");
		System.out.println("3. Add Customer");
		System.out.println("4. Add Employee");
		System.out.println("5. Add Supplier");
		System.out.println("6. Add Online Partner");
		System.out.println("7. Remove Employee");
		System.out.println("8. Reset OrderId (New Day) ");
		System.out.println("9. Queries");
		System.out.println("10. Exit");
		ch = sc.nextInt();
		switch(ch)
		{
		case 1:
			System.out.print("Customer Phone number: ");
			long phone = sc.nextLong();
			cst = con.prepareCall("{? = call FIND_CUSTOMER_ID(?)}");
			cst.setLong(2,phone);
			cst.registerOutParameter(1,Types.VARCHAR);
			cst.execute();
			String id = cst.getString(1);
			if(id.equals("C0000"))
			{
				System.out.println("Customer not found.");
				System.out.println("For adding customer.");
				sc.nextLine();
				System.out.print("Name: ");
				String name = sc.nextLine();
				System.out.print("Email: ");
				String email = sc.next();
				cst = con.prepareCall("{? = call ADD_CUSTOMER(?,?,?)}");
				cst.setString(2,name);
				cst.setString(3,email);
				cst.setLong(4,phone);
				cst.registerOutParameter(1,Types.INTEGER);
				cst.execute();
				if(cst.getInt(1)==0)
				{
					System.out.println("Invalid Values");
					continue;
				}
				else
					System.out.println("Customer added successfully");
			}
			cst = con.prepareCall("{? = call ADD_ORDER(?,?,?)}");
			cst.setString(4,id);
			System.out.println("Choose the order method:");
			System.out.println("1. Offline");
			System.out.println("2. Online");
			int method = sc.nextInt();
			switch(method)
			{
			case 1:
				System.out.print("Enter empcode: ");
				cst.setString(2,sc.next());
				cst.setNull(3, Types.VARCHAR);
				break;
			case 2:
				System.out.print("Enter online partner name: ");
				cst.setString(3,sc.next());
				cst.setNull(2,Types.VARCHAR);
				break;
			}
			cst.registerOutParameter(1,Types.CHAR);
			cst.execute();
			String order_id = cst.getString(1);
			System.out.print("Enter the number of diffrent items: ");
			int n = sc.nextInt();
			float total_price = 0;
			for(int i=1;i<=n;i++)
			{
				System.out.println("Item "+i+":-");
				sc.nextLine();
				System.out.print("Name: ");
				String name = sc.nextLine();
				System.out.println("Quantity: ");
				int quantity = sc.nextInt();
				cst = con.prepareCall("{? = call ADD_ITEM_ORDERED(?,?,?)}");
				cst.registerOutParameter(1,Types.CHAR);
				cst.setString(2,order_id);
				cst.setString(3,name);
				cst.setInt(4,quantity);
				cst.execute();
				float price = cst.getFloat(1);
				if(price == 0) System.out.println("Stock Not available");
				total_price+=price;
			}
			System.out.println("Total Cost = " + total_price);
			break;
		case 2:
			//Later: Check if supplier name is correct
			sc.nextLine();
			System.out.print("Supplier Name: ");
			String supp_name = sc.nextLine();
			System.out.print("Enter number of diffrent Items:");
			n = sc.nextInt();
			for(int i=1;i<=n;i++)
			{
				System.out.println("Item "+i+":");
			System.out.print("Batch number: ");
			String batch = sc.next();
			sc.nextLine();
			System.out.print("Item Name: ");
			String item_name = sc.nextLine();
			System.out.print("MFD (in DD-MM-YYYY format): ");
			java.sql.Date mfd = new java.sql.Date(dateFormat.parse(sc.next()).getTime());
			System.out.print("EXP Date (in DD-MM-YYYY format): ");
			java.sql.Date exp = new java.sql.Date(dateFormat.parse(sc.next()).getTime());
			System.out.print("Cost Price: ");
			float cp = sc.nextFloat();
			System.out.print("Selling Price: ");
			float sp = sc.nextFloat();
			System.out.print("Quantity: ");
			int quantity = sc.nextInt();
			sc.nextLine();
			System.out.print("Manufacturer Name: ");
			String manu = sc.nextLine();
			do {
				
				cst = con.prepareCall("{call SUPPLY_ORDER(?,?,?,?,?,?,?,?,?)}");
				cst.setString(1,supp_name);
				cst.setString(2,batch);
				cst.setString(3,item_name);
				cst.setDate(4,mfd);
				cst.setDate(5,exp);
				cst.setFloat(6,cp);
				cst.setFloat(7,sp);
				cst.setInt(8,quantity);
				cst.registerOutParameter(9,Types.VARCHAR);
				cst.setString(9,manu);
				cst.execute();
				System.out.println(cst.getString(9));
				if(cst.getString(9).equals("-1")) {
					System.out.println("New Manufacturer, Enter its details");
					add_Manufacturer();
					sc.nextLine();
					continue;
			}
			cst.close();
			}while(false);
			}
			System.out.println("Order Placed");
			break;
		case 3:
			System.out.println("Name: ");
			sc.nextLine();
			String name = sc.nextLine();
			System.out.println("Mobile No: ");
			phone = sc.nextLong();
			System.out.println("Email: ");
			String email = sc.next();
			cst = con.prepareCall("{? = call add_customer(?,?,?)}");
			cst.registerOutParameter(1,Types.INTEGER);
			cst.setString(2,name);
			cst.setString(3,email);
			cst.setLong(4,phone);
			cst.execute();
			if(cst.getInt(1)==0)
				System.out.println("Invalid Values");
			else
				System.out.println("Customer added successfully");
			break;
		case 4:
			System.out.print("Fist Name: ");
			String f_name = sc.next();
			System.out.print("Middle Name: ");
			String m_name = sc.next();
			System.out.print("Last Name: ");
			String l_name = sc.next();
			System.out.print("Sex (M/F/O): ");
			String sex = Character.toString(sc.next().charAt(0));
			System.out.print("Mobile No: ");
			phone = sc.nextLong();
			System.out.print("Email: ");
			email = sc.next();
			sc.nextLine();
			System.out.println("Address Line 1: ");
			String add_l1 = sc.nextLine();
			System.out.println("City: ");
			String city = sc.nextLine();
			System.out.println("State: ");
			String state = sc.nextLine();
			System.out.println("Choose a country: ");
			countries();
			System.out.println("Enter the name of country: ");
			String country = sc.nextLine().toUpperCase();
			Integer pincode = null;
			if(country.equals("INDIA"))
			{
				System.out.println("Pincode: ");
				pincode = sc.nextInt();
			}
			cst = con.prepareCall("{? = call add_employee(?,?,?,?,?,?,?,?,?,?,?)}");
			cst.registerOutParameter(1,Types.INTEGER);
			cst.setString(2,f_name);
			cst.setString(3,m_name);
			cst.setString(4,l_name);
			cst.setString(5,sex);
			cst.setLong(6,phone);
			cst.setString(7,email);
			cst.setString(8,add_l1);
			cst.setString(9,city);
			cst.setString(10,state);
			cst.setInt(11,pincode);
			cst.setString(12,country);
			cst.execute();
			if(cst.getInt(1)==0)
				System.out.println("Invalid Values");
			else
				System.out.println("Employee Addad Successfully");
			cst.close();
			break;
		case 5: 
		System.out.println("Name: ");
		sc.nextLine();
		name = sc.nextLine();
		System.out.println("Mobile No: ");
		phone = sc.nextLong();
		System.out.println("Email: ");
		email = sc.next();
		sc.nextLine();
		System.out.println("Address Line 1: ");
		add_l1 = sc.nextLine();
		System.out.println("City: ");
		city = sc.nextLine();
		System.out.println("State: ");
		state = sc.nextLine();
		System.out.println("Choose a country: ");
		countries();
		System.out.println("Enter the name of country: ");
		country = sc.nextLine().toUpperCase();
		pincode = null;
		if(country.equals("INDIA"))
		{
			System.out.println("Pincode: ");
			pincode = sc.nextInt();
		}
		cst = con.prepareCall("{ ? = call add_supplier(?,?,?,?,?,?,?,?)}");
		cst.registerOutParameter(1,Types.INTEGER);
		cst.setString(2,name);
		cst.setLong(3,phone);
		cst.setString(4,email);
		cst.setString(5,add_l1);
		cst.setString(6,city);
		cst.setString(7,state);
		cst.setInt(8,pincode);
		cst.setString(9,country);
		cst.execute();
		if(cst.getInt(1)==0)
			System.out.println("Invalid Values");
		else
			System.out.println("Supplier added Successfully");
			break;
		case 6:
			System.out.println("Name: ");
			sc.nextLine();
			name = sc.nextLine();
			System.out.println("Comission% = ");
			float comission = sc.nextFloat();
			cst = con.prepareCall("{? = call add_online_partner(?,?)}");
			cst.registerOutParameter(1,Types.INTEGER);
			cst.setString(2,name);
			cst.setFloat(3,comission);
			cst.execute();
			if(cst.getInt(1)==0)
				System.out.println("Invalid Values");
			else
				System.out.println("Online Partner Added");
			break;
		case 7:
			System.out.println("Empcode: ");
			String empcode = sc.next();
			cst = con.prepareCall("{call remove_employee(?)}");
			cst.setString(1,empcode);
			cst.execute();
			System.out.println("Employee Removed 👍");
			break;
		case 8:
			cst = con.prepareCall("{call reset_orderid}");
			cst.execute();
			System.out.println("Reset Orderid Done");
			break;
		case 9: 
			queries();
			break;
		case 10: break;
		default: System.out.println("Invalind Input");
		}
		}
		con.close();
		sc.close();
	}
	private static void add_Manufacturer() throws Exception
	{
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE","system","sql25");
		CallableStatement cst;
		Scanner sc = new Scanner(System.in);
		System.out.print("Name: ");
		String name = sc.nextLine();
		System.out.print("Mobile No: ");
		long phone = sc.nextLong();
		System.out.print("Email: ");
		String email = sc.next();
		sc.nextLine();
		System.out.print("Address Line 1: ");
		String add_l1 = sc.nextLine();
		System.out.print("City: ");
		String city = sc.nextLine();
		System.out.print("State: ");
		String state = sc.nextLine();
		System.out.println("Choose a country: ");
		countries();
		System.out.print("Enter the name of country: ");
		String country = sc.nextLine().toUpperCase();
		Integer pincode = null;
		if(country.equals("INDIA"))
		{
			System.out.print("Pincode: ");
			pincode = sc.nextInt();
		}
		cst = con.prepareCall("{? = call add_manufacturer(?,?,?,?,?,?,?,?)}");
		cst.registerOutParameter(1,Types.INTEGER);
		cst.setString(2,name);
		cst.setLong(3,phone);
		cst.setString(4,email);
		cst.setString(5,add_l1);
		cst.setString(6,city);
		cst.setString(7,state);
		cst.setInt(8,pincode);
		cst.setString(9,country);
		cst.execute();
		if(cst.getInt(1)==0)
		{
			System.out.println("Invalid Values");
			add_Manufacturer();
		}
		else
			System.out.println("Manufacturer added");
		sc.close();
	}
	private static void queries() throws Exception
	{
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE","system","sql25");
		Statement smt = con.createStatement();
		char ch = ' ';
		Scanner sc = new Scanner(System.in);
		while(ch!='h') {
		System.out.println("Select any query");
		System.out.println("a. Searching for employee who has made the most number of orders");
		System.out.println("b. Find the total stock available of a particular item (according to item name)");
		System.out.println("c. Searching for all the expired or due to expire in 1 month items");
		System.out.println("d. Searching for Suppliers that sell a particular item");
		System.out.println("e. Search for out of stock and less then 50 stock items in increasing order of stock");
		System.out.println("f. Search for employees who live at the same city/state/country/pincode");
		System.out.println("g. Seach employees through city/state/country/pincode");
		System.out.println("h. Exit queries");
		ch = sc.next().charAt(0);
		switch(ch)
		{
		case 'a':
			ResultSet resultSet = smt.executeQuery("SELECT E.FIRST_NAME, E.LAST_NAME, COUNT(O.ORDERID) AS NUM_ORDERS "
					+ "FROM EMPLOYEE E JOIN ORDERS O ON E.EMPCODE = O.EMPCODE "
					+ "GROUP BY E.FIRST_NAME, E.LAST_NAME "
					+ "ORDER BY COUNT(O.ORDERID) DESC "
					+ "FETCH FIRST 1 ROW ONLY");
			resultSet.next();
			System.out.println("The Employee with most orders is: " + resultSet.getString("first_name")+ " " + resultSet.getString("last_name"));
			System.out.println("with no of orders = " + resultSet.getString("NUM_ORDERS"));
			break;
		case 'b':
			System.out.println("Enter the Item Name");
			sc.nextLine();
			resultSet = smt.executeQuery("SELECT ITEM_NAME, SUM(STOCK) AS TOTAL_STOCK FROM ITEM WHERE ITEM_NAME = '"+sc.nextLine()+"' "
					+ "GROUP BY ITEM_NAME");
			resultSet.next();
			System.out.println("Total Stock is: " + resultSet.getInt("TOTAL_STOCK"));
			break;
		case 'c':
			resultSet = smt.executeQuery("SELECT item_name FROM ITEM WHERE EXP_DATE <= ADD_MONTHS(SYSDATE, 1)");
			System.out.println("The Items Are:");
			while(resultSet.next())
				System.out.println(resultSet.getString("Item_name"));
			break;
		case 'd':
			System.out.println("Enter the Item Name");
			sc.nextLine();
			resultSet = smt.executeQuery("SELECT DISTINCT S.NAME AS SUPPLIER_NAME "
					+ "FROM SUPPLY_TRANSACTIONS ST "
					+ "INNER JOIN SUPPLIER S ON ST.SUPPLIER_NAME = S.NAME "
					+ "INNER JOIN ITEM I ON ST.BATCHNO = I.BATCH_NO "
					+ "WHERE I.ITEM_NAME = '"+sc.nextLine()+"'");
			System.out.println("The suppliers are:");
			while(resultSet.next())
				System.out.println(resultSet.getString("SUPPLIER_NAME"));
			break;
		case 'e':
			resultSet = smt.executeQuery("SELECT BATCH_NO, ITEM_NAME, STOCK "
					+ "FROM ITEM "
					+ "WHERE STOCK < 50 "
					+ "ORDER BY STOCK ASC");
			System.out.println("BATCH_NO\tITEM_NAME\tSTOCK");
			while(resultSet.next())
				System.out.println(resultSet.getString("BATCH_NO")+"\t\t"+resultSet.getString("ITEM_NAME")+"\t\t"+resultSet.getString("STOCK"));
			break;
		case 'f':
			System.out.println("Choose one of CITY/STATE/COUNTRY/PINCODE");
			String factor = sc.next();
			resultSet = smt.executeQuery("SELECT E1.EMPCODE AS EMPCODE1, E2.EMPCODE AS EMPCODE2, A."+factor+" as factor "
					+ "FROM EMPLOYEE E1\r\n"
					+ "JOIN EMPLOYEE E2 ON E1.ADDRESSID = E2.ADDRESSID AND E1.EMPCODE < E2.EMPCODE "
					+ "JOIN ADDRESS A ON E1.ADDRESSID = A.ID");
			System.out.println("2 employees having the same "+factor+":");
			while(resultSet.next())
				System.out.println(resultSet.getString("EMPCODE1")+"\t"+resultSet.getString("EMPCODE2")+"\t"+resultSet.getString("factor"));
			break;
		case 'g':
			System.out.println("Choose one of CITY/STATE/COUNTRY/PINCODE");
			factor = sc.next();
			System.out.println("Enter which "+factor+":");
			sc.nextLine();
			resultSet = smt.executeQuery("SELECT EMPCODE, FIRST_NAME, MIDDLE_NAME, LAST_NAME\r\n"
					+ "FROM EMPLOYEE E JOIN ADDRESS A ON E.ADDRESSID = A.ID\r\n"
					+ "WHERE A."+factor+" = '"+sc.nextLine()+"'");
			while(resultSet.next()) {
				System.out.println(resultSet.getString("EMPCODE")+"\t"+resultSet.getString("FIRST_NAME")+" "+resultSet.getString("MIDDLE_NAME")+" "+resultSet.getString("LAST_NAME"));
			}
			break;
		case 'h':break;
		default: System.out.println("Invalid Input");
		}}
	}
	private static void countries()
	{
		System.out.println("AFGHANISTAN");
		System.out.println("ALBANIA");
		System.out.println("ALGERIA");
		System.out.println("ANDORRA");
		System.out.println("ANGOLA");
		System.out.println("ANTIGUA AND BARBUDA");
		System.out.println("ARGENTINA");
		System.out.println("ARMENIA");
		System.out.println("AUSTRALIA");
		System.out.println("AUSTRIA");
		System.out.println("AZERBAIJAN");
		System.out.println("BAHAMAS");
		System.out.println("BAHRAIN");
		System.out.println("BANGLADESH");
		System.out.println("BARBADOS");
		System.out.println("BELARUS");
		System.out.println("BELGIUM");
		System.out.println("BELIZE");
		System.out.println("BENIN");
		System.out.println("BHUTAN");
		System.out.println("BOLIVIA");
		System.out.println("BOSNIA AND HERZEGOVINA");
		System.out.println("BOTSWANA");
		System.out.println("BRAZIL");
		System.out.println("BRUNEI");
		System.out.println("BULGARIA");
		System.out.println("BURKINA FASO");
		System.out.println("BURUNDI");
		System.out.println("CABO VERDE");
		System.out.println("CAMBODIA");
		System.out.println("CAMEROON");
		System.out.println("CANADA");
		System.out.println("CENTRAL AFRICAN REPUBLIC");
		System.out.println("CHAD");
		System.out.println("CHILE");
		System.out.println("CHINA");
		System.out.println("COLOMBIA");
		System.out.println("COMOROS");
		System.out.println("CONGO");
		System.out.println("COSTA RICA");
		System.out.println("CROATIA");
		System.out.println("CUBA");
		System.out.println("CYPRUS");
		System.out.println("CZECH REPUBLIC");
		System.out.println("DEMOCRATIC REPUBLIC OF THE CONGO");
		System.out.println("DENMARK");
		System.out.println("DJIBOUTI");
		System.out.println("DOMINICA");
		System.out.println("DOMINICAN REPUBLIC");
		System.out.println("ECUADOR");
		System.out.println("EGYPT");
		System.out.println("EL SALVADOR");
		System.out.println("EQUATORIAL GUINEA");
		System.out.println("ERITREA");
		System.out.println("ESTONIA");
		System.out.println("ESWATINI");
		System.out.println("ETHIOPIA");
		System.out.println("FIJI");
		System.out.println("FINLAND");
		System.out.println("FRANCE");
		System.out.println("GABON");
		System.out.println("GAMBIA");
		System.out.println("GEORGIA");
		System.out.println("GERMANY");
		System.out.println("GHANA");
		System.out.println("GREECE");
		System.out.println("GRENADA");
		System.out.println("GUATEMALA");
		System.out.println("GUINEA");
		System.out.println("GUINEA-BISSAU");
		System.out.println("GUYANA");
		System.out.println("HAITI");
		System.out.println("HONDURAS");
		System.out.println("HUNGARY");
		System.out.println("ICELAND");
		System.out.println("INDIA");
		System.out.println("INDONESIA");
		System.out.println("IRAN");
		System.out.println("IRAQ");
		System.out.println("IRELAND");
		System.out.println("ISRAEL");
		System.out.println("ITALY");
		System.out.println("JAMAICA");
		System.out.println("JAPAN");
		System.out.println("JORDAN");
		System.out.println("KAZAKHSTAN");
		System.out.println("KENYA");
		System.out.println("KIRIBATI");
		System.out.println("KUWAIT");
		System.out.println("KYRGYZSTAN");
		System.out.println("LAOS");
		System.out.println("LATVIA");
		System.out.println("LEBANON");
		System.out.println("LESOTHO");
		System.out.println("LIBERIA");
		System.out.println("LIBYA");
		System.out.println("LIECHTENSTEIN");
		System.out.println("LITHUANIA");
		System.out.println("LUXEMBOURG");
		System.out.println("MADAGASCAR");
		System.out.println("MALAWI");
		System.out.println("MALAYSIA");
		System.out.println("MALDIVES");
		System.out.println("MALI");
		System.out.println("MALTA");
		System.out.println("MARSHALL ISLANDS");
		System.out.println("MAURITANIA");
		System.out.println("MAURITIUS");
		System.out.println("MEXICO");
		System.out.println("MICRONESIA");
		System.out.println("MOLDOVA");
		System.out.println("MONACO");
		System.out.println("MONGOLIA");
		System.out.println("MONTENEGRO");
		System.out.println("MOROCCO");
		System.out.println("MOZAMBIQUE");
		System.out.println("MYANMAR");
		System.out.println("NAMIBIA");
		System.out.println("NAURU");
		System.out.println("NEPAL");
		System.out.println("NETHERLANDS");
		System.out.println("NEW ZEALAND");
		System.out.println("NICARAGUA");
		System.out.println("NIGER");
		System.out.println("NIGERIA");
		System.out.println("NORTH KOREA");
		System.out.println("NORTH MACEDONIA");
		System.out.println("NORWAY");
		System.out.println("OMAN");
		System.out.println("PAKISTAN");
		System.out.println("PALAU");
		System.out.println("PALESTINE");
		System.out.println("PANAMA");
		System.out.println("PAPUA NEW GUINEA");
		System.out.println("PARAGUAY");
		System.out.println("PERU");
		System.out.println("PHILIPPINES");
		System.out.println("POLAND");
		System.out.println("PORTUGAL");
		System.out.println("QATAR");
		System.out.println("ROMANIA");
		System.out.println("RUSSIA");
		System.out.println("RWANDA");
		System.out.println("SAINT KITTS AND NEVIS");
		System.out.println("SAINT LUCIA");
		System.out.println("SAINT VINCENT AND THE GRENADINES");
		System.out.println("SAMOA");
		System.out.println("SAN MARINO");
		System.out.println("SAO TOME AND PRINCIPE");
		System.out.println("SAUDI ARABIA");
		System.out.println("SENEGAL");
		System.out.println("SERBIA");
		System.out.println("SEYCHELLES");
		System.out.println("SIERRA LEONE");
		System.out.println("SINGAPORE");
		System.out.println("SLOVAKIA");
		System.out.println("SLOVENIA");
		System.out.println("SOLOMON ISLANDS");
		System.out.println("SOMALIA");
		System.out.println("SOUTH AFRICA");
		System.out.println("SOUTH KOREA");
		System.out.println("SOUTH SUDAN");
		System.out.println("SPAIN");
		System.out.println("SRI LANKA");
		System.out.println("SUDAN");
		System.out.println("SURINAME");
		System.out.println("SWEDEN");
		System.out.println("SWITZERLAND");
		System.out.println("SYRIA");
		System.out.println("TAJIKISTAN");
		System.out.println("TANZANIA");
		System.out.println("THAILAND");
		System.out.println("TIMOR-LESTE");
		System.out.println("TOGO");
		System.out.println("TONGA");
		System.out.println("TRINIDAD AND TOBAGO");
		System.out.println("TUNISIA");
		System.out.println("TURKEY");
		System.out.println("TURKMENISTAN");
		System.out.println("TUVALU");
		System.out.println("UGANDA");
		System.out.println("UKRAINE");
		System.out.println("UNITED ARAB EMIRATES");
		System.out.println("UNITED KINGDOM");
		System.out.println("UNITED STATES OF AMERICA");
		System.out.println("URUGUAY");
		System.out.println("UZBEKISTAN");
		System.out.println("VANUATU");
		System.out.println("VATICAN CITY");
		System.out.println("VENEZUELA");
		System.out.println("VIETNAM");
		System.out.println("YEMEN");
		System.out.println("ZAMBIA");
		System.out.println("ZIMBABWE");

	}
}
