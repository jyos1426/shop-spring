package com.my.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.my.sql.MyConnection;
import com.my.vo.Customer;

public class CustomerDAOOracle implements CustomerDAO {	
	
//	public CustomerDAOOracle() throws ClassNotFoundException{		
//	// 1. JDBC 드라이버(ojdbc14.jar) 로드 - 생성자 호출 시
//		Class.forName("oracle.jdbc.driver.OracleDriver");
//	}
	
	@Override
	public void insert(Customer c) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		// 2. DB와의 연결 - 매번 메소드 호출시
		con = MyConnection.getConnection();
		
		// 3. SQL 구문을 송신
		String insertSQL = "INSERT INTO customer VALUES(?,?,?)";
		try{
			pstmt = con.prepareStatement(insertSQL);
			pstmt.setString(1, c.getId());
			pstmt.setString(2, c.getPassword());
			pstmt.setString(3, c.getName());		
			pstmt.executeUpdate();
		}catch(SQLException e){
			int errorCode = e.getErrorCode();
			if(errorCode == 1){		//오라클에서 PK중복인 경우 오류 1번을 의미
				throw new Exception("이미 존재하는 아이디입니다.");
			}else{
				throw e;
			}
		}			
		// 4. 송신결과 수신(생략)		
		// 5. 연결닫기				
		MyConnection.close(con,pstmt);
	}
	
	@Override
	public List<Customer> selectAll() throws Exception {
		Connection con = null;
		Statement stmt = null;		
		ResultSet rs = null;
		
		con = MyConnection.getConnection();		
		String selectallSQL = "SELECT * FROM customer ORDER BY id";
		
		stmt = con.createStatement();
		rs = stmt.executeQuery(selectallSQL);		
		ArrayList<Customer> list = new ArrayList<>();
	
		while( rs.next() ){		
			list.add(new Customer(rs.getString("id"),
									rs.getString("password"),
									rs.getString("name")
									));
		}
		MyConnection.close(con,stmt,rs);
		return list;
	}

	@Override
	public Customer selectById(String id) throws Exception {
		Connection con = null;
		Statement stmt = null;		
		ResultSet rs = null;
		
		con = MyConnection.getConnection();		
		String selectIDSQL = "SELECT * FROM customer WHERE id ='"+id+"'";
		stmt = con.createStatement();
		rs = stmt.executeQuery(selectIDSQL);	
		
		Customer c = null;
		if (rs.next()){
			c = new Customer(rs.getString("id"),rs.getString("password"),rs.getString("name"));
		}		
		MyConnection.close(con,stmt,rs);
		
		return c;
	}

	@Override
	public List<Customer> selectByName(String name) throws Exception {
		Connection con = null;
		PreparedStatement pstmt = null;		
		ResultSet rs = null;
		
		con = MyConnection.getConnection();		
		String selectNameSQL = "SELECT * FROM customer WHERE name = ?";
		pstmt = con.prepareStatement(selectNameSQL);
		pstmt.setString(1, name);
		rs = pstmt.executeQuery();	
		
		ArrayList<Customer> list = new ArrayList<>();
		while( rs.next() ){		
			list.add(new Customer(rs.getString("id"),
									rs.getString("password"),
									rs.getString("name")
									));
		}
		MyConnection.close(con,pstmt,rs);
		return list;
	}

}
