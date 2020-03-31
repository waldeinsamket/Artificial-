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
		response.setCharacterEncoding("UTF-8");// ���������utf-8����
		response.setHeader("Content-type", "text/html;charset=UTF-8");// ��ֹ����
		PrintWriter out = response.getWriter();

		// �������ݿ�
		Connection con = null;
		ResultSet result;
		String sql;
		String url = "jdbc:sqlserver://localhost:1433;DatabaseName=Select"; // ?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC
		String user = "Course";
		String pass = "0520english";
		Statement stmt;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); // ��������
			con = DriverManager.getConnection(url, user, pass);
		} catch (Exception e) {
			System.out.println(e);
		}

		// �������
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
		hs.setMaxInactiveInterval(20); // �޸�session�Ĵ���ʱ��
		// �ж����������ĸ���
		String type = request.getParameter("type");

		// ����Login������
		if (type.equals("Login")) {
			// ��ȡ����
			st = new String(request.getParameter("radio").getBytes("iso-8859-1"), "UTF-8");// ���Ĵ�ֵ��������
			String us = request.getParameter("us");
			String pw = request.getParameter("pw");
			try {
				if (st.equals("ѧ��"))
					sql = "select count(*)  count from Student where Sno='" + us + "' and  Spw='" + pw + "'";
				else if (st.equals("��ʦ"))
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
						if (st.equals("ѧ��"))
							response.sendRedirect("Stulogin.html");
						else if (st.equals("��ʦ"))
							response.sendRedirect("Tealogin.html");
						else
							response.sendRedirect("Manelogin.html");
					} else
						out.println(
								"<script type='text/javascript'>alert('������˻������벻��ȷ��');location.href('Login.jsp');</script>");
				}
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}
		hs = request.getSession();
		// ����Select������
		if (type.equals("Select")) {
			// ��ȡ����
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
								"<script type='text/javascript'>alert('����ѡ���ÿγ̣��벻Ҫ�ظ�ѡ�Σ�');location.href('Select.jsp');</script>");
					else {
						sql = "insert into SC values('" + Sno + "','" + Cno + "',0)";
						System.out.print(sql);
						stmt = (Statement) con.createStatement();
						if (stmt.executeUpdate(sql) == 1)
							;
						out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + "<head>\r\n"
								+ "<meta  charset=\"utf-8\" />\r\n" + "<style type=\"text/css\">\r\n" + "body {\r\n"
								+ "	background-image: url(image1.jpg);\r\n" + "}\r\n" + "</style>\r\n" + "</head>\r\n"
								+ "\r\n" + "<body leftmargin=\"150\">\r\n" + "<a><button>ѡ�γɹ�!!!</button></a> "
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
		// ����StuLook������
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

		// ����StuModify������
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
								+ "\r\n" + "<body leftmargin=\"150\">\r\n" + "<a><button>�˿γɹ�!!!</button></a> "
								+ "</body>\r\n" + "</html>");
					} else {
						out.println(
								"<script type='text/javascript'>alert('��δѡ���ÿγ̣�����Ҫ�˿Σ�');location.href('StuModify.jsp');</script>");
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
		// ����Attence������
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
						+ "<body leftmargin=\"150\">\r\n" + "<a><button>���ڽ���</button></a> " + "</body>\r\n"
						+ "</html>");
				stmt.close();
				con.close();

			} catch (Exception e1) {
				System.out.println(e1);
				out.println("<a><button>����ʧ��</button></a> ");
			}
		}

		// ����TeaModify������
		if (type.equals("TeaModify")) {
			Sno = request.getParameter("Sno");// ���Ĵ�ֵ��������
			Cno = request.getParameter("Cno");// ���Ĵ�ֵ��������
			grade = Integer.parseInt(request.getParameter("grade"));
			name = new String(request.getParameter("radio").getBytes("iso-8859-1"), "UTF-8");// ���Ĵ�ֵ��������
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
						if (name.equals("����"))
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
								+ "\r\n" + "<body leftmargin=\"150\">\r\n" + "<a><button>�޸ĳɹ�</button></a> "
								+ "</body>\r\n" + "</html>");
					} else
						out.println("<a><button>�����ڸ�ѡ�μ�¼���޷�¼��</button></a>");
				}
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}
		// ����TeaInsert������
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
								 		"<a><button>�ɼ�¼��ɹ�</button></a> "+
								 		"</body>\r\n" + 
								 		"</html>");
							   }else 
									 out.println("<a><button>¼��ʧ��</button></a> ");	  
	  					}
	  					else
	     					 out.println("<a><button>�����ڸ�ѡ�μ�¼���޷�¼��</button></a>");	         				
	  				  }	
    					 stmt.close();
    					 con.close();
				 }
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}
		// ����TeaLook������
		if (type.equals("TeaLook")) {
			st = new String(request.getParameter("radio").getBytes("iso-8859-1"), "UTF-8");// ���Ĵ�ֵ��������
			if (st.equals("���ɼ���������"))
				sql = "select *  from view_tea where Cno=(select Cno from Course where Tno='" + Tno
						+ "')  order by grade asc";
			else if (st.equals("���ɼ���������"))
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
						+ "</style>\r\n" + "</head>\r\n" + "<body>\r\n" + "\r\n" + "  <h2>�鿴���ογ̵������Ϣ  </h2>\r\n"
						+ "  <table width=\"542\" height=\"54\" border=\"0\" >\r\n"
						+ "    <tbody  align=\"center\">\r\n" + "      <tr   bgcolor=\"#E0EFEF\">\r\n"
						+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">ѧ����</td>\r\n"
						+ "        <td width=\"127\" align=\"center\" valign=\"middle\">ѧ������</td>\r\n"
						+ "        <td width=\"141\" align=\"center\" valign=\"middle\">�Ͽ�ʱ��</td>\r\n"
						+ "        <td width=\"127\" align=\"center\" valign=\"middle\">����״̬</td>\r\n"
						+ "        <td width=\"127\" align=\"center\" valign=\"middle\">�ɼ�</td>\r\n" + "      </tr>");
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
		// ����Create������
		if (type.equals("Create")) {
			st = new String(request.getParameter("radio").getBytes("iso-8859-1"), "UTF-8");// ���Ĵ�ֵ��������
			try {
				if (st.equals("���ѧ����Ϣ")) {
					Sno = request.getParameter("Sno");
					Sname = new String(request.getParameter("Sname").getBytes("iso-8859-1"), "UTF-8");
					Spw = request.getParameter("Spw");
					sql = "insert into student  values('" + Sno + "','" + Sname + "','" + Spw + "')";
					System.out.println(sql);
				} else if (st.equals("��ӽ�ʦ��Ϣ")) {
					Tno = request.getParameter("Tno");
					Tname = new String(request.getParameter("Tname").getBytes("iso-8859-1"), "UTF-8");
					Tpw = request.getParameter("Tpw");
					sql = "insert into Teacher  values('" + Tno + "','" + Tname + "','" + Tpw + "')";
					System.out.println(sql);
				} else if (st.equals("���ʵ��γ�")) {
					Cno = request.getParameter("Cno");
					Cname = new String(request.getParameter("Cname").getBytes("iso-8859-1"), "UTF-8");
					Ctime = new String(request.getParameter("Ctime").getBytes("iso-8859-1"), "UTF-8");
					Cplace = new String(request.getParameter("Cplace").getBytes("iso-8859-1"), "UTF-8");
					Tno = request.getParameter("Tno");
					sql = "insert into Course  values('" + Cno + "','" + Cname + "','" + Ctime + "','" + Cplace + "','"
							+ Tno + "')";
					System.out.println(sql);
				} else if (st.equals("��ӳɼ���")) {
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
					if (name.equals("����"))
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
							+ "\r\n" + "<body leftmargin=\"150\">\r\n" + "<a><button>��ӳɹ�</button></a> " + "</body>\r\n"
							+ "</html>");
				} else
					out.println("<a><button>�ü�¼�Ѵ��ڣ�</button></a>");
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}

		// ����ManeModify������
		if (type.equals("ManeModify")) {
			st = new String(request.getParameter("radio").getBytes("iso-8859-1"), "UTF-8");// ���Ĵ�ֵ��������
			try {
				if (st.equals("�޸�ѧ����Ϣ")) {
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
							out.println("<a><button>�ü�¼�����ڣ�</button></a>");
					}
				} else if (st.equals("�޸Ľ�ʦ��Ϣ")) {
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
							out.println("<a><button>�ü�¼�����ڣ�</button></a>");
					}
				} else if (st.equals("�޸�ʵ��γ�")) {
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
							out.println("<a><button>�ü�¼�����ڣ�</button></a>");
					}
				} else if (st.equals("�޸ĳɼ���")) {
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
							out.println("<a><button>�ü�¼�����ڣ�</button></a>");
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
							if (name.equals("����"))
								sql = "Update TSC  set attence=1 where Sno='" + Sno + "' and Tno='" + Tno
										+ "' and Cno='" + Cno + "'";
							else
								sql = "Update TSC  set attence=0 where Sno='" + Sno + "' and Tno='" + Tno
										+ "' and Cno='" + Cno + "'";
						} else
							out.println("<a><button>�ü�¼�����ڣ�</button></a>");
					}
				}
				System.out.println(sql);
				stmt = (Statement) con.createStatement();
				if (stmt.executeUpdate(sql) == 1) {
					out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + "<head>\r\n"
							+ "<meta  charset=\"utf-8\" />\r\n" + "<style type=\"text/css\">\r\n" + "body {\r\n"
							+ "	background-image: url(image1.jpg);\r\n" + "}\r\n" + "</style>\r\n" + "</head>\r\n"
							+ "\r\n" + "<body leftmargin=\"150\">\r\n" + "<a><button>�޸ĳɹ�!!!</button></a> "
							+ "</body>\r\n" + "</html>");
				}
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}
		// ����ManeDelete������
		if (type.equals("ManeDelete")) {
			st = new String(request.getParameter("radio").getBytes("iso-8859-1"), "UTF-8");// ���Ĵ�ֵ��������
			try {
				if (st.equals("ɾ��ѧ����Ϣ")) {
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
							out.println("<a><button>�ü�¼�����ڣ�</button></a>");
					}
				} else if (st.equals("ɾ����ʦ��Ϣ")) {
					Tno = request.getParameter("Tno");
					sql = "select count(*) count from  Teacher  where Tno='" + Tno + "'";
					stmt = (Statement) con.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next()) {
						int sum = rs.getInt("count");
						if (sum == 1)
							sql = "delete from Teacher  where Tno='" + Tno + "'";
						else
							out.println("<a><button>�ü�¼�����ڣ�</button></a>");
					}
				} else if (st.equals("ɾ��ʵ��γ�")) {
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
							out.println("<a><button>�ü�¼�����ڣ�</button></a>");
					}
				} else if (st.equals("ɾ���ɼ���")) {
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
							out.println("<a><button>�ü�¼�����ڣ�</button></a>");
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
							out.println("<a><button>�ü�¼�����ڣ�</button></a>");
					}
				}
				System.out.println(sql);
				stmt = (Statement) con.createStatement();
				if (stmt.executeUpdate(sql) == 1) {
					out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + "<head>\r\n"
							+ "<meta  charset=\"utf-8\" />\r\n" + "<style type=\"text/css\">\r\n" + "body {\r\n"
							+ "	background-image: url(image1.jpg);\r\n" + "}\r\n" + "</style>\r\n" + "</head>\r\n"
							+ "\r\n" + "<body leftmargin=\"150\">\r\n" + "<a><button>ɾ���ɹ�!!!</button></a> "
							+ "</body>\r\n" + "</html>");
				}
				stmt.close();
				con.close();
			} catch (Exception e1) {
				System.out.println(e1);
			}
		}
		// ����ManeLook������
		if (type.equals("ManeLook")) {
			st = new String(request.getParameter("radio").getBytes("iso-8859-1"), "UTF-8");// ���Ĵ�ֵ��������
			if (st.equals("��ѯѧ����Ϣ"))
				sql = "select *  from  Student";
			else if (st.equals("��ѯ��ʦ��Ϣ"))
				sql = "select *  from Teacher";
			else if (st.equals("��ѯʵ��γ�"))
				sql = "select *  from Course";
			else if (st.equals("��ѯ�ɼ���"))
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

				if (st.equals("��ѯѧ����Ϣ")) {
					out.println("  <h2>��ѯѧ����Ϣ</h2>\r\n" + "  <table width=\"542\" height=\"54\" border=\"0\" >\r\n"
							+ "    <tbody  align=\"center\">\r\n" + "      <tr   bgcolor=\"#E0EFEF\">\r\n"
							+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">ѧ����</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">ѧ������</td>\r\n"
							+ "        <td width=\"141\" align=\"center\" valign=\"middle\">����</td>\r\n"
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
				} else if (st.equals("��ѯ��ʦ��Ϣ")) {
					out.println("  <h2>��ѯ��ʦ��Ϣ</h2>\r\n" + "  <table width=\"542\" height=\"54\" border=\"0\" >\r\n"
							+ "    <tbody  align=\"center\">\r\n" + "      <tr   bgcolor=\"#E0EFEF\">\r\n"
							+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">��ʦ���</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">��ʦ����</td>\r\n"
							+ "        <td width=\"141\" align=\"center\" valign=\"middle\">����</td>\r\n"
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
				} else if (st.equals("��ѯʵ��γ�")) {

					out.println("  <h2>��ѯʵ��γ���Ϣ  </h2>\r\n" + "  <table width=\"542\" height=\"54\" border=\"0\" >\r\n"
							+ "    <tbody  align=\"center\">\r\n" + "      <tr   bgcolor=\"#E0EFEF\">\r\n"
							+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">�γ̺�</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">�γ���</td>\r\n"
							+ "        <td width=\"141\" align=\"center\" valign=\"middle\">�Ͽ�ʱ��</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">�Ͽεص�</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">�Ͽν�ʦ</td>\r\n"
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
				} else if (st.equals("��ѯ�ɼ���")) {
					out.println("  <h2>��ѯ�ɼ���</h2>\r\n" + "  <table width=\"542\" height=\"54\" border=\"0\" >\r\n"
							+ "    <tbody  align=\"center\">\r\n" + "      <tr   bgcolor=\"#E0EFEF\">\r\n"
							+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">ѧ����</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">�γ̺�</td>\r\n"
							+ "        <td width=\"141\" align=\"center\" valign=\"middle\">�ɼ�</td>\r\n"
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
					out.println("  <h2>�鿴�������</h2>\r\n" + "  <table width=\"542\" height=\"54\" border=\"0\" >\r\n"
							+ "    <tbody  align=\"center\">\r\n" + "      <tr   bgcolor=\"#E0EFEF\">\r\n"
							+ "        <td width=\"129\" height=\"50\" align=\"center\" valign=\"middle\">��ʦ���</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">ѧ����</td>\r\n"
							+ "        <td width=\"141\" align=\"center\" valign=\"middle\">�γ̺�</td>\r\n"
							+ "        <td width=\"127\" align=\"center\" valign=\"middle\">����״̬</td>\r\n"
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
				// " <a href=\"bg.jsp\"><button>����</button></a>\r\n" +
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
