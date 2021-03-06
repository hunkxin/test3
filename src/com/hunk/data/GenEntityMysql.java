package com.hunk.data;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
 
public class GenEntityMysql {
	
	private String packageOutPath = "com.user.entity";//指定实体生成所在包的路径
	private String authorName = "hunk xin";//作者名字
	private String tablename = "";//表名
	private String[] colnames; // 列名数组
	private String[] colTypes; //列名类型数组
	private int[] colSizes; //列名大小数组
	private boolean f_util = false; // 是否需要导入包java.util.*
	private boolean f_sql = false; // 是否需要导入包java.sql.*
    
    //数据库连接
	private static final String URL ="jdbc:mysql://192.168.1.128:3306/FSWccc?" +
        "user=freeswitch&password=freeswitch";
	private static final String NAME = "freeswitch";
	private static final String PASS = "freeswitch";
	private static final String DRIVER ="com.mysql.jdbc.Driver";
	
	private static void loaddriver(){
		//System.out.println("-------- MySQL "
        //+ "JDBC Connection Testing ------------");
		try {

			Class.forName("com.mysql.jdbc.Driver");
		
		} catch (Exception e) {
		
			System.out.println("Where is your Mysql JDBC Driver? "
		            + "Include in your library path!");
			e.printStackTrace();
		
		}
		//System.out.println("Mysql JDBC Driver Registered!");
	}
 
