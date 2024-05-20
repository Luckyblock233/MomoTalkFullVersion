package all.database;

import all.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection;//数据库连接时静态
    public static Connection getConnection(){

        if (connection == null) {
            //数据库驱动加载
            try {
                Class.forName(Config.DRIVER);
                connection = DriverManager.getConnection(Config.URL);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return connection;
    }
}
