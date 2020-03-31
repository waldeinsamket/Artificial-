import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * Servlet implementation class CourseSe
 */
@WebServlet("/CourseSe")
public class CourseSe extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CourseSe() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("UTF-8");// 让浏览器用utf-8解析
		response.setHeader("Content-type", "text/html;charset=UTF-8");// 防止乱码
		PrintWriter out = response.getWriter();

		// 连接数据库
		Connection con = null;
		ResultSet result;
		String sql;
		String url = "jdbc:sqlserver://localhost:1433;DatabaseName=Select"; // ?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
		String user = "Course";
		String pass = "0520english";
		Statement stmt;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); // 加载驱动
			con = DriverManager.getConnection(url, user, pass);
		} catch (Exception e) {
			System.out.println(e);
		}

		// 所需参数
		String st;
		String hsid;
		String name;
		String Sno;
		String Sname;
		String Spw;
		String Tno;
		String Tname;
		String Tpw;
		String Mno;
		String Mname;
		String Mpw;
		String Cno;
		String Cname;
		String Ctime;
		String Cplace;
		String attence;
		int grade;

		HttpSession hs = request.getSession(true);
		hs.setMaxInactiveInterval(20); // 修改session的存在时间
		// 判断请求来自哪个表单
		String type = request.getParameter("type");

		// 处理Login的内容
		if (type.equals("Login")) {
			// 获取参数
			st = new String(request.getParameter("radio").getBytes("iso-8859-1"), "UTF-8");// 中文传值出现乱码
			String us = request.getParameter("us");
			String pw = request.getParameter("pw");
			try {
				if (st.equals("学生"))
					sql = "select count(*)  count from Student where Sno='" + us + "' and  Spw='" + pw + "'";
				else if (st.equals("教师"))
					sql = "select count(*) count from Teacher where Tno='" + us + "' and  Tpw='" + pw + "'";
				else
					sql = "select count(*) count from Maneger where Mno='" + us + "' and  Mpw='" + pw + "'";
				stmt = (Statement) con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				System.out.println(sql);
				while (rs.next()) {
					int sum = rs.getInt("count");
					if (sum == 1) {
						hsid = hs.getId();
						hs.setAttribute("us", us);
						if (st.equals("学生"))
							response.sendRedirect("Stulogin.html");
						else if (st.equals("教师"))
							response.sendRedirect("Tealogin.html");
						else
							response.sendRedirect("Manelogin.html");
					} else
						out.println(
								"<script type='text/javascript'>alert('输入的账户或密码不正确！');location.href('Login.jsp');</script>");
				}
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}
		hs = request.getSession();
		// 处理Select的内容
		if (type.equals("Select")) {
			// 获取参数
			Sno = (String) hs.getAttribute("us");
			Cno = request.getParameter("checkbox");
			try {
				sql = "select count(*) count from  SC  where Sno='" + Sno + "' and  Cno='" + Cno + "'";
				stmt = (Statement) con.createStatement();
				System.out.print(sql);
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					int sum = rs.getInt("count");
					if (sum == 1)
						out.println(
								"<script type='text/javascript'>alert('您已选过该课程，请不要重复选课！');location.href('Select.jsp');</script>");
					else {
						sql = "insert into SC values('" + Sno + "','" + Cno + "',0)";
						System.out.print(sql);
						stmt = (Statement) con.createStatement();
						if (stmt.executeUpdate(sql) == 1)
							;
						out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + "<head>\r\n"
								+ "<meta  charset=\"utf-8\" />\r\n" + "<style type=\"text/css\">\r\n" + "body {\r\n"
								+ "	background-image: url(image1.jpg);\r\n" + "}\r\n" + "</style>\r\n" + "</head>\r\n"
								+ "\r\n" + "<body leftmargin=\"150\">\r\n" + "<a><button>选课成功!!!</button></a> "
								+ "</body>\r\n" + "</html>");
					}
				}
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}
		hs = request.getSession();
		// 处理StuLook的内容
		Sno = (String) hs.getAttribute("us");
		if (type.equals("StuLook")) {
			try {
				sql = "select  * from  Course where  Cno in(select Cno from SC where Sno='" + Sno + "')";
				System.out.println(sql);
				stmt = (Statement) con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				out.println("<html>\r\n" + "<head>\r\n" + "<meta charset=\"utf-8\">\r\n"
						+ "<title>Insert title here</title>\r\n" + "<style type=\"text/css\">\r\n" + "body {\r\n"
						+ "	background-image: url(image1.jpg);\r\n" + "	margin-left: 50px;\r\n" + "}\r\n" +

						"</style>\r\n" + "</head>\r\n" + "<body>\r\n" + "<form method=\"post\" action=\"CourseSe\">\r\n"
						+ "   <input type=\"hidden\" name=\"type\" value=\"StuLook\" />\r\n" + "  <h3>\r\n"
						+ "    <input type=\"hidden\" name=\"type\" value=\"Select\" />\r\n"
						+ "    &#23454;&#39564;&#35838;&#31243;&#20449;&#24687;\r\n" + "  </h3>\r\n"
						+ "<table width=\"690\" height=\"175\" border=\"0\" >" + "  <tbody  align=\"center\">\r\n"
						+ "      <tr   bgcolor=\"#E0EFEF\">\r\n"
						+ "        <td width=\"122\">&#23454;&#39564;&#35838;&#31243;&#21495;</td>\r\n"
						+ "        <td width=\"139\">&#23454;&#39564;&#35838;&#31243;&#21517;</td>\r\n"
						+ "        <td width=\"154\">&#19978;&#35838;&#26102;&#38388;</td>\r\n"
						+ "        <td width=\"126\">&#19978;&#35838;&#22320;&#28857;</td>\r\n"
						+ "        <td width=\"115\">&#19978;&#35838;&#32769;&#24072;</td>\r\n" + "      </tr>");
				while (rs.next()) {
					out.println("      <tr>\r\n" + "        <td>" + rs.getString("Cno") + "</td>\r\n" + "        <td>"
							+ rs.getString("Cname") + "</td>\r\n" + "        <td>" + rs.getString("Ctime") + "</td>\r\n"
							+ "        <td>" + rs.getString("Cplace") + "</td>\r\n" + "        <td>"
							+ rs.getString("Tno") + "</td>\r\n" + "      </tr>");

				}
				out.println("      <tr  align=\"right\">\r\n" + "      </tr>\r\n" + "    </tbody>\r\n" + "</table>\r\n"
						+ "</body>\r\n" + "</html>");
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}

		// 处理StuModify的内容
		if (type.equals("StuModify")) {
			Cno = request.getParameter("Cno");
			try {
				sql = "select count(*) count from  SC  where Sno='" + Sno + "' and  Cno='" + Cno + "'";
				stmt = (Statement) con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					int sum = rs.getInt("count");
					if (sum == 1) {
						sql = "delete from SC where Sno='" + Sno + "' and Cno='" + Cno + "'";
						System.out.println(sql);
						stmt = (Statement) con.createStatement();
						stmt.executeUpdate(sql);
						out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + "<head>\r\n"
								+ "<meta  charset=\"utf-8\" />\r\n" + "<style type=\"text/css\">\r\n" + "body {\r\n"
								+ "	background-image: url(image1.jpg);\r\n" + "}\r\n" + "</style>\r\n" + "</head>\r\n"
								+ "\r\n" + "<body leftmargin=\"150\">\r\n" + "<a><button>退课成功!!!</button></a> "
								+ "</body>\r\n" + "</html>");
					} else {
						out.println(
								"<script type='text/javascript'>alert('您未选过该课程，不需要退课！');location.href('StuModify.jsp');</script>");
					}
				}
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}

		hs = request.getSession();
		Tno = (String) hs.getAttribute("us");
		// 处理Attence的内容
		if (type.equals("Attence")) {
			System.out.println(Tno);
			Sno = request.getParameter("checkbox");
			try {
				sql = "Update TSC  set attence=1 where Sno='" + Sno + "' and Tno='" + Tno + "' "; // and Cno='"+Cno+"'";
				System.out.println(sql);
				stmt = (Statement) con.createStatement();
				stmt.executeUpdate(sql);
				out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + "<head>\r\n"
						+ "<meta  charset=\"utf-8\" />\r\n" + "<style type=\"text/css\">\r\n" + "body {\r\n"
						+ "	background-image: url(image1.jpg);\r\n" + "}\r\n" + "</style>\r\n" + "</head>\r\n" + "\r\n"
						+ "<body leftmargin=\"150\">\r\n" + "<a><button>考勤结束</button></a> " + "</body>\r\n"
						+ "</html>");
				stmt.close();
				con.close();

			} catch (Exception e1) {
				System.out.println(e1);
				out.println("<a><button>考勤失败</button></a> ");
			}
		}

		// 处理TeaModify的内容
		if (type.equals("TeaModify")) {
			Sno = request.getParameter("Sno");// 中文传值出现乱码
			Cno = request.getParameter("Cno");// 中文传值出现乱码
			grade = Integer.parseInt(request.getParameter("grade"));
			name = new String(request.getParameter("radio").getBytes("iso-8859-1"), "UTF-8");// 中文传值出现乱码
			try {
				sql = "select count(*) count from SC  where Sno='" + Sno + "'and Cno='" + Cno + "'";
				stmt = (Statement) con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					int sum = rs.getInt("count");
					if (sum == 1) {
						sql = "Update SC  set grade=" + grade + " where Sno='" + Sno + "'  and Cno='" + Cno + "'";
						stmt = (Statement) con.createStatement();
						stmt.executeUpdate(sql);
						if (name.equals("出勤"))
							sql = "Update TSC  set attence=1 where Sno='" + Sno + "' and Tno='" + Tno + "' and Cno='"
									+ Cno + "'";
						else
							sql = "Update TSC  set attence=0 where Sno='" + Sno + "' and Tno='" + Tno + "' and Cno='"
									+ Cno + "'";
						System.out.print(sql);
						stmt = (Statement) con.createStatement();
						stmt.executeUpdate(sql);
						out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + "<head>\r\n"
								+ "<meta  charset=\"utf-8\" />\r\n" + "<style type=\"text/css\">\r\n" + "body {\r\n"
								+ "	background-image: url(image1.jpg);\r\n" + "}\r\n" + "</style>\r\n" + "</head>\r\n"
								+ "\r\n" + "<body leftmargin=\"150\">\r\n" + "<a><button>修改成功</button></a> "
								+ "</body>\r\n" + "</html>");
					} else
						out.println("<a><button>不存在该选课记录，无法录入</button></a>");
				}
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}
		// 处理TeaInsert的内容
		if (type.equals("TeaInsert")) {
			try {
				 name= request.getParameter("teainsert");
				 String[]  strArr=name.split(",");
				 System.out.println(strArr);
				 for(int i=0; i< strArr.length; i++) {
					  Sno = strArr[i].split(";")[0];
					  Cno = strArr[i].split(";")[1];
					  grade=Integer.parseInt(request.getParameter("grade")); 
					  System.out.println(Tno);
		     		  sql="select count(*) count from SC  where Sno='"+Sno+"'and Cno='"+Cno+"'"; 
		              stmt=(Statement) con.createStatement();
		      		  ResultSet rs=stmt.executeQuery(sql);
    				  while(rs.next()) {
	  					int sum=rs.getInt("count");
	  					if(sum==1) {
				             sql="Update SC  set grade="+grade+" where Sno='"+Sno+"'  and Cno='"+Cno+"'";
							 System.out.print(sql);
							 stmt=(Statement) con.createStatement();
							 if(stmt.executeUpdate(sql)==1){
						      out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + 
								 		"<head>\r\n" + 
								 		"<meta  charset=\"utf-8\" />\r\n" + 
								 		"<style type=\"text/css\">\r\n" + 
								 		"body {\r\n" + 
								 		"	background-image: url(image1.jpg);\r\n" + 
								 		"}\r\n" + 
								 		"</style>\r\n" + 
								 		"</head>\r\n" + 
								 		"\r\n" + 
								 		"<body leftmargin=\"150\">\r\n" + 
								 		"<a><button>成绩录入成功</button></a> "+
								 		"</body>\r\n" + 
								 		"</html>");
							   }else 
									 out.println("<a><button>录入失败</button></a> ");	  
	  					}
	  					else
	     					 out.println("<a><button>不存在该选课记录，无法录入</button></a>");	         				
	  				  }	
    					 stmt.close();
    					 con.close();
				 }
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}
		// 处理TeaLook的内容
		if (type.equals("TeaLook")) {
			st = new String(request.getParameter("radio").getBytes("iso-8859-1"), "UTF-8");// 中文传值出现乱码
			if (st.equals("按成绩升序排列"))
				sql = "select *  from view_tea where Cno=(select Cno from Course where Tno='" + Tno
						+ "')  order by grade asc";
			else if (st.equals("按成绩降序排列"))
				sql = "select *  from view_tea where Cno=(select Cno from Course where Tno='" + Tno
						+ "')  order by grade desc";
			else
				sql = "select *  from view_tea where Cno=(select Cno from Course where Tno='" + Tno + "')";
			try {
				stmt = (Statement) con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				System.out.print(sql);
				out.println("<html>\r\n" + "<head>\r\n" + "<meta charset=\"utf-8\">\r\n"
						+ "<title>Insert title here</title>\r\n" + "<style type=\"text/css\">\r\n" + "body {\r\n"
						+ "	background-image: url(image1.jpg);\r\n" + "	margin-left: 70px;\r\n" + "}\r\n"
						+ "</style>\r\n" + "</head>\r\n" + "<body>\r\n" + "\r\n" + "  <h2>查看所任课程的相关信息  </h2>\r\n"
						+ "  <table width=\"542\" height=\"54\" border=\"0\" >\r\n"
						+ "    <tbody  align=\"center\">\r\n" + "      <tr   bgcolor=\"#E0EFEF\">\r\n"
						+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">学生号</td>\r\n"
						+ "        <td width=\"127\" align=\"center\" valign=\"middle\">学生姓名</td>\r\n"
						+ "        <td width=\"141\" align=\"center\" valign=\"middle\">上课时间</td>\r\n"
						+ "        <td width=\"127\" align=\"center\" valign=\"middle\">出勤状态</td>\r\n"
						+ "        <td width=\"127\" align=\"center\" valign=\"middle\">成绩</td>\r\n" + "      </tr>");
				while (rs.next()) {
					out.println("       <tr>\r\n"
							+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">"
							+ rs.getString("Sno") + "</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">" + rs.getString("Sname")
							+ "</td>\r\n" + "        <td width=\"141\" align=\"center\" valign=\"middle\">"
							+ rs.getString("Ctime") + "</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">" + rs.getString("attence")
							+ "</td>\r\n" + "        <td width=\"127\" align=\"center\" valign=\"middle\">"
							+ rs.getString("grade") + "</td>\r\n" + "      </tr>");
				}
				out.println("    </tbody>\r\n" + "  </table>\r\n" + "</body>\r\n" + "</html>");
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}
		hs = request.getSession();
		Mno = (String) hs.getAttribute("us");
		// 处理Create的内容
		if (type.equals("Create")) {
			st = new String(request.getParameter("radio").getBytes("iso-8859-1"), "UTF-8");// 中文传值出现乱码
			try {
				if (st.equals("添加学生信息")) {
					Sno = request.getParameter("Sno");
					Sname = new String(request.getParameter("Sname").getBytes("iso-8859-1"), "UTF-8");
					Spw = request.getParameter("Spw");
					sql = "insert into student  values('" + Sno + "','" + Sname + "','" + Spw + "')";
					System.out.println(sql);
				} else if (st.equals("添加教师信息")) {
					Tno = request.getParameter("Tno");
					Tname = new String(request.getParameter("Tname").getBytes("iso-8859-1"), "UTF-8");
					Tpw = request.getParameter("Tpw");
					sql = "insert into Teacher  values('" + Tno + "','" + Tname + "','" + Tpw + "')";
					System.out.println(sql);
				} else if (st.equals("添加实验课程")) {
					Cno = request.getParameter("Cno");
					Cname = new String(request.getParameter("Cname").getBytes("iso-8859-1"), "UTF-8");
					Ctime = new String(request.getParameter("Ctime").getBytes("iso-8859-1"), "UTF-8");
					Cplace = new String(request.getParameter("Cplace").getBytes("iso-8859-1"), "UTF-8");
					Tno = request.getParameter("Tno");
					sql = "insert into Course  values('" + Cno + "','" + Cname + "','" + Ctime + "','" + Cplace + "','"
							+ Tno + "')";
					System.out.println(sql);
				} else if (st.equals("添加成绩表")) {
					Sno = request.getParameter("Sno");
					Cno = request.getParameter("Cno");
					grade = Integer.parseInt(request.getParameter("grade"));
					sql = "insert into SC  values('" + Sno + "','" + Cno + "'," + grade + ")";
					System.out.println(sql);
				} else {
					Tno = Sno = request.getParameter("Tno");
					Sno = request.getParameter("Sno");
					Cno = request.getParameter("Cno");
					name = request.getParameter("radio");
					if (name.equals("出勤"))
						sql = "insert into TSC  values('" + Tno + "','" + Sno + "','" + Cno + "',1)";
					else
						sql = "insert into TSC  values('" + Tno + "','" + Sno + "','" + Cno + "',0)";
					System.out.println(sql);
				}
				System.out.println(sql);
				stmt = (Statement) con.createStatement();
				if (stmt.executeUpdate(sql) == 1) {
					out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + "<head>\r\n"
							+ "<meta  charset=\"utf-8\" />\r\n" + "<style type=\"text/css\">\r\n" + "body {\r\n"
							+ "	background-image: url(image1.jpg);\r\n" + "}\r\n" + "</style>\r\n" + "</head>\r\n"
							+ "\r\n" + "<body leftmargin=\"150\">\r\n" + "<a><button>添加成功</button></a> " + "</body>\r\n"
							+ "</html>");
				} else
					out.println("<a><button>该记录已存在！</button></a>");
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}

		// 处理ManeModify的内容
		if (type.equals("ManeModify")) {
			st = new String(request.getParameter("radio").getBytes("iso-8859-1"), "UTF-8");// 中文传值出现乱码
			try {
				if (st.equals("修改学生信息")) {
					Sno = request.getParameter("Sno");
					Sname = new String(request.getParameter("Sname").getBytes("iso-8859-1"), "UTF-8");
					Spw = request.getParameter("Spw");
					sql = "select count(*) count from  student  where Sno='" + Sno + "'";
					System.out.println(sql);
					stmt = (Statement) con.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next()) {
						int sum = rs.getInt("count");
						if (sum == 1)
							sql = "update student set  Sname='" + Sname + "',Spw='" + Spw + "' where Sno='" + Sno + "'";
						else
							out.println("<a><button>该记录不存在！</button></a>");
					}
				} else if (st.equals("修改教师信息")) {
					Tno = request.getParameter("Tno");
					Tname = new String(request.getParameter("Tname").getBytes("iso-8859-1"), "UTF-8");
					Tpw = request.getParameter("Tpw");
					sql = "select count(*) count from  Teacher  where Tno='" + Tno + "'";
					stmt = (Statement) con.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next()) {
						int sum = rs.getInt("count");
						if (sum == 1)
							sql = "update Teacher set  Tname='" + Tname + "', Tpw='" + Tpw + "' where Tno='" + Tno
									+ "'";
						else
							out.println("<a><button>该记录不存在！</button></a>");
					}
				} else if (st.equals("修改实验课程")) {
					Cno = request.getParameter("Cno");
					Cname = new String(request.getParameter("Cname").getBytes("iso-8859-1"), "UTF-8");
					Ctime = new String(request.getParameter("Ctime").getBytes("iso-8859-1"), "UTF-8");
					Cplace = new String(request.getParameter("Cplace").getBytes("iso-8859-1"), "UTF-8");
					Tno = request.getParameter("Tno");
					sql = "select count(*) count from  Course  where Tno='" + Tno + "' and Cno='" + Cno + "'";
					stmt = (Statement) con.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next()) {
						int sum = rs.getInt("count");
						if (sum == 1)
							sql = "update Course set  Cname='" + Cname + "' ,Ctime='" + Ctime + "' , Cplace='" + Cplace
									+ "' where  Tno='" + Tno + "' and Cno='" + Cno + "'";
						else
							out.println("<a><button>该记录不存在！</button></a>");
					}
				} else if (st.equals("修改成绩表")) {
					Sno = request.getParameter("Sno");
					Cno = request.getParameter("Cno");
					grade = Integer.parseInt(request.getParameter("grade"));
					sql = "select count(*) count from  SC  where Sno='" + Sno + "' and Cno='" + Cno + "'";
					stmt = (Statement) con.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next()) {
						int sum = rs.getInt("count");
						if (sum == 1)
							sql = "update SC set  grade=" + grade + " where Sno='" + Sno + "' and Cno='" + Cno + "'";
						else
							out.println("<a><button>该记录不存在！</button></a>");
					}
				} else {
					Tno = Sno = request.getParameter("Tno");
					Sno = request.getParameter("Sno");
					Cno = request.getParameter("Cno");
					name = request.getParameter("radio");
					sql = "select count(*) count from  TSC  where Tno='" + Tno + "' Sno='" + Sno + "' and Cno='" + Cno
							+ "'";
					stmt = (Statement) con.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next()) {
						int sum = rs.getInt("count");
						if (sum == 1) {
							if (name.equals("出勤"))
								sql = "Update TSC  set attence=1 where Sno='" + Sno + "' and Tno='" + Tno
										+ "' and Cno='" + Cno + "'";
							else
								sql = "Update TSC  set attence=0 where Sno='" + Sno + "' and Tno='" + Tno
										+ "' and Cno='" + Cno + "'";
						} else
							out.println("<a><button>该记录不存在！</button></a>");
					}
				}
				System.out.println(sql);
				stmt = (Statement) con.createStatement();
				if (stmt.executeUpdate(sql) == 1) {
					out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + "<head>\r\n"
							+ "<meta  charset=\"utf-8\" />\r\n" + "<style type=\"text/css\">\r\n" + "body {\r\n"
							+ "	background-image: url(image1.jpg);\r\n" + "}\r\n" + "</style>\r\n" + "</head>\r\n"
							+ "\r\n" + "<body leftmargin=\"150\">\r\n" + "<a><button>修改成功!!!</button></a> "
							+ "</body>\r\n" + "</html>");
				}
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}
		// 处理ManeDelete的内容
		if (type.equals("ManeDelete")) {
			st = new String(request.getParameter("radio").getBytes("iso-8859-1"), "UTF-8");// 中文传值出现乱码
			try {
				if (st.equals("删除学生信息")) {
					Sno = request.getParameter("Sno");
					sql = "select count(*) count from  student  where Sno='" + Sno + "'";
					System.out.println(sql);
					stmt = (Statement) con.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next()) {
						int sum = rs.getInt("count");
						if (sum == 1)
							sql = "delete from student  where Sno='" + Sno + "'";
						else
							out.println("<a><button>该记录不存在！</button></a>");
					}
				} else if (st.equals("删除教师信息")) {
					Tno = request.getParameter("Tno");
					sql = "select count(*) count from  Teacher  where Tno='" + Tno + "'";
					stmt = (Statement) con.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next()) {
						int sum = rs.getInt("count");
						if (sum == 1)
							sql = "delete from Teacher  where Tno='" + Tno + "'";
						else
							out.println("<a><button>该记录不存在！</button></a>");
					}
				} else if (st.equals("删除实验课程")) {
					Cno = request.getParameter("Cno");
					Tno = request.getParameter("Tno");
					sql = "select count(*) count from  Course  where Tno='" + Tno + "' and Cno='" + Cno + "'";
					stmt = (Statement) con.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next()) {
						int sum = rs.getInt("count");
						if (sum == 1)
							sql = "delete from Course where  Tno='" + Tno + "' and Cno='" + Cno + "'";
						else
							out.println("<a><button>该记录不存在！</button></a>");
					}
				} else if (st.equals("删除成绩表")) {
					Sno = request.getParameter("Sno");
					Cno = request.getParameter("Cno");
					sql = "select count(*) count from  SC  where Sno='" + Sno + "' and Cno='" + Cno + "'";
					stmt = (Statement) con.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next()) {
						int sum = rs.getInt("count");
						if (sum == 1)
							sql = "delete from SC where Sno='" + Sno + "' and Cno='" + Cno + "'";
						else
							out.println("<a><button>该记录不存在！</button></a>");
					}
				} else {
					Tno = Sno = request.getParameter("Tno");
					Sno = request.getParameter("Sno");
					Cno = request.getParameter("Cno");
					sql = "select count(*) count from  TSC  where Tno='" + Tno + "' Sno='" + Sno + "' and Cno='" + Cno
							+ "'";
					stmt = (Statement) con.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next()) {
						int sum = rs.getInt("count");
						if (sum == 1)
							sql = "delete from  TSC  where Tno='" + Tno + "' Sno='" + Sno + "' and Cno='" + Cno + "'";
						else
							out.println("<a><button>该记录不存在！</button></a>");
					}
				}
				System.out.println(sql);
				stmt = (Statement) con.createStatement();
				if (stmt.executeUpdate(sql) == 1) {
					out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + "<head>\r\n"
							+ "<meta  charset=\"utf-8\" />\r\n" + "<style type=\"text/css\">\r\n" + "body {\r\n"
							+ "	background-image: url(image1.jpg);\r\n" + "}\r\n" + "</style>\r\n" + "</head>\r\n"
							+ "\r\n" + "<body leftmargin=\"150\">\r\n" + "<a><button>删除成功!!!</button></a> "
							+ "</body>\r\n" + "</html>");
				}
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}
		// 处理ManeLook的内容
		if (type.equals("ManeLook")) {
			st = new String(request.getParameter("radio").getBytes("iso-8859-1"), "UTF-8");// 中文传值出现乱码
			if (st.equals("查询学生信息"))
				sql = "select *  from  Student";
			else if (st.equals("查询教师信息"))
				sql = "select *  from Teacher";
			else if (st.equals("查询实验课程"))
				sql = "select *  from Course";
			else if (st.equals("查询成绩表"))
				sql = "select *  from SC";
			else
				sql = "select *  from TSC";
			System.out.println(st + "   " + sql);
			try {
				stmt = (Statement) con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				out.println("<html>\r\n" + "<head>\r\n" + "<meta charset=\"utf-8\">\r\n"
						+ "<title>Insert title here</title>\r\n" + "<style type=\"text/css\">\r\n" + "body {\r\n"
						+ "	background-image: url(image1.jpg);\r\n" + "	margin-left: 70px;\r\n" + "}\r\n"
						+ "</style>\r\n" + "</head>\r\n" + "<body>\r\n" + "\r\n");

				if (st.equals("查询学生信息")) {
					out.println("  <h2>查询学生信息</h2>\r\n" + "  <table width=\"542\" height=\"54\" border=\"0\" >\r\n"
							+ "    <tbody  align=\"center\">\r\n" + "      <tr   bgcolor=\"#E0EFEF\">\r\n"
							+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">学生号</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">学生姓名</td>\r\n"
							+ "        <td width=\"141\" align=\"center\" valign=\"middle\">密码</td>\r\n"
							+ "      </tr>");
					while (rs.next()) {
						out.println("       <tr>\r\n"
								+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">"
								+ rs.getString("Sno") + "</td>\r\n"
								+ "        <td width=\"127\" align=\"center\" valign=\"middle\">"
								+ rs.getString("Sname") + "</td>\r\n"
								+ "        <td width=\"141\" align=\"center\" valign=\"middle\">" + rs.getString("Spw")
								+ "</td>\r\n" + "      </tr>");
					}
				} else if (st.equals("查询教师信息")) {
					out.println("  <h2>查询教师信息</h2>\r\n" + "  <table width=\"542\" height=\"54\" border=\"0\" >\r\n"
							+ "    <tbody  align=\"center\">\r\n" + "      <tr   bgcolor=\"#E0EFEF\">\r\n"
							+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">教师编号</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">教师姓名</td>\r\n"
							+ "        <td width=\"141\" align=\"center\" valign=\"middle\">密码</td>\r\n"
							+ "      </tr>");
					while (rs.next()) {
						out.println("       <tr>\r\n"
								+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">"
								+ rs.getString("Tno") + "</td>\r\n"
								+ "        <td width=\"127\" align=\"center\" valign=\"middle\">"
								+ rs.getString("Tname") + "</td>\r\n"
								+ "        <td width=\"141\" align=\"center\" valign=\"middle\">" + rs.getString("Tpw")
								+ "</td>\r\n" + "      </tr>");
					}
				} else if (st.equals("查询实验课程")) {

					out.println("  <h2>查询实验课程信息  </h2>\r\n" + "  <table width=\"542\" height=\"54\" border=\"0\" >\r\n"
							+ "    <tbody  align=\"center\">\r\n" + "      <tr   bgcolor=\"#E0EFEF\">\r\n"
							+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">课程号</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">课程名</td>\r\n"
							+ "        <td width=\"141\" align=\"center\" valign=\"middle\">上课时间</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">上课地点</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">上课教师</td>\r\n"
							+ "      </tr>");
					while (rs.next()) {
						out.println("       <tr>\r\n"
								+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">"
								+ rs.getString("Cno") + "</td>\r\n"
								+ "        <td width=\"127\" align=\"center\" valign=\"middle\">"
								+ rs.getString("Cname") + "</td>\r\n"
								+ "        <td width=\"141\" align=\"center\" valign=\"middle\">"
								+ rs.getString("Ctime") + "</td>\r\n"
								+ "        <td width=\"127\" align=\"center\" valign=\"middle\">"
								+ rs.getString("Cplace") + "</td>\r\n"
								+ "        <td width=\"127\" align=\"center\" valign=\"middle\">" + rs.getString("Tno")
								+ "</td>\r\n" + "      </tr>");
					}
				} else if (st.equals("查询成绩表")) {
					out.println("  <h2>查询成绩表</h2>\r\n" + "  <table width=\"542\" height=\"54\" border=\"0\" >\r\n"
							+ "    <tbody  align=\"center\">\r\n" + "      <tr   bgcolor=\"#E0EFEF\">\r\n"
							+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">学生号</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">课程号</td>\r\n"
							+ "        <td width=\"141\" align=\"center\" valign=\"middle\">成绩</td>\r\n"
							+ "      </tr>");
					while (rs.next()) {
						out.println("       <tr>\r\n"
								+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">"
								+ rs.getString("Sno") + "</td>\r\n"
								+ "        <td width=\"127\" align=\"center\" valign=\"middle\">" + rs.getString("Cno")
								+ "</td>\r\n" + "        <td width=\"141\" align=\"center\" valign=\"middle\">"
								+ rs.getString("grade") + "</td>\r\n" + "      </tr>");
					}
				} else {
					out.println("  <h2>查看考勤情况</h2>\r\n" + "  <table width=\"542\" height=\"54\" border=\"0\" >\r\n"
							+ "    <tbody  align=\"center\">\r\n" + "      <tr   bgcolor=\"#E0EFEF\">\r\n"
							+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">教师编号</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">学生号</td>\r\n"
							+ "        <td width=\"141\" align=\"center\" valign=\"middle\">课程号</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">出勤状态</td>\r\n"
							+ "      </tr>");
					while (rs.next()) {
						out.println("       <tr>\r\n"
								+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">"
								+ rs.getString("Tno") + "</td>\r\n"
								+ "        <td width=\"127\" align=\"center\" valign=\"middle\">" + rs.getString("Sno")
								+ "</td>\r\n" + "        <td width=\"141\" align=\"center\" valign=\"middle\">"
								+ rs.getString("Cno") + "</td>\r\n"
								+ "        <td width=\"127\" align=\"center\" valign=\"middle\">"
								+ rs.getString("attence") + "</td>\r\n" + "      </tr>");
					}
				}
				out.println("    </tbody>\r\n" + "  </table>\r\n" +
				// " <a href=\"bg.jsp\"><button>返回</button></a>\r\n" +
						"</body>\r\n" + "</html>");
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