	/*
	 * 构造函数
	 */
	public GenEntityMysql(String tbname,String sqlcdt){
    	//创建连接
    	Connection con = null;
		//查要生成实体类的表
    	String sql = "" + sqlcdt;
    	System.out.println(sql);
    	PreparedStatement pStemt = null;
    	ResultSetMetaData rsmd = null;
    	this.tablename = tbname;
    	try {
    		loaddriver();
    		con = DriverManager.getConnection(URL);
			pStemt = con.prepareStatement(sql);
			rsmd = pStemt.getMetaData();
			int size = rsmd.getColumnCount();	//统计列
			colnames = new String[size];
			colTypes = new String[size];
			colSizes = new int[size];
			for (int i = 0; i < size; i++) {
				colnames[i] = rsmd.getColumnName(i + 1);
				colTypes[i] = rsmd.getColumnTypeName(i + 1);
				
				if(colTypes[i].equalsIgnoreCase("datetime")){
					f_util = true;
				}
				if(colTypes[i].equalsIgnoreCase("image") || colTypes[i].equalsIgnoreCase("text")){
					f_sql = true;
				}
				colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
			}
			
			String content = parse(colnames,colTypes,colSizes);
			//System.out.println(content);
			
			try {
				//File directory = new File("");
				//System.out.println("绝对路径："+directory.getAbsolutePath());
				//System.out.println("相对路径："+directory.getCanonicalPath());
//				String path=this.getClass().getResource("").getPath();
//				
//				System.out.println(path);
//				System.out.println("src/?/"+path.substring(path.lastIndexOf("/com/", path.length())) );
//				String outputPath = directory.getAbsolutePath()+ "/src/"+path.substring(path.lastIndexOf("/com/", path.length()), path.length()) + initcap(tablename) + ".java";
				//String outputPath = directory.getAbsolutePath()+ "/src/"+this.packageOutPath.replace(".", "/")+"/"+initcap(tablename) + ".java";
				String outputPath = "E:/PM/JAVA/workspace/test3"+ "/src/"+this.packageOutPath.replace(".", "/")+"/"+initcap(tbname) + ".java";
				System.out.println(outputPath);
				FileWriter fw = new FileWriter(outputPath);
				PrintWriter pw = new PrintWriter(fw);
				
				pw.println(content);
				pw.flush();
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(pStemt!=null)
			{
				try {
					pStemt.close();
					pStemt = null;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(con!=null)
			{
				try {
					con.close();
					con = null;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
 
	/**
	 * 功能：生成实体类主体代码
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @return
	 */
	private String parse(String[] colnames, String[] colTypes, int[] colSizes) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("package " + this.packageOutPath + ";\r\n");
		sb.append("\r\n");
		//判断是否导入工具包
		if(f_util){
			sb.append("import java.util.Date;\r\n");
		}
		if(f_sql){
			sb.append("import java.sql.*;\r\n");
		}
		sb.append("\r\n");
		//注释部分
		sb.append("   /**\r\n");
		sb.append("    * "+tablename+" 实体类\r\n");
		sb.append("    * "+new Date()+" "+this.authorName+"\r\n");
		sb.append("    */ \r\n");
		//实体部分
		sb.append("\r\n\r\npublic class " + initcap(tablename) + "{\r\n");
		processAllAttrs(sb);//属性
		processAllMethod(sb);//get set方法
		
		processselsql(sb);
		processinssql(sb);
		processupdsql(sb);
		processres(sb);
		
		sb.append("}\r\n");
		
    	//System.out.println(sb.toString());
		return sb.toString();
	}
	
	/**
	 * 功能：生成所有属性
	 * @param sb
	 */
	private void processAllAttrs(StringBuffer sb) {
		
		for (int i = 0; i < colnames.length; i++) {
			//sb.append("\tprivate " + sqlType2JavaType(colTypes[i]) + " " + colnames[i] + ";\r\n");
			sb.append("\tprivate " + "String" + " " + colnames[i] + ";\r\n");
		}
		
	}
 
	/**
	 * 功能：生成所有方法
	 * @param sb
	 */
	private void processAllMethod(StringBuffer sb) {
		
		for (int i = 0; i < colnames.length; i++) {
			//sb.append("\tpublic void set" + initcap(colnames[i]) + "(" + sqlType2JavaType(colTypes[i]) + " " + 
			sb.append("\tpublic void set" + initcap(colnames[i]) + "(" + "String" + " " +
					colnames[i] + "){\r\n");
			sb.append("\t\tthis." + colnames[i] + "=" + colnames[i] + ";\r\n");
			sb.append("\t}\r\n");
			//sb.append("\tpublic " + sqlType2JavaType(colTypes[i]) + " get" + initcap(colnames[i]) + "(){\r\n");
			sb.append("\tpublic " + "String" + " get" + initcap(colnames[i]) + "(){\r\n");
			sb.append("\t\treturn " + colnames[i] + ";\r\n");
			sb.append("\t}\r\n");
		}
		
	}
	
	private void processselsql(StringBuffer sb){
		sb.append("\tprivate String selsql = \"select \"\r\n");
		for (int i = 0; i < colnames.length; i++) {
			sb.append("\t\t\u002B\"`" + colnames[i] + "`");
			if(i<colnames.length-1)
				sb.append(",\"\r\n");
			else
				sb.append("\"\r\n");
		}
		sb.append("\t\t\u002B\" from `tablename`\";\r\n\r\n");
	}
	
	private void processinssql(StringBuffer sb){
		sb.append("\tprivate String inssql = \"insert into tablename(\"\r\n");
		for (int i = 0; i < colnames.length; i++) {
			sb.append("\t\t\u002B\"`" + colnames[i] + "`");
			if(i<colnames.length-1)
				sb.append(",");
			sb.append("\"\r\n");
		}
		sb.append("\t\t\u002B\") values(\"\r\n");
		for (int i = 0; i < colnames.length; i++) {
			if(sqlType2JavaType(colTypes[i]).equals("String"))
				sb.append("\t\t\u002B\"\'\"\u002B"+"content.get" + initcap(colnames[i]) + "()\u002B\"\'");
			else
				sb.append("\t\t\u002B"+"content.get" + initcap(colnames[i]) + "()\u002B\"");
			if(i<colnames.length-1)
				sb.append(",\"");
			else
				sb.append("\"");
			sb.append("\r\n");
		}
		sb.append("\t\t\u002B\")\";\r\n\r\n");
	}
	
	private void processupdsql(StringBuffer sb){
		sb.append("\tprivate String updsql = \"update tablename set \"\r\n");
		for (int i = 0; i < colnames.length; i++) {
			if(sqlType2JavaType(colTypes[i]).equals("String"))
				sb.append("\t\t\u002B\"`" + colnames[i] + "` =  \'\"\u002Bcontent." + initcap(colnames[i]) + "()\u002B\"\'");
			else
				sb.append("\t\t\u002B\"`" + colnames[i] + "` =  \"\u002Bcontent." + initcap(colnames[i]) + "()\u002B\"");
			if(i<colnames.length-1)
				sb.append(",\"");
			else
				sb.append("\";");
			sb.append("\r\n");
		}
		sb.append("\r\n");
	}
	
	private void processres(StringBuffer sb){
		sb.append("\tT content = new T(\r\n");
		for (int i = 0; i < colnames.length; i++) {
			sb.append("\t\tres.getString(\"" + colnames[i] + "\")");
			if(i<colnames.length-1)
				sb.append(",");
			else
				sb.append(");");
			sb.append("\r\n");
		}
		sb.append("\r\n");
	}
	
	/**
	 * 功能：将输入字符串的首字母改成大写
	 * @param str
	 * @return
	 */
	private String initcap(String str) {
		
		char[] ch = str.toCharArray();
		if(ch[0] >= 'a' && ch[0] <= 'z'){
			ch[0] = (char)(ch[0] - 32);
		}
		
		return new String(ch);
	}
 
	/**
	 * 功能：获得列的数据类型
	 * @param sqlType
	 * @return
	 */
	private String sqlType2JavaType(String sqlType) {
		
		if(sqlType.equalsIgnoreCase("bit")){
			return "boolean";
		}else if(sqlType.equalsIgnoreCase("tinyint")){
			return "byte";
		}else if(sqlType.equalsIgnoreCase("smallint")){
			return "short";
		}else if(sqlType.equalsIgnoreCase("int")){
			return "int";
		}else if(sqlType.equalsIgnoreCase("bigint")){
			return "long";
		}else if(sqlType.equalsIgnoreCase("float")){
			return "float";
		}else if(sqlType.equalsIgnoreCase("decimal") || sqlType.equalsIgnoreCase("numeric") 
				|| sqlType.equalsIgnoreCase("real") || sqlType.equalsIgnoreCase("money") 
				|| sqlType.equalsIgnoreCase("smallmoney")){
			return "double";
		}else if(sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char") 
				|| sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar") 
				|| sqlType.equalsIgnoreCase("text")){
			return "String";
		}else if(sqlType.equalsIgnoreCase("datetime")){
			return "Date";
		}else if(sqlType.equalsIgnoreCase("image")){
			return "Blod";
		}
		
		return null;
	}
	
}